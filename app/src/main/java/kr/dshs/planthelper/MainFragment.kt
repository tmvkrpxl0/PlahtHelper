package kr.dshs.planthelper

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.net.toUri
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
import kr.dshs.planthelper.data.PlantProfile
import kr.dshs.planthelper.databinding.AddPlantPopupBinding
import kr.dshs.planthelper.databinding.FragmentMainBinding
import java.util.*

class MainFragment : Fragment() {
    var plantSelectPopup: View? = null
    private lateinit var mainActivity: MainActivity
    var tookPicture = false
    var chosenPlantAcademic: PlantAcademic? = null

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

            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            val popupWindow = PopupWindow(plantSelectPopup, width, height, true)

            val currentNightMode = mainActivity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                popupBinding.root.setBackgroundColor(Color.argb(255, 27, 27, 27))
            }

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
            popupWindow.dimBehind()

            popupBinding.cancelbutton.setOnClickListener {
                popupWindow.dismiss()
            }

            popupWindow.setOnDismissListener {
                plantSelectPopup = null
                tookPicture = false
                mainActivity.capturedImage = null
                chosenPlantAcademic = null
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

            popupBinding.setplantdatebutton.setOnClickListener {
                val calender = Calendar.getInstance()
                val year = calender.get(Calendar.YEAR)
                val month = calender.get(Calendar.MONTH)
                val day = calender.get(Calendar.DAY_OF_MONTH)
                DatePickerDialog(mainActivity, { datePicker: DatePicker, newYear: Int, newMonth: Int, newDay: Int ->
                    popupBinding.setplantdatebutton.text = "$newYear-$newMonth-$newDay"
                }, year, month, day).show()
            }

            popupBinding.addbutton.setOnClickListener addButtonHandler@ {
                if (chosenPlantAcademic == null) {
                    Toast.makeText(mainActivity, "먼저 식물을 선택해주세요!", Toast.LENGTH_LONG).show()
                    return@addButtonHandler
                }
                val plantedDate = if (!popupBinding.setplantdatebutton.text.contains("-")) null else {
                    val text = popupBinding.setplantdatebutton.text
                    val split = text.split("-")
                    val year = split[0].toInt()
                    val month = split[1].toInt()
                    val day = split[2].toInt()
                    Calendar.getInstance().apply{ set(year, month, day) }.time
                }

                val customName = popupBinding.customnamefield.text.toString().ifEmpty { null }
                val profile = PlantProfile(chosenPlantAcademic!!, customName, popupBinding.pickperiod.value, plantedDate, mainActivity.capturedImage)
                adapter.add(profile)
                popupWindow.dismiss()
            }

            popupBinding.searchbutton.setOnClickListener searchHandler@ {
                val result: PlantAcademic? = if (popupBinding.searchwithname.text.isNotEmpty()) {
                    val tempResult = plantAcademics.find { academic ->
                        academic.name.startsWith(popupBinding.searchwithname.text)
                    }
                    if (tempResult == null) {
                        Toast.makeText(mainActivity, "지정하신 식물을 찾을 수 없습니다!", Toast.LENGTH_SHORT).show()
                        return@searchHandler
                    } else {
                        tempResult
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
                            val scientificName = plantInfo.results.first().species.scientificNameWithoutAuthor

                            Log.i("name", scientificName)
                            val findResult = plantAcademics.filter { it.scientificName != null }.find { it.scientificName!! == scientificName }
                            if (findResult == null) {
                                Toast.makeText(mainActivity, "등록되지 않은 식물입니다!", Toast.LENGTH_LONG).show()
                                return@runBlocking null
                            }
                            popupBinding.searchwithname.setText(findResult.name)
                            return@runBlocking findResult
                        }
                    }
                } else {
                    Toast.makeText(mainActivity, "식물 이름 또는 식물 사진을 지정해주세요!", Toast.LENGTH_LONG).show()
                    return@searchHandler
                }

                if (result != null) {
                    chosenPlantAcademic = result
                    result.wateringPeriod?.let {
                        popupBinding.pickperiod.minValue = it.start.inWholeDays.toInt()
                        popupBinding.pickperiod.maxValue = it.endInclusive.inWholeDays.toInt()
                        popupBinding.pickperiod.isEnabled = true
                    }
                    popupBinding.setplantdatebutton.isEnabled = true
                    popupBinding.customnamefield.isEnabled = true
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
