package com.muei.soundshare.ui.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.muei.soundshare.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val addViewModel = ViewModelProvider(this)[AddViewModel::class.java]

        _binding = FragmentAddBinding.inflate(inflater, container, false)

        val btnUpload = binding.buttonUpload

        btnUpload.setOnClickListener {
            Log.d("AddFragment", "Upload clicked")
        }

        val searchBar = binding.searchBar

        searchBar.setOnClickListener{
            Log.d("AddFragment", "Searchbar clicked")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}