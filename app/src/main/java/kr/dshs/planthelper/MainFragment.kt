package kr.dshs.planthelper

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.serialization.responseObject
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kr.dshs.planthelper.data.PlantAcademic
import kr.dshs.planthelper.data.PlantNetResponse
import kr.dshs.planthelper.databinding.AddPlantPopupBinding
import kr.dshs.planthelper.databinding.FragmentMainBinding
import java.util.*

class MainFragment : Fragment() {
    var plantSelectPopup: View? = null
    private lateinit var mainActivity: MainActivity
    var tookPicture = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        mainActivity = requireActivity() as MainActivity
        mainActivity.mainFragment = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.plants)!!

        val adapter = PlantProfileAdapter(requireActivity(), plantProfiles)

        listView.adapter = adapter

        val navController = findNavController()

        listView.setOnItemClickListener { _: AdapterView<*>, _: View, _: Int, _: Long ->
            navController.navigate(R.id.PlantFragment)
        }

        val addButton = view.findViewById<FloatingActionButton>(R.id.addplant)

        addButton.setOnClickListener {
            plantSelectPopup = layoutInflater.inflate(R.layout.add_plant_popup, null)
            val popupBinding = AddPlantPopupBinding.bind(plantSelectPopup!!)
            plantSelectPopup!!.removeCallbacks {
                plantSelectPopup = null
                tookPicture = false
            }

            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            val popupWindow = PopupWindow(plantSelectPopup, width, height, true)

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
            popupWindow.dimBehind()

            popupBinding.cancelbutton.setOnClickListener {
                popupWindow.dismiss()
            }

            popupBinding.camerabutton.setOnClickListener {
                val mainActivity = requireActivity() as MainActivity

                val alert = AlertDialog.Builder(mainActivity)
                alert.run {
                    setMessage("이미지 가져올 방법")
                    setPositiveButton("카메라") { _, _ ->
                        mainActivity.requestNewPhoto()
                    }
                    setNegativeButton("갤러리") { _, _ ->
                        mainActivity.requestGalleryPhoto()
                    }
                    show()
                }
            }

            popupBinding.searchbutton.setOnClickListener searchHandler@ {
                var result: PlantAcademic

                if (popupBinding.searchwithname.text.length > 2) {
                    val tempResult = plantAcademic.find { academic ->
                        academic.name.startsWith(popupBinding.searchwithname.text)
                    }
                    if (tempResult == null) {
                        Toast.makeText(mainActivity, "지정하신 식물을 찾을 수 없습니다!", Toast.LENGTH_SHORT).show()
                        return@searchHandler
                    } else {
                        result = tempResult
                    }
                } else if (tookPicture) {
                    val fileDataPart = FileDataPart.from(mainActivity.capturedImage!!.absolutePath, name = "images")

                    runBlocking {
                        val plantNetResult = async (Dispatchers.IO) {
                            val response = Fuel.upload(
                                path = "https://my-api.plantnet.org/v2/identify/all?api-key=${mainActivity.plantNetKey}",
                                parameters = listOf("organs" to "auto"),
                                method = Method.POST
                            ).add(fileDataPart).responseObject<PlantNetResponse>()

                            if (response.third.component2() != null) {
                                Toast.makeText(mainActivity, "식물 정보를 사진을 이용해 조회하는데 실패했습니다!", Toast.LENGTH_LONG).show()
                                return@async null
                            } else {
                                return@async response.third.get()
                            }
                        }

                        plantNetResult.await()?.let { plantInfo ->
                            Toast.makeText(mainActivity, plantInfo.results.first().species.scientificNameWithoutAuthor, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    return@searchHandler
                }
            }
        }
    }

    private fun scheduleNotification(context: Context, binding: AddPlantPopupBinding) {
        val intent = Intent(context, AlarmBroadcast::class.java)
        val pending = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime(binding)

    }

    private fun getTime(binding: AddPlantPopupBinding): Long {
        val minute = binding.time.minute
        val hour = binding.time.hour

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        return calendar.timeInMillis
    }
}

fun PopupWindow.dimBehind() {
    val container = contentView.rootView
    val context = contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val p = container.layoutParams as WindowManager.LayoutParams
    p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    p.dimAmount = 0.3f
    wm.updateViewLayout(container, p)
}
