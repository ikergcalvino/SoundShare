package com.muei.soundshare.entities

data class Song(
    val songId: Long = 0, val title: String, val artist: String, val songImage: String? = null
)