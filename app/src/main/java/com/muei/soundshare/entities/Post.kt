package com.muei.soundshare.entities

import java.util.Date

data class Post(
    val postId: Long = 0,
    val userId: Long,
    val songId: String,
    val content: String? = null,
    val timestamp: Date,
    val daily: Boolean,
    val latitude: Double,
    val longitude: Double
)