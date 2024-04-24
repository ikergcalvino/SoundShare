package com.muei.soundshare.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.muei.soundshare.databinding.FragmentProfileEditBinding

class ProfileEditFragment : Fragment() {

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
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}