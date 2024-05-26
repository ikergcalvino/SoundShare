package com.muei.soundshare.util

import com.muei.soundshare.entities.Notification
import com.muei.soundshare.entities.Post
import java.util.Date

class SoundShareRepository {

    private val mockPostData: List<Post> = listOf(
        Post(
            postId = 1,
            userId = 1,
            songId = "1TwjWWo5MMKPF5cdaCRNec",
            content = "Contenido del post 1",
            timestamp = Date(124, 3, 13, 10, 0),
            daily = false,
            latitude = 43.368611,
            longitude = -8.411389
        ),
        Post(
            postId = 2,
            userId = 2,
            songId = "456",
            content = "Contenido del post 2",
            timestamp = Date(124, 3, 12, 15, 30),
            daily = true,
            latitude = 43.368611,
            longitude = -8.411389
        )
    )

    private val mockNotificationData: List<Notification> = listOf(
        Notification(
            notificationId = 1, message = "Mensaje de notificación 1", isRead = false
        ), Notification(
            notificationId = 2, message = "Mensaje de notificación 2", isRead = true
        ), Notification(
            notificationId = 3, message = "Mensaje de notificación 3", isRead = false
        )
    )

    fun getPosts(): List<Post> {
        return mockPostData
    }

    fun getNotifications(): List<Notification> {
        return mockNotificationData
    }

}