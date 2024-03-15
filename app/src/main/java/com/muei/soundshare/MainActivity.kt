package com.muei.soundshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muei.soundshare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNav
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        binding.buttonShazam.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.song_layout, null)

            MaterialAlertDialogBuilder(this).setTitle("R.string.title").setView(dialogView)
                .setMessage("R.string.supporting_text")
                .setNeutralButton("R.string.cancel") { dialog, which ->
                    // Respond to neutral button press
                }.setNegativeButton("R.string.decline") { dialog, which ->
                    // Respond to negative button press
                }.setPositiveButton("R.string.accept") { dialog, which ->
                    // Respond to positive button press
                }.show()
        }
    }
}