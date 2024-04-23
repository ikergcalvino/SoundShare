package com.muei.soundshare.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

//        binding.topNav.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.navigation_profile -> {
//                    Log.d("SoundShare", "Profile clicked")
//                    findNavController().navigate(R.id.navigation_profile)
//                    true
//                }
//
//                else -> false
//            }
//        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> loadFragment(HomePostsFragment())
                        1 -> loadFragment(HomeDailySongsFragment())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        loadFragment(HomePostsFragment())

        return binding.root
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}