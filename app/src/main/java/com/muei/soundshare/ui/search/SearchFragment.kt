package com.muei.soundshare.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muei.soundshare.databinding.FragmentSearchBinding
import com.muei.soundshare.entities.User
import com.muei.soundshare.util.ItemClickListener
import com.muei.soundshare.util.UserAdapter
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SearchFragment : Fragment(), ItemClickListener<User> {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var userAdapter: UserAdapter

    private val firebase: FirebaseAuth by inject()
    private val firestore: FirebaseFirestore by inject()

    private var friendsList: List<String> = emptyList()
    private var friendRequests: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())

        userAdapter =
            UserAdapter(emptyList(), this@SearchFragment, firebase.currentUser?.uid.toString())
        binding.recyclerUsers.adapter = userAdapter

        firebase.currentUser?.let { user ->
            val userRef = firestore.collection("users").document(user.uid)
            userRef.addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    friendsList = snapshot.get("friends") as? List<String> ?: emptyList()
                    friendRequests = snapshot.get("friendRequests") as? List<String> ?: emptyList()
                    userAdapter.updateFriends(friendsList, friendRequests)
                }
            }
        }

        lifecycleScope.launch {
            val users = searchViewModel.getUsers()
            userAdapter.updateUsers(users)
        }

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
        println("User clicked: ${item.username}")
    }

    override fun onAddFriendButtonClick(item: User) {
        println("Add friend button clicked for user: ${item.username}")
        firebase.currentUser?.let { user ->
            val currentUserRef = firestore.collection("users").document(user.uid)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(currentUserRef)
                val friendRequests =
                    (snapshot.get("friendRequests") as? MutableList<String>) ?: mutableListOf()
                if (!friendRequests.contains(item.uid)) {
                    friendRequests.add(item.uid)
                    transaction.update(currentUserRef, "friendRequests", friendRequests)
                }
            }
        }
    }

    override fun onRemoveFriendButtonClick(item: User) {
        println("Remove friend button clicked for user: ${item.username}")
        firebase.currentUser?.let { user ->
            val currentUserRef = firestore.collection("users").document(user.uid)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(currentUserRef)
                val friends = (snapshot.get("friends") as? MutableList<String>) ?: mutableListOf()
                val friendRequests =
                    (snapshot.get("friendRequests") as? MutableList<String>) ?: mutableListOf()

                if (friends.contains(item.uid)) {
                    friends.remove(item.uid)
                    transaction.update(currentUserRef, "friends", friends)
                }
                if (friendRequests.contains(item.uid)) {
                    friendRequests.remove(item.uid)
                    transaction.update(currentUserRef, "friendRequests", friendRequests)
                }
            }
        }
    }
}