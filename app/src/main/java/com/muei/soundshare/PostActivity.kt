package com.muei.soundshare

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.muei.soundshare.databinding.ActivityPostBinding
import com.muei.soundshare.databinding.LayoutSongBinding
import com.muei.soundshare.ui.search.SearchViewModel
import com.muei.soundshare.util.SongAdapter
import java.util.Date

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var songAdapter: SongAdapter
    private lateinit var selectedSongLayout: LayoutSongBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences


    val db = Firebase.firestore
    val post = hashMapOf(
        "postId" to 0,
        "userId" to 0,
        "songId" to 0,
        "content" to "Mi primer post",
        "dateTime" to Date.UTC(2024, 2, 3, 12, 30, 0),
        "latitude" to 40.416775 as Any,
        "longitude" to -3.703790 as Any,
        "likes" to "Manuel, Miguel y María"
    )


    @RequiresApi(Build.VERSION_CODES.O)
    private val searchViewModel =
        SearchViewModel()
    private var selectedSongId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedSongLayout = LayoutSongBinding.bind(findViewById(R.id.selectedSongLayout))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        binding.recyclerSongs.layoutManager =
            LinearLayoutManager(this)

        songAdapter = SongAdapter(searchViewModel.getSongs()) { songId ->
            selectedSongId = songId
            val selectedSong = searchViewModel.getSongById(selectedSongId!!)
            if (selectedSong != null) {
                selectedSongLayout.songName.text = selectedSong.title
                selectedSongLayout.artistName.text = selectedSong.artist
                selectedSongLayout.buttonRemoveSong.visibility = View.VISIBLE
                selectedSongLayout.root.visibility = View.VISIBLE
                binding.searchView.visibility = View.GONE
                binding.editText.isEnabled = true // Habilitar el EditText
                binding.buttonPost.isEnabled = true // Habilitar el botón "Post"


            }
        }

        selectedSongLayout.buttonRemoveSong.setOnClickListener {
            selectedSongLayout.root.visibility = View.GONE
            binding.searchView.visibility = View.VISIBLE
            binding.editText.isEnabled = false // Deshabilitar el EditText
            binding.buttonPost.isEnabled = false // Deshabilitar el botón "Post"


        }

        binding.recyclerSongs.adapter = songAdapter

        sharedPreferences = getSharedPreferences("com.muei.soundshare", MODE_PRIVATE)

        val locationEnabled = sharedPreferences.getBoolean("ubicacion", false)

        binding.switchLocation.isChecked = locationEnabled

        binding.switchLocation.isEnabled = locationEnabled
        val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("SoundShare", "Location on")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@OnCheckedChangeListener
                }

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            val latLng = LatLng(it.latitude, it.longitude)
                            post["latitude"] = latLng.latitude
                            post["longitude"] = latLng.longitude
                        }
                    }
            } else {
                Log.d("SoundShare", "Location off")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
            }
        }

        binding.switchLocation.setOnCheckedChangeListener(onCheckedChangeListener)

        // Llamamos al listener manualmente una vez por si el usuario ha dejado activada la ubicación
        onCheckedChangeListener.onCheckedChanged(binding.switchLocation, binding.switchLocation.isChecked)
//        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                Log.d("SoundShare", "Location on")
//                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
//
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                }
//                fusedLocationClient.lastLocation
//                    .addOnSuccessListener { location ->
//                        location?.let {
//                            val latLng = LatLng(it.latitude, it.longitude)
//                            post["latitude"] = latLng.latitude
//                            post["longitude"] = latLng.longitude
//                        }
//                    }
//            } else {
//                Log.d("SoundShare", "Location off")
//                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
//            }
//        }
//        onCheckedChangeListener.onCheckedChanged(binding.switchLocation, binding.switchLocation.isChecked)

        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                songAdapter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonPost.setOnClickListener {
            Log.d("SoundShare", "Post button clicked")
            db.collection("posts").add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d("SoundShare", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("SoundShare", "Error adding document", e)
                }
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

}
