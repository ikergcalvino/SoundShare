package com.muei.soundshare.entities

import androidx.room.*
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"]),
        ForeignKey(entity = Song::class,
            parentColumns = ["songId"],
            childColumns = ["songId"])
    ]
)
data class Post(
    @PrimaryKey(autoGenerate = true) val postId: Long = 0,
    val userId: Long,
    val songId: Long, // Assuming 'Song' has a 'songId' field
    val content: String? = null,
    val dateTime: LocalDateTime,
    val location: String,
    val likesCount: Int = 0
)
