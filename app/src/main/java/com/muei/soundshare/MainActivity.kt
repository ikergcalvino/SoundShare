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
import com.acrcloud.rec.ACRCloudClient
import com.acrcloud.rec.ACRCloudConfig
import com.acrcloud.rec.ACRCloudResult
import com.acrcloud.rec.IACRCloudListener
import com.acrcloud.rec.utils.ACRCloudLogger
import com.android.volley.BuildConfig
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.muei.soundshare.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(), IACRCloudListener {

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
    private var mClient: ACRCloudClient? = null


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
            initAcrcloud()
            startRecognition()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.dismiss()
        progressDialog = null
    }


    private fun initAcrcloud() {
        val config = ACRCloudConfig()

        config.acrcloudListener = this
        config.context = this

        config.host = "identify-eu-west-1.acrcloud.com"
        config.accessKey = "a677b38c71a0a38b3092106b31f758ec"
        config.accessSecret = "ENNzTVtGpr0y1tvnTgWjYhkiyQQyu98oZdI9ycAN"

        config.recorderConfig.rate = 8000
        config.recorderConfig.channels = 1

        mClient = ACRCloudClient()
        if (BuildConfig.DEBUG) {
            ACRCloudLogger.setLog(true)
        }
        mClient!!.initWithConfig(config)
    }

    private fun startRecognition() {
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
        }
        mClient?.let {
            if (it.startRecognize()) {
                // Mostrar diálogo de progreso
                progressDialog = AlertDialog.Builder(this).apply {
                    setTitle("Analizando audio...")
                    setView(ProgressBar(this@MainActivity))
                    setCancelable(false)
                }.create()
                progressDialog?.show()
            } else {
                Log.e("SoundShare", "Init error")
            }
        } ?: run {
            Log.e("SoundShare", "Client not ready")
        }
    }

    override fun onResult(acrResult: ACRCloudResult?) {
        acrResult?.let {
            Log.d("SoundShare", "ACRCloud result received: ${it.result}")
            handleResult(it.result)
        }
    }

    override fun onVolumeChanged(vol: Double) {
        Log.d("SoundShare", "Volume changed $vol")
    }

    private fun handleResult(acrResult: String) {
        // Parse the JSON response
        val jsonObject = JSONObject(acrResult)
        val metadata = jsonObject.getJSONObject("metadata")
        val humming = metadata.getJSONArray("humming")
        val firstResult = humming.getJSONObject(0)
        val songTitle = firstResult.getString("title")

        // Close the progress dialog
        progressDialog?.dismiss()

        // Show the song title in a new dialog
        AlertDialog.Builder(this).apply {
            setTitle("Song Identified")
            setMessage(songTitle)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }
}