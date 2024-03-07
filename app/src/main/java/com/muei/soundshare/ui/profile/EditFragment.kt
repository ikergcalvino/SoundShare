package com.muei.soundshare.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentEditBinding

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val editViewModel = ViewModelProvider(this)[EditViewModel::class.java]

        _binding = FragmentEditBinding.inflate(inflater, container, false)

        binding.topNav.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_check -> {
                    findNavController().navigate(R.id.navigation_profile)
                    true
                }

                else -> false
            }
        }

        binding.topNav.setNavigationOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}