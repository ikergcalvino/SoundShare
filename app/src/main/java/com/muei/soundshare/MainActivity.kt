package com.muei.soundshare

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muei.soundshare.databinding.ActivityMainBinding
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
    private var progressDialog: AlertDialog? = null

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
                    topNav.menu.clear()
                }

                R.id.navigation_edit_profile -> {
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
            if (isRecording) {
                stopRecording()
                progressDialog?.dismiss()
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
        val file = File(getExternalFilesDir("Music"), "nueva_grabacion.mp3")

        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "upload_file", "nueva_grabacion.mp3",
                file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
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
                runOnUiThread {
                    showDialog("Error", "Error al mandar el audio a Shazam")
                }
                Log.e("SoundShare", "Failed to send audio to Shazam", e)
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
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
                                showDialog("Error", "Error parsing response")
                                Log.e("SoundShare", "Error parsing response", e)
                            }
                        }
                    } else {
                        showDialog("Error", "Error response from Shazam: ${response.message}")
                        Log.e("SoundShare", "Error response from Shazam: ${response.message}")
                    }
                }
            }
        })
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showTrackDialog(title: String, subtitle: String, coverArt: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_track_info, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.coverArtImageView)
        val titleView = dialogView.findViewById<TextView>(R.id.titleTextView)
        val subtitleView = dialogView.findViewById<TextView>(R.id.subtitleTextView)

        titleView.text = title
        subtitleView.text = subtitle
        Glide.with(this).load(coverArt).into(imageView)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK", null)
            .show()
    }
}
