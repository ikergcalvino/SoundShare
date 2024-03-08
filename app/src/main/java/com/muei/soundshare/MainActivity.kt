package com.muei.soundshare

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        val btnMenu = navView.menu

        val homeMenuItem = btnMenu.findItem(R.id.navigation_home)
        homeMenuItem.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    Log.d("MainActivity", "Home clicked")
                    navController.navigate(R.id.navigation_home)
                    true
                }

                else -> false
            }
        }

        val searchMenuItem = btnMenu.findItem(R.id.navigation_search)
        searchMenuItem.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_search -> {
                    Log.d("MainActivity", "Search clicked")
                    navController.navigate(R.id.navigation_search)
                    true
                }

                else -> false
            }
        }

        val addMenuItem = btnMenu.findItem(R.id.navigation_add)
        addMenuItem.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_add -> {
                    Log.d("MainActivity", "Add clicked")
                    navController.navigate(R.id.navigation_add)
                    true
                }

                else -> false
            }
        }

        val notificationsMenuItem = btnMenu.findItem(R.id.navigation_notifications)
        notificationsMenuItem.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_notifications -> {
                    Log.d("MainActivity", "Notifications clicked")
                    navController.navigate(R.id.navigation_notifications)
                    true
                }

                else -> false
            }
        }

        val mapMenuItem = btnMenu.findItem(R.id.navigation_map)
        mapMenuItem.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_map -> {
                    Log.d("MainActivity", "Map clicked")
                    navController.navigate(R.id.navigation_map)
                    true
                }

                else -> false
            }
        }

        val btnShazam = binding.btnShazam
        btnShazam.setOnClickListener {
            Log.d("MainActivity", "Shazam button clicked")
            // Crear el AlertDialog personalizado
            val dialogView = layoutInflater.inflate(R.layout.popup_layout, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
                .setPositiveButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = builder.create()

            // Personalizar el borde del AlertDialog
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setGravity(Gravity.CENTER) // Centrar el AlertDialog en la pantalla
        }
    }
}