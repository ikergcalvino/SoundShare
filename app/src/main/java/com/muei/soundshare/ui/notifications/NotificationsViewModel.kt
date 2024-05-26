package com.muei.soundshare.ui.notifications

import androidx.lifecycle.ViewModel
import com.muei.soundshare.entities.Notification
import com.muei.soundshare.util.SoundShareRepository

class NotificationsViewModel : ViewModel() {
    private val soundShareRepository = SoundShareRepository()

    fun getNotifications(): List<Notification> {
        return soundShareRepository.getNotifications()
    }
}