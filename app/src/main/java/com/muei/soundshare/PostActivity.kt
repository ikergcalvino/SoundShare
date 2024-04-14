package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muei.soundshare.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Aquí puedes manejar el evento antes de que el texto cambie
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Aquí puedes manejar el evento cuando el texto está cambiando
                // Puedes usar 's' para buscar coincidencias en los nombres de los usuarios y los mensajes de los posts
            }

            override fun afterTextChanged(s: Editable?) {
                // Aquí puedes manejar el evento después de que el texto haya cambiado
            }
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