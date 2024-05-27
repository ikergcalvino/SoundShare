package com.muei.soundshare

import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.muei.soundshare.databinding.ActivityMainBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var topNav: MaterialToolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController
    private var isRecording = false
    private var progressDialog: AlertDialog? = null
    private var audioRecord: AudioRecord? = null
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
//    private lateinit var audioData: ByteArray

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
                R.id.navigation_profile -> {
                    topNav.subtitle = destination.label
                    binding.buttonShazam.visibility = View.GONE
                    binding.buttonLogOut.visibility = View.VISIBLE
                    topNav.menu.clear()
                }

                R.id.navigation_edit_profile -> {
                    topNav.subtitle = destination.label
                    binding.buttonShazam.visibility = View.GONE
                    binding.buttonLogOut.visibility = View.GONE
                    topNav.menu.clear()
                }

                else -> {
                    topNav.title = getString(R.string.app_name)
                    topNav.subtitle = destination.label
                    if (topNav.menu.size() == 0) {
                        topNav.inflateMenu(R.menu.top_nav_menu)
                    }
                    binding.buttonShazam.visibility = View.VISIBLE
                    binding.buttonLogOut.visibility = View.GONE
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

        binding.buttonLogOut.setOnClickListener {
            Log.d("SoundShare", "Log out button clicked")
            FirebaseAuth.getInstance().signOut()
            val mainIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        binding.buttonShazam.setOnClickListener {
            if (isRecording) {
                stopRecording()
                progressDialog?.dismiss()
//                sendAudioToShazam()
            } else {
                startRecording(File(getExternalFilesDir("Music"), "nueva_grabacion.mp3"))
                val progressDialog = AlertDialog.Builder(this).apply {
                    setTitle("Analizando audio...")
                    setView(ProgressBar(this@MainActivity))
                    setCancelable(false)
                }.create()
                progressDialog.show()
                this.progressDialog = progressDialog

                // Detener la grabación después de 10 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isRecording) {
                        stopRecording()
                        progressDialog.dismiss()
                       sendAudioToShazam()
                    }
                }, 10000) // 10 segundos
            }
        }
    }

    private var recorder: MediaRecorder? = null

    private fun startRecording(outputFile: File) {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar el permiso para grabar audio
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                200
            )
            return
        }

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
//        if (audioData.isEmpty()) {
//            Log.e("SoundShare", "No audio data recorded")
//            return
//        }

        val file = File(getExternalFilesDir("Music"), "nueva_grabacion.mp3") // Reemplaza "ruta_del_archivo" con la ubicación de tu archivo

        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_file", "nueva_grabacion.mp3",
                file.asRequestBody("application/octet-stream".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url("https://shazam-api6.p.rapidapi.com/shazam/recognize/")
            .post(requestBody)
            .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
            .addHeader("X-RapidAPI-Key", "e133776cdamsh86b926a2158ff65p16da0cjsnb8c64f8fcee1")
            .addHeader("X-RapidAPI-Host", "shazam-api6.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SoundShare", "Failed to send audio to Shazam", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("SoundShare", "Response from Shazam: $responseBody")
                } else {
                    Log.e("SoundShare", "Error response from Shazam: ${response.message}")
                }
            }
        })
    }
}
