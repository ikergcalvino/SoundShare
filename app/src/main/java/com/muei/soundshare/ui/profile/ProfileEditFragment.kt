package com.muei.soundshare.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentProfileEditBinding
import com.muei.soundshare.util.Constants
import org.koin.android.ext.android.inject

class ProfileEditFragment : Fragment() {

    private val sharedPreferences: SharedPreferences by inject()

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileEditViewModel = ViewModelProvider(this)[ProfileEditViewModel::class.java]

        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        binding.buttonSave.setOnClickListener {
            Log.d("SoundShare", "Save button clicked")

            // ProfileService para la actualización asíncrona

            findNavController().navigateUp()
        }

        if (sharedPreferences.getBoolean(Constants.LOCATION, false)) {
            binding.switchLocation.setChecked(true)
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
        } else {
            binding.switchLocation.setChecked(false)
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
        }

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("SoundShare", "Location setting on")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
                sharedPreferences.edit().putBoolean(Constants.LOCATION, true).apply()
            } else {
                Log.d("SoundShare", "Location setting off")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
                sharedPreferences.edit().putBoolean(Constants.LOCATION, false).apply()
            }
        }

        if (sharedPreferences.getBoolean(Constants.DARK_MODE, false)) {
            binding.switchDarkMode.setChecked(true)
            binding.switchDarkMode.setThumbIconResource(R.drawable.ic_dark_mode)
        } else {
            binding.switchDarkMode.setChecked(false)
            binding.switchDarkMode.setThumbIconResource(R.drawable.ic_light_mode)
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("SoundShare", "Dark mode")
                binding.switchDarkMode.setThumbIconResource(R.drawable.ic_dark_mode)
                sharedPreferences.edit().putBoolean(Constants.DARK_MODE, true).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                Log.d("SoundShare", "Light mode")
                binding.switchDarkMode.setThumbIconResource(R.drawable.ic_light_mode)
                sharedPreferences.edit().putBoolean(Constants.DARK_MODE, false).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}