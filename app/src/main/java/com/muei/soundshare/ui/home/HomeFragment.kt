package com.muei.soundshare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.muei.soundshare.databinding.FragmentHomeBinding
import com.muei.soundshare.entities.Post
import com.muei.soundshare.util.ItemClickListener
import com.muei.soundshare.util.PostAdapter

class HomeFragment : Fragment(), ItemClickListener<Post> {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.recyclerHome.layoutManager = LinearLayoutManager(requireContext())

        postAdapter = PostAdapter(homeViewModel.getPosts(), this)

        binding.recyclerHome.adapter = postAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> postAdapter.filter(true)
                        1 -> postAdapter.filter(false)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        postAdapter.filter(false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: Post) {
    }

    override fun onAddFriendButtonClick(item: Post) {
    }

    override fun onRemoveSongButtonClick(item: Post) {
    }
}