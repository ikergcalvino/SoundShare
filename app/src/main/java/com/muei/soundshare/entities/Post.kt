package com.muei.soundshare.entities

import java.time.LocalDateTime

data class Post(
    val postId: Long = 0,
    val userId: Long,
    val song: Song,
    val content: String? = null,
    val dateTime: LocalDateTime,
    val location: String,
    val likesCount: Int = 0
)