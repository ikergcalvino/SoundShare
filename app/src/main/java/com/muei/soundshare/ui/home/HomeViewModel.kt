package com.muei.soundshare.ui.home

import androidx.lifecycle.ViewModel
import com.muei.soundshare.entities.Post
import com.muei.soundshare.util.SoundShareRepository

class HomeViewModel : ViewModel() {
    private val soundShareRepository = SoundShareRepository()

    fun getPosts(): List<Post> {
        return soundShareRepository.getPosts()
    }

    fun hasDailyPost(userId: Long): Boolean {
        return getPosts().any { post ->
            post.userId == userId && post.daily && post.timestamp.time == (System.currentTimeMillis())
        }
    }
}