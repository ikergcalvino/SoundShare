package com.muei.soundshare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.muei.soundshare.databinding.ActivityMainBinding
import com.muei.soundshare.util.ShakeDetector
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var topNav: MaterialToolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController

    private var isRecording = false
    private var isAnalyzing = false
    private var isDialogShowing = false
    private var recorder: MediaRecorder? = null
    private var progressDialog: AlertDialog? = null
    private val client = OkHttpClient()

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var shakeDetector: ShakeDetector

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startRecording(File(getExternalFilesDir("Music"), "song.mp3"))
        } else {
            showDialog("Permission denied", "Cannot record audio without permissions")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topNav = binding.topNav
        bottomNav = binding.bottomNav
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_notifications,
                R.id.navigation_map
            )
        )

        topNav.setupWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_profile, R.id.navigation_edit_profile -> {
                    topNav.subtitle = destination.label
                    binding.buttonShazam.visibility = View.GONE
                    topNav.menu.clear()
                }

                else -> {
                    topNav.title = getString(R.string.app_name)
                    topNav.subtitle = destination.label
                    if (topNav.menu.size() == 0) {
                        topNav.inflateMenu(R.menu.top_nav_menu)
                    }
                    binding.buttonShazam.visibility = View.VISIBLE
                }
            }
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    Log.d("SoundShare", "Home clicked")
                    navController.navigate(R.id.navigation_home)
                    true
                }

                R.id.navigation_search -> {
                    Log.d("SoundShare", "Search clicked")
                    navController.navigate(R.id.navigation_search)
                    true
                }

                R.id.navigation_post -> {
                    Log.d("SoundShare", "Post clicked")
                    val postIntent = Intent(this@MainActivity, PostActivity::class.java)
                    startActivity(postIntent)
                    overridePendingTransition(R.anim.slide_up, 0)
                    true
                }

                R.id.navigation_notifications -> {
                    Log.d("SoundShare", "Notifications clicked")
                    navController.navigate(R.id.navigation_notifications)
                    true
                }

                R.id.navigation_map -> {
                    Log.d("SoundShare", "Map clicked")
                    navController.navigate(R.id.navigation_map)
                    true
                }

                else -> false
            }
        }

        topNav.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_profile -> {
                    Log.d("SoundShare", "Profile clicked")
                    navController.navigate(R.id.navigation_profile)
                    true
                }

                else -> false
            }
        }

        binding.buttonShazam.setOnClickListener {
            handleShazamClick()
        }

        // Initialize the sensor manager and accelerometer sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        shakeDetector = ShakeDetector {
            handleShazamClick()
        }

        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun handleShazamClick() {
        if (!isAnalyzing && !isDialogShowing) {
            if (isRecording) {
                stopRecording()
            } else {
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                } else {
                    startRecording(File(getExternalFilesDir("Music"), "song.mp3"))
                    val overlayLoading =
                        layoutInflater.inflate(R.layout.overlay_loading, null).apply {
                            setBackgroundColor(
                                ContextCompat.getColor(
                                    this@MainActivity, android.R.color.transparent
                                )
                            )
                            isVisible = true
                        }
                    progressDialog =
                        MaterialAlertDialogBuilder(this).setTitle("Analyzing audio...")
                            .setView(overlayLoading).setCancelable(false).show()

                    isAnalyzing = true

                    GlobalScope.launch(Dispatchers.Main) {
                        delay(9000) // 9s
                        if (isRecording) {
                            stopRecording()
                            sendAudioToShazam()
                        }
                    }
                }
            }
        }
    }

    private fun startRecording(outputFile: File) {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)

            try {
                prepare()
                start()
                isRecording = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
        }
    }

    private fun sendAudioToShazam() {
        val file = File(getExternalFilesDir("Music"), "song.mp3")

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
            "upload_file",
            "song.mp3",
            file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        ).build()

        val request = Request.Builder().url("https://shazam-api6.p.rapidapi.com/shazam/recognize/")
            .post(requestBody)
            .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
            .addHeader("X-RapidAPI-Key", "e133776cdamsh86b926a2158ff65p16da0cjsnb8c64f8fcee1")
            .addHeader("X-RapidAPI-Host", "shazam-api6.p.rapidapi.com").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showDialog("Error", "Error sending audio to Shazam")
                    progressDialog?.dismiss()
                    isAnalyzing = false
                    isDialogShowing = true
                }
                Log.e("SoundShare", "Failed to send audio to Shazam", e)
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    progressDialog?.dismiss()
                    if (response.isSuccessful) {
                        response.body?.string()?.let { responseBody ->
                            try {
                                val jsonResponse = JSONObject(responseBody)
                                val track = jsonResponse.getJSONObject("track")
                                val title = track.getString("title")
                                val subtitle = track.getString("subtitle")
                                val images = track.getJSONObject("images")
                                val coverArt = images.getString("coverart")

                                showTrackDialog(title, subtitle, coverArt)
                            } catch (e: Exception) {
                                showDialog("Error", "It has not been possible to analyze the song")
                                Log.e("SoundShare", "Error parsing response", e)
                            }
                        }
                    } else {
                        showDialog("Error", "Error response from Shazam: ${response.message}")
                        Log.e("SoundShare", "Error response from Shazam: ${response.message}")
                    }
                    isAnalyzing = false
                    isDialogShowing = true
                }
            }
        })
    }

    private fun showDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this).setTitle(title).setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                isDialogShowing = false
            }.show()
    }

    private fun showTrackDialog(title: String, subtitle: String, coverArt: String) {
        val dialogView = layoutInflater.inflate(R.layout.layout_song, null)
        val songImage = dialogView.findViewById<ShapeableImageView>(R.id.song_image)
        val songName = dialogView.findViewById<MaterialTextView>(R.id.song_name)
        val artistName = dialogView.findViewById<MaterialTextView>(R.id.artist_name)

        Glide.with(this).load(coverArt).into(songImage)
        songName.text = title
        artistName.text = subtitle

        MaterialAlertDialogBuilder(this).setTitle("Title").setView(dialogView)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                isDialogShowing = false
            }.setNeutralButton(getString(R.string.post)) { dialog, _ ->
                dialog.dismiss()
                isDialogShowing = false
            }.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                isDialogShowing = false
            }.show()
    }

}