package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.muei.soundshare.databinding.ActivityPostBinding
import java.time.LocalDateTime
import java.util.Date

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    val db = Firebase.firestore
    val post = hashMapOf(
        "postId" to 0,
        "userId" to 0,
        "songId" to 0,
        "content" to "Mi primer post",
        "dateTime" to Date.UTC(2024, 2, 3, 12, 30, 0),
        "location" to "A Coruña",
        "likes" to "Manuel, Miguel y María"
    )

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