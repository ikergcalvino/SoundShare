package com.muei.soundshare.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muei.soundshare.LoginActivity
import com.muei.soundshare.MainActivity
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentProfileBinding
import com.muei.soundshare.entities.Post
import com.muei.soundshare.entities.User
import com.muei.soundshare.services.SpotifyClient
import com.muei.soundshare.util.PostAdapter
import com.muei.soundshare.util.SongAdapter
import org.koin.android.ext.android.inject

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val firebase: FirebaseAuth by inject()
    private val firestore: FirebaseFirestore by inject()
    private val spotifyClient: SpotifyClient by inject()

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupRecyclerView()

        if (firebase.currentUser != null) {
            loadUserProfile(firebase.currentUser!!.uid)
            loadUserFriends(firebase.currentUser!!.uid)
            loadUserPosts(firebase.currentUser!!.uid)
        }

        binding.buttonEdit.setOnClickListener {
            Log.d("SoundShare", "Edit button clicked")
            findNavController().navigate(R.id.navigation_edit_profile)
        }

        binding.buttonLogOut.setOnClickListener {
            Log.d("SoundShare", "Log out button clicked")
            FirebaseAuth.getInstance().signOut()
            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(loginIntent)
            activity?.finish()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (firebase.currentUser != null) {
            loadUserProfile(firebase.currentUser!!.uid)
            loadUserFriends(firebase.currentUser!!.uid)
            loadUserPosts(firebase.currentUser!!.uid)
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList(), null)
        binding.recyclerPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun loadUserProfile(userId: String) {
        firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    (activity as? MainActivity)?.supportActionBar?.title = user.username
                    Glide.with(this).load(user.profilePicture).into(binding.profileImage)

                    spotifyClient.getTrack(user.favouriteSongId.toString()) { song ->
                        song?.let {
                            view?.post {
                                val songAdapter = SongAdapter(listOf(it), clickListener = null)
                                songAdapter.bindItem(binding.favouriteSong.root, it)
                            }
                        }
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.w("ProfileFragment", "Error getting user profile: ", exception)
        }
    }

    private fun loadUserFriends(userId: String) {
        firestore.collection("friends").document(userId).collection("userFriends").get()
            .addOnSuccessListener { documents ->
                val friendCount = documents.size()
                binding.buttonFriends.text = getString(R.string.friends, friendCount)
            }.addOnFailureListener { exception ->
                Log.w("ProfileFragment", "Error getting user friends: ", exception)
            }
    }

    private fun loadUserPosts(userId: String) {
        firestore.collection("posts").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { documents ->
                val posts = documents.map { it.toObject(Post::class.java) }
                postAdapter = PostAdapter(posts, null)
                binding.recyclerPosts.adapter = postAdapter
            }.addOnFailureListener { exception ->
                Log.w("ProfileFragment", "Error getting user posts: ", exception)
            }
    }
//    private fun loadUserFavouriteSong(userId: String)}{
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}