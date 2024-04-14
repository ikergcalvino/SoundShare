package com.muei.soundshare.util

import android.view.View
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutPostBinding
import com.muei.soundshare.entities.Post

class PostAdapter(private val posts: List<Post>) : BaseAdapter<Post>(posts, R.layout.layout_post) {
    private var filteredPosts: List<Post> = posts

    fun filter(query: String) {
        filteredPosts = if (query.isEmpty()) {
            posts
        } else {
            posts.filter { it.content?.contains(query, ignoreCase = true) == true }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredPosts.size
    }

    override fun bindItem(view: View, item: Post) {
        val binding = LayoutPostBinding.bind(view)
        binding.textPost.text = item.content
        // Set other views accordingly
    }

    override fun onItemClick(item: Post) {
        TODO("Not yet implemented")
    }

    override fun onAddButtonClick(item: Post) {
        TODO("Not yet implemented")
    }
}