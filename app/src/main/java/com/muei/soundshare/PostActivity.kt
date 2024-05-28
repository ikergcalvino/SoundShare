package com.muei.soundshare

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muei.soundshare.databinding.ActivityPostBinding
import com.muei.soundshare.databinding.LayoutSongBinding
import com.muei.soundshare.entities.Post
import com.muei.soundshare.entities.Song
import com.muei.soundshare.services.SpotifyClient
import com.muei.soundshare.util.Constants
import com.muei.soundshare.util.ItemClickListener
import com.muei.soundshare.util.SongAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date

class PostActivity : AppCompatActivity(), ItemClickListener<Song>, KoinComponent {

    private lateinit var binding: ActivityPostBinding
    private lateinit var selectedSongId: String
    private lateinit var selectedSongLayout: LayoutSongBinding
    private lateinit var resultDialog: AlertDialog
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val firebase: FirebaseAuth by inject()
    private val firestore: FirebaseFirestore by inject()
    private val spotifyClient: SpotifyClient by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private var postLatitude: Double? = null
    private var postLongitude: Double? = null
    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionGranted = checkLocationPermission()

        if (sharedPreferences.getBoolean(Constants.LOCATION, false)) {
            binding.switchLocation.isChecked = true
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
            if (locationPermissionGranted) {
                getLastLocation()
            } else {
                requestLocationPermission()
            }
        } else {
            binding.switchLocation.isChecked = false
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
            postLatitude = null
            postLongitude = null
        }

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isLocationEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                if (!isLocationEnabled) {
                    MaterialAlertDialogBuilder(this).setTitle("Activar Ubicación")
                        .setMessage("Tu ubicación está desactivada. ¿Quieres activarla ahora?")
                        .setPositiveButton("Activar") { _, _ ->
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }.setNegativeButton("Cancelar") { _, _ ->
                            binding.switchLocation.isChecked = false
                        }.setOnCancelListener {
                            binding.switchLocation.isChecked = false
                        }.show()
                } else {
                    Log.d("SoundShare", "Location on")
                    binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
                    if (locationPermissionGranted) {
                        getLastLocation()
                    } else {
                        requestLocationPermission()
                    }
                }
            } else {
                Log.d("SoundShare", "Location off")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
                postLatitude = null
                postLongitude = null
            }
        }

        binding.selectSong.setOnClickListener {
            val songSearchView: View =
                this.layoutInflater.inflate(R.layout.layout_song_search, null)

            val textTrackName = songSearchView.findViewById<TextInputEditText>(R.id.text_track_name)

            val songResultView: View =
                this.layoutInflater.inflate(R.layout.layout_song_result, null)

            resultDialog =
                MaterialAlertDialogBuilder(this).setView(songResultView).setTitle("Search Results")
                    .setPositiveButton("OK", null).create()

            val searchDialog =
                MaterialAlertDialogBuilder(this).setView(songSearchView).setTitle("Song Search")
                    .setPositiveButton("Search") { _, _ ->
                        spotifyClient.searchSongs(textTrackName.text.toString()) { songs ->
                            runOnUiThread {
                                val recyclerView =
                                    songResultView.findViewById<RecyclerView>(R.id.recycler_songs)
                                recyclerView.layoutManager = LinearLayoutManager(this)
                                recyclerView.adapter = SongAdapter(songs, this)
                            }
                        }
                        resultDialog.show()
                    }.setNegativeButton("Cancel", null).create()

            searchDialog.show()
        }

        binding.selectedSong.buttonRemoveSong.setOnClickListener {
            selectedSongLayout.root.visibility = View.GONE
            binding.selectSong.visibility = View.VISIBLE
            binding.buttonPost.isEnabled = false
            binding.textPost.isEnabled = false
        }

        binding.buttonPost.setOnClickListener {
            Log.d("SoundShare", "Post button clicked")

            val postRef = firestore.collection("users").document(firebase.currentUser!!.uid)
                .collection("posts").document()

            val post = Post(
                songId = selectedSongId,
                content = binding.textPost.text.toString(),
                timestamp = Date(),
                daily = false,
                latitude = postLatitude,
                longitude = postLongitude
            )

            postRef.set(post)

            val mainIntent = Intent(this@PostActivity, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(mainIntent)
            overridePendingTransition(R.anim.slide_down, 0)
            finish()
        }

        binding.topNav.setNavigationOnClickListener {
            Log.d("SoundShare", "Back button clicked")

            MaterialAlertDialogBuilder(this).setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres salir? Se perderán los datos del post.")
                .setPositiveButton("Sí") { dialog, _ ->
                    val mainIntent = Intent(this@PostActivity, MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(mainIntent)
                    overridePendingTransition(R.anim.slide_down, 0)
                    dialog.dismiss()
                    finish()
                }.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), 123
        )
    }

    private fun getLastLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        postLatitude = it.latitude
                        postLongitude = it.longitude
                    }
                }
            } else {
                Log.e("SoundShare", "Location permissions are not granted.")
            }
        } catch (e: SecurityException) {
            Log.e("SoundShare", "SecurityException: ${e.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                locationPermissionGranted = true
                getLastLocation()
            }
        }
    }

    override fun onItemClick(item: Song) {
        selectedSongId = item.songId
        selectedSongLayout = LayoutSongBinding.bind(findViewById(R.id.selected_song))

        selectedSongLayout.songName.text = item.title
        selectedSongLayout.artistName.text = item.artist
        if (!item.songImage.isNullOrEmpty()) {
            Glide.with(selectedSongLayout.songImage.context).load(item.songImage)
                .into(selectedSongLayout.songImage)
        }
        selectedSongLayout.buttonRemoveSong.visibility = View.VISIBLE

        selectedSongLayout.root.visibility = View.VISIBLE
        binding.selectSong.visibility = View.GONE
        binding.buttonPost.isEnabled = true
        binding.textPost.isEnabled = true

        resultDialog.dismiss()
    }

    override fun onAddFriendButtonClick(item: Song) {
    }

    override fun onRemoveFriendButtonClick(item: Song) {
    }

}