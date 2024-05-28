package com.muei.soundshare.entities

import java.util.Date

data class Post(
    val songId: String,
    val content: String? = null,
    val timestamp: Date,
    val daily: Boolean,
    val latitude: Double?,
    val longitude: Double?
)