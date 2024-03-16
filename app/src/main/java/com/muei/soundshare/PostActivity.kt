package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
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
                Log.d("PostActivity", "Location on")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
            } else {
                Log.d("PostActivity", "Location off")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
            }
        }

        binding.topNav.setNavigationOnClickListener {
            Log.d("PostActivity", "Back button clicked")
            MaterialAlertDialogBuilder(this).setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres salir? Se perderán los datos del post.")
                .setPositiveButton("Sí") { dialog, _ ->
                    val mainIntent = Intent(this@PostActivity, MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(mainIntent)
                    dialog.dismiss()
                    finish()
                }.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }
}