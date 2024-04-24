package com.muei.soundshare.ui.profile

import android.content.Context
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

class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileEditViewModel = ViewModelProvider(this)[ProfileEditViewModel::class.java]

        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        binding.topNav.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_check -> {
                    Log.d("SoundShare", "Confirm button clicked")

                    // ProfileService para la actualización asíncrona

                    findNavController().navigate(R.id.navigation_profile)
                    true
                }

                else -> false
            }
        }

        binding.topNav.setNavigationOnClickListener {
            Log.d("SoundShare", "Cancel button clicked")
            findNavController().navigate(R.id.navigation_profile)
        }

        val sharedPreferences = requireActivity().getSharedPreferences("com.muei.soundshare", Context.MODE_PRIVATE)

        binding.switchLocation.isChecked = sharedPreferences.getBoolean("ubicacion", true)
        binding.switchNightMode.isChecked = sharedPreferences.getBoolean("nocturno", false)

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().apply {
                putBoolean("ubicacion", isChecked)
                apply()
            }
        }

        binding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().apply {
                putBoolean("nocturno", isChecked)
                apply()
            }
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
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
