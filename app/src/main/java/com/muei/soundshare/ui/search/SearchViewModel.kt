package com.muei.soundshare.ui.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.muei.soundshare.entities.Post
import com.muei.soundshare.entities.Song
import com.muei.soundshare.entities.User
import com.muei.soundshare.util.SoundShareRepository

@RequiresApi(Build.VERSION_CODES.O)
class SearchViewModel : ViewModel() {
    private val soundShareRepository = SoundShareRepository()

    fun getUsers(): List<User> {
        return soundShareRepository.getUsers()
    }

    fun getPosts(): List<Post> {
        return soundShareRepository.getPosts()
    }


    fun getSongs(): List<Song> {
        return soundShareRepository.getSongs()
    }
}