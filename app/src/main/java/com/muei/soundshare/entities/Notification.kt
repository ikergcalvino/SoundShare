package com.muei.soundshare.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"]),
    ]
)
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
//    val read: Boolean = false,
    val userId: Long,
//    val type: String // e.g., "friend_request", "message", "alert"
)
