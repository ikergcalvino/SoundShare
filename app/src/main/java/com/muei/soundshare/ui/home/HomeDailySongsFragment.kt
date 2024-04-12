package com.muei.soundshare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.muei.soundshare.databinding.FragmentHomeDailySongsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeDailySongsFragment : Fragment() {

    private var _binding: FragmentHomeDailySongsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val dailySongsViewModel = ViewModelProvider(this)[HomeDailySongsViewModel::class.java]

        _binding = FragmentHomeDailySongsBinding.inflate(inflater, container, false)

        lifecycleScope.launch(Dispatchers.Main) {
            // dailySongsViewModel.loadDailySongs() // Hará uso del ContentService
            // binding.recyclerView.adapter?.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}