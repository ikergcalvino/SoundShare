package com.muei.soundshare.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.muei.soundshare.entities.Post
import com.muei.soundshare.util.SoundShareRepository

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel : ViewModel() {
    private val soundShareRepository = SoundShareRepository()

    fun getPosts(): List<Post> {
        return soundShareRepository.getPosts()
    }
}