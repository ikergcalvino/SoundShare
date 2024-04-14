package com.muei.soundshare.ui.search

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muei.soundshare.databinding.FragmentSearchBinding
import com.muei.soundshare.util.PostAdapter
import com.muei.soundshare.util.UserAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var userAdapter: UserAdapter
    private lateinit var postAdapter: PostAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPosts.layoutManager = LinearLayoutManager(requireContext())

        userAdapter = UserAdapter(searchViewModel.getUsers())
        postAdapter = PostAdapter(searchViewModel.getPosts())

        binding.recyclerUsers.adapter = userAdapter
        binding.recyclerPosts.adapter = postAdapter

        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                userAdapter.filter(query)
                postAdapter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}