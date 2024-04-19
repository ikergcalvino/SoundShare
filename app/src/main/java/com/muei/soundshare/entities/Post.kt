package com.muei.soundshare.entities

import java.time.LocalDateTime

data class Post(
    val postId: Long = 0,
    val userId: Long,
    val songId: String,
    val content: String? = null,
    val dateTime: LocalDateTime,
    val location: String?,
    val likes: List<Long> = emptyList(),
    val daily: Boolean,
    val latitud: Double?,
    val longitud: Double?
)