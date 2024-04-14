package com.muei.soundshare.ui.notifications

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.muei.soundshare.entities.Notification
import com.muei.soundshare.util.SoundShareRepository

@RequiresApi(Build.VERSION_CODES.O)
class NotificationsViewModel : ViewModel() {
    private val soundShareRepository = SoundShareRepository()

    fun getNotifications(): List<Notification> {
        return soundShareRepository.getNotifications()
    }
}