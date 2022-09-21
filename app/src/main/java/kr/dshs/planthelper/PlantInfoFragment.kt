package kr.dshs.planthelper

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kr.dshs.planthelper.databinding.FragmentPlantInfoBinding

const val profileIndexKey: String = "kr.dshs.planthelper.profileindex"

class PlantInfoFragment : Fragment() {
    private lateinit var binding: FragmentPlantInfoBinding
    private val mainActivity: MainActivity by lazy { requireActivity() as MainActivity }
    val args: PlantInfoFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantInfoBinding.inflate(inflater, container, false)

        val navController = findNavController()

        val index = args.profileindex
        val plantProfile = plantProfiles[index]

        if (plantProfile.photoFile?.exists() == true) binding.imageButton.setImageURI(plantProfile.photoFile.toUri())
        binding.profileplantname.text = plantProfile.getAnyName()
        binding.profileacademicname.text =
            if (plantProfile.customName != null) plantProfile.academic.name else ""
        binding.profileplanteddate.text = "심은 날짜:" + if (plantProfile.plantDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = plantProfile.plantDate
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        } else "알 수 없읍"
        binding.profilewateringperiod.text = "${plantProfile.wateringPeriod}일에 한번 물주기"
        binding.profiledescription.text = plantProfile.academic.etcDescription

        binding.deleteprofilebutton.setOnClickListener {
            val alarmManager = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, AlarmBroadcast::class.java)
            intent.putExtra(profileIndexKey, plantProfiles.indexOf(plantProfile))
            val pending = PendingIntent.getBroadcast(context, plantProfile.id, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.cancel(pending)

            plantProfiles.remove(plantProfile)
            navController.navigate(R.id.MainFragment)
        }
        return binding.root
    }
}