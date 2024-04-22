package com.muei.soundshare

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muei.soundshare.databinding.ActivityPostBinding
import com.muei.soundshare.ui.search.SearchViewModel
import com.muei.soundshare.util.SongAdapter

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var songAdapter: SongAdapter // Asegúrate de tener un adaptador para las canciones

    @RequiresApi(Build.VERSION_CODES.O)
    private val searchViewModel =
        SearchViewModel() // Necesitarás una instancia de SearchViewModel para obtener las canciones
    private var selectedSongId: String? =
        null // Variable para almacenar el ID de la canción seleccionada

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerSongs.layoutManager =
            LinearLayoutManager(this) // Asegúrate de tener un RecyclerView para las canciones
        songAdapter = SongAdapter(searchViewModel.getSongs()) { songId ->
            // Guarda el ID de la canción seleccionada
            selectedSongId = songId

            // Oculta la barra de búsquedaº
            binding.searchView.visibility = View.GONE

            // Reinicia la actividad
            val intent = Intent(this@PostActivity, PostActivity::class.java)
            startActivity(intent)        }

        binding.recyclerSongs.adapter = songAdapter

            binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    Log.d("SoundShare", "Location on")
                    binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
                } else {
                    Log.d("SoundShare", "Location off")
                    binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
                }
            }

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
