package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muei.soundshare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var topNav: MaterialToolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topNav = binding.topNav
        bottomNav = binding.bottomNav
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_notifications,
                R.id.navigation_map
            )
        )

        topNav.setupWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_profile, R.id.navigation_edit_profile -> {
                    topNav.subtitle = destination.label
                    binding.buttonShazam.visibility = View.GONE
                    topNav.menu.clear()
                }

                else -> {
                    topNav.title = getString(R.string.app_name)
                    topNav.subtitle = destination.label
                    if (topNav.menu.size() == 0) {
                        topNav.inflateMenu(R.menu.top_nav_menu)
                    }
                    binding.buttonShazam.visibility = View.VISIBLE
                }
            }
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    Log.d("SoundShare", "Home clicked")
                    navController.navigate(R.id.navigation_home)
                    true
                }

                R.id.navigation_search -> {
                    Log.d("SoundShare", "Search clicked")
                    navController.navigate(R.id.navigation_search)
                    true
                }

                R.id.navigation_post -> {
                    Log.d("SoundShare", "Post clicked")
                    val postIntent = Intent(this@MainActivity, PostActivity::class.java)
                    startActivity(postIntent)
                    overridePendingTransition(R.anim.slide_up, 0)
                    true
                }

                R.id.navigation_notifications -> {
                    Log.d("SoundShare", "Notifications clicked")
                    navController.navigate(R.id.navigation_notifications)
                    true
                }

                R.id.navigation_map -> {
                    Log.d("SoundShare", "Map clicked")
                    navController.navigate(R.id.navigation_map)
                    true
                }

                else -> false
            }
        }

        topNav.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_profile -> {
                    Log.d("SoundShare", "Profile clicked")
                    navController.navigate(R.id.navigation_profile)
                    true
                }

                else -> false
            }
        }

        binding.buttonShazam.setOnClickListener {
            Log.d("SoundShare", "Shazam button clicked")
            val dialogView = layoutInflater.inflate(R.layout.layout_song, null)

            MaterialAlertDialogBuilder(this).setTitle("Title").setView(dialogView)
                .setMessage("Support Text")
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.setNeutralButton(getString(R.string.post)) { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }
}