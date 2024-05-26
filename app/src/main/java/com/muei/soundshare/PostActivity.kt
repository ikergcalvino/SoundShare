package com.muei.soundshare

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.muei.soundshare.databinding.ActivityPostBinding
import com.muei.soundshare.databinding.LayoutSongBinding
import com.muei.soundshare.entities.Song
import com.muei.soundshare.util.Constants
import com.muei.soundshare.util.ItemClickListener
import com.muei.soundshare.util.SongAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PostActivity : AppCompatActivity(), ItemClickListener<Song>, KoinComponent {

    private lateinit var binding: ActivityPostBinding
    private lateinit var selectedSong: Song
    private lateinit var selectedSongLayout: LayoutSongBinding

    private val firebaseFirestore: FirebaseFirestore by inject()
    private val spotifyClient: SpotifyClient by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private var postLocation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (sharedPreferences.getBoolean(Constants.LOCATION, false)) {
            postLocation = true
            binding.switchLocation.setChecked(true)
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
        } else {
            postLocation = false
            binding.switchLocation.setChecked(false)
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
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
                            binding.switchLocation.setChecked(false)
                        }.setOnCancelListener {
                            binding.switchLocation.setChecked(false)
                        }.show()

                } else {
                    Log.d("SoundShare", "Location on")
                    binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
                    postLocation = true
                }
            } else {
                Log.d("SoundShare", "Location off")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
                postLocation = false
            }
        }

        binding.selectSong.setOnClickListener {
            val songSearchView: View =
                this.layoutInflater.inflate(R.layout.layout_song_search, null)

            val textTrackName = songSearchView.findViewById<TextInputEditText>(R.id.text_track_name)

            val songResultView: View =
                this.layoutInflater.inflate(R.layout.layout_song_result, null)

            val resultDialog =
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

        binding.buttonPost.setOnClickListener {
            Log.d("SoundShare", "Post button clicked")

            // ContentService para la publicación asíncrona

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

    override fun onItemClick(item: Song) {
        println(item)
    }

    override fun onAddFriendButtonClick(item: Song) {
    }

    override fun onRemoveSongButtonClick(item: Song) {
    }

}