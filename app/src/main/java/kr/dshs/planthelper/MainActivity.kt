package kr.dshs.planthelper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.activity.result.contract.ActivityResultContracts
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.serialization.responseObject
import com.github.kittinunf.result.Result
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kr.dshs.planthelper.data.*
import kr.dshs.planthelper.databinding.ActivityMainBinding
import java.util.*
import kotlin.time.Duration.Companion.days

lateinit var plantAcademic: List<PlantAcademic>

val plantProfiles = mutableListOf(
    PlantProfile(plantAcademic[0], null, Date(2022, 2, 1), Uri.parse("file://a.png"))
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contents = resources.openRawResource(R.raw.academic_plants).bufferedReader().readText()
        plantAcademic = Json.decodeFromString(contents)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        plantNetKey = resources.openRawResource(R.raw.plantnetkey).reader().readText()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun requestPhoto() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val photoFile = filesDir.resolve("$year-$month-$day-$hour-$minute-$second")

        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))

        resultLauncher.launch(photoIntent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photoFile = result.data!!.extras!!.get(MediaStore.EXTRA_OUTPUT)
            Fuel.post(
                path = "https://my-api.plantnet.org/v2/identify/all",
                parameters = listOf(
                    "api-key" to plantNetKey,
                    "images" to arrayOf(photoFile),
                    "organs" to "auto"
                )
            ).responseObject(Json { ignoreUnknownKeys = true }) { request: Request, response: Response, plantNetResult: Result<PlantNetResponse, FuelError> ->
                val plantInfo = plantNetResult.get()
                Toast.makeText(this, plantInfo.results[0].species[0].scientificNameWithoutAuthor, Toast.LENGTH_SHORT)
            }
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var plantNetKey: String
}