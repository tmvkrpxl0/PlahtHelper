package kr.dshs.planthelper

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.dshs.planthelper.data.PlantAcademic
import kr.dshs.planthelper.databinding.ActivityMainBinding
import java.io.File
import java.util.*


lateinit var plantAcademics: List<PlantAcademic>

class MainActivity : AppCompatActivity() {
    lateinit var mainFragment: MainFragment
    var capturedImage: File? = null

    @Suppress("DeferredResultUnused")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileRoot = filesDir

        runBlocking {
            launch(Dispatchers.IO) {
                val contents =
                    resources.openRawResource(R.raw.academic_plants).bufferedReader().readText()
                plantAcademics = Json.decodeFromString(contents)
            }
            createChannel()
            launch {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        1
                    )
                }
            }

            launch(Dispatchers.IO) {
                plantNetKey = resources.openRawResource(R.raw.plantnetkey).reader().readText()
            }

            launch {
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)
                setSupportActionBar(binding.toolbar)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun createChannel() {
        val name = "Plant Helper Alarm Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun requestNewPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, NEW_PHOTO_REQUEST)
        }
    }

    fun requestGalleryPhoto() {
        val contents = Intent(Intent.ACTION_GET_CONTENT)
        contents.type = "image/*"

        val pick = Intent(Intent.ACTION_PICK)
        pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

        val chooserIntent = Intent.createChooser(contents, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pick))

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            @Suppress("DEPRECATION")
            startActivityForResult(chooserIntent, GALLERY_PHOTO_REQUEST)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode != RESULT_OK) {
            val trace = Exception().stackTraceToString()
            Log.d("TRACE", trace)
            return
        }

        runBlocking {
            if (requestCode == GALLERY_PHOTO_REQUEST && intent!!.data != null) {
                val imageUri = intent.data!!
                launch {
                    mainFragment.plantSelectPopup!!.findViewById<ImageButton>(R.id.camerabutton).setImageURI(imageUri)
                }
                mainFragment.tookPicture = true

                launch(Dispatchers.IO) {
                    val copiedFile = filesDir.resolve("${UUID.randomUUID()}.${getMimeType(imageUri)}")
                    contentResolver.openInputStream(imageUri)!!.buffered().use { input ->
                        copiedFile.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }
                    capturedImage = copiedFile
                }
            } else if (requestCode == NEW_PHOTO_REQUEST && intent!!.hasExtra("data")) {
                launch {
                    mainFragment.plantSelectPopup!!.findViewById<ImageButton>(R.id.camerabutton).setImageBitmap(intent.extras!!.get("data") as Bitmap)
                }
                mainFragment.tookPicture = true

                launch(Dispatchers.IO) {
                    val tempFile = filesDir.resolve("${UUID.randomUUID()}.png")
                    tempFile.outputStream().buffered().use {
                        (intent.extras!!.get("data") as Bitmap).compress(Bitmap.CompressFormat.PNG, 100, it)
                    }
                    capturedImage = tempFile
                }
            } else {
                throw IllegalStateException("Code: $requestCode, has data from extra: ${intent?.hasExtra("data")}, data field: ${intent?.data}")
            }
        }

    }

    fun getMimeType(uri: Uri): String? {
        //Check uri format to avoid null
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())
        }
        return extension
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    lateinit var plantNetKey: String

    companion object {
        private const val NEW_PHOTO_REQUEST = 128946
        private const val GALLERY_PHOTO_REQUEST = 2690854
    }
}