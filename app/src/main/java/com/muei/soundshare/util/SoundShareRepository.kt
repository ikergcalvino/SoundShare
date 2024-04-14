package com.muei.soundshare.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.muei.soundshare.entities.Notification
import com.muei.soundshare.entities.Post
import com.muei.soundshare.entities.Song
import com.muei.soundshare.entities.User
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class SoundShareRepository {

    private val mockUserData: List<User> = listOf(
        User(
            userId = 1,
            username = "usuario1",
            email = "usuario1@example.com",
            password = "password1",
            dateOfBirth = LocalDate.of(1990, 5, 15),
            phone = "123456789",
            favouriteSongId = "123",
            profilePicture = "url_imagen1",
            friendsIds = listOf(2, 3)
        ), User(
            userId = 2,
            username = "usuario2",
            email = "usuario2@example.com",
            password = "password2",
            dateOfBirth = LocalDate.of(1985, 8, 20),
            phone = "987654321",
            favouriteSongId = "456",
            profilePicture = "url_imagen2",
            friendsIds = listOf(1, 3)
        )
    )

    private val mockSongData: List<Song> = listOf(
        Song(
            songId = "123",
            title = "Cancion 1",
            artist = "Artista 1",
            songImage = "url_imagen_cancion1"
        ), Song(
            songId = "456",
            title = "Cancion 2",
            artist = "Artista 2",
            songImage = "url_imagen_cancion2"
        )
    )

    private val mockPostData: List<Post> = listOf(
        Post(
            postId = 1,
            userId = 1,
            songId = "123",
            content = "Contenido del post 1",
            dateTime = LocalDateTime.of(2024, 4, 13, 10, 0),
            location = "Ubicacion 1",
            likes = listOf(2, 3)
        ), Post(
            postId = 2,
            userId = 2,
            songId = "456",
            content = "Contenido del post 2",
            dateTime = LocalDateTime.of(2024, 4, 12, 15, 30),
            location = "Ubicacion 2",
            likes = listOf(1)
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

    fun getUsers(): List<User> {
        return mockUserData
    }

    fun getSongs(): List<Song> {
        return mockSongData
    }

    fun getPosts(): List<Post> {
        return mockPostData
    }

    fun getNotifications(): List<Notification> {
        return mockNotificationData
    }
}