package com.muei.soundshare.util

import android.view.View
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutPostBinding
import com.muei.soundshare.entities.Post
import com.muei.soundshare.services.SpotifyClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PostAdapter(
    private var posts: List<Post>,
    private val clickListener: ItemClickListener<Post>?
) : BaseAdapter<Post>(posts, clickListener, R.layout.layout_post), KoinComponent {

    private val spotifyClient: SpotifyClient by inject()
    private var filteredPosts: List<Post> = posts

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        filter(false)  // Apply default filter criteria or your specific logic here
    }

    fun filter(query: Boolean) {
        filteredPosts = if (query) {
            posts.sortedByDescending { it.timestamp }
        } else {
            posts
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredPosts.size
    }

    override fun bindItem(view: View, item: Post) {
        val binding = LayoutPostBinding.bind(view)
        binding.userName.text = "User"  // Replace with actual user data

        spotifyClient.getTrack(item.songId) { song ->
            song?.let {
                view.post {
                    val songAdapter = SongAdapter(listOf(it), clickListener = null)
                    songAdapter.bindItem(binding.song.root, it)
                }
            }
        }

        binding.textPost.text = item.content
    }
}