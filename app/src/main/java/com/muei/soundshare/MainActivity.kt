package com.muei.soundshare

import android.os.Bundle
import android.util.Log
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

        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    Log.d("MainActivity", "Home clicked")
                    navController.navigate(R.id.navigation_home)
                    true
                }

                R.id.navigation_search -> {
                    Log.d("MainActivity", "Search clicked")
                    navController.navigate(R.id.navigation_search)
                    true
                }

                R.id.navigation_post -> {
                    Log.d("MainActivity", "Post clicked")
                    navController.navigate(R.id.navigation_post)
                    true
                }

                R.id.navigation_notifications -> {
                    Log.d("MainActivity", "Notifications clicked")
                    navController.navigate(R.id.navigation_notifications)
                    true
                }

                R.id.navigation_map -> {
                    Log.d("MainActivity", "Map clicked")
                    navController.navigate(R.id.navigation_map)
                    true
                }

                else -> false
            }
        }

        binding.buttonShazam.setOnClickListener {
            Log.d("MainActivity", "Shazam button clicked")
            val dialogView = layoutInflater.inflate(R.layout.song_layout, null)

            MaterialAlertDialogBuilder(this).setTitle("Title").setView(dialogView)
                .setMessage("Support Text").setNeutralButton("Cancel") { dialog, which ->
                    // Respond to neutral button press
                }.setNegativeButton("Decline") { dialog, which ->
                    // Respond to negative button press
                }.setPositiveButton("Accept") { dialog, which ->
                    // Respond to positive button press
                }.show()
        }
    }
}