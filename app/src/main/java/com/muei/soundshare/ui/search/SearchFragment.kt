package com.muei.soundshare.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.muei.soundshare.databinding.FragmentSearchBinding
import com.muei.soundshare.entities.User
import com.muei.soundshare.util.ItemClickListener
import com.muei.soundshare.util.UserAdapter

class SearchFragment : Fragment(), ItemClickListener<User> {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.recyclerUsers.layoutManager =
            GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)

        userAdapter = UserAdapter(searchViewModel.getUsers(), this)

        binding.recyclerUsers.adapter = userAdapter

        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                userAdapter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: User) {
    }

    override fun onAddFriendButtonClick(item: User) {
    }

    override fun onRemoveSongButtonClick(item: User) {
    }

}