package com.muei.soundshare.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.muei.soundshare.entities.Post
import com.muei.soundshare.util.SoundShareRepository
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel : ViewModel() {
    private val soundShareRepository = SoundShareRepository()

    fun getPosts(): List<Post> {
        return soundShareRepository.getPosts()
    }

    fun hasDailyPost(userId: Long): Boolean {
        val today = LocalDateTime.now().toLocalDate()
        return getPosts().any { post ->
            post.userId == userId && post.daily && post.dateTime.toLocalDate() == today
        }
    }
}