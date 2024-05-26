package com.muei.soundshare.util

import android.view.View
import com.muei.soundshare.R
import com.muei.soundshare.SpotifyClient
import com.muei.soundshare.databinding.LayoutPostBinding
import com.muei.soundshare.entities.Post
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PostAdapter(private val posts: List<Post>, clickListener: ItemClickListener<Post>?) :
    BaseAdapter<Post>(posts, clickListener, R.layout.layout_post), KoinComponent {

    private val spotifyClient: SpotifyClient by inject()

    private var filteredPosts: List<Post> = posts

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
        binding.userName.text = item.userId.toString()

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