package kr.dshs.planthelper

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.dshs.planthelper.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.plants)!!

        val adapter = PlantProfileAdapter(requireActivity(), plantProfiles)

        listView.adapter = adapter

        val navController = findNavController()

        listView.setOnItemClickListener { parent: AdapterView<*>, clicked: View, position: Int, id: Long ->
            navController.navigate(R.id.PlantFragment)
        }

        val addButton = view.findViewById<FloatingActionButton>(R.id.addplant)

        addButton.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.add_plant_popup, null);

            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            val popupWindow = PopupWindow(popupView, width, height, true)

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
            popupWindow.dimBehind()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
