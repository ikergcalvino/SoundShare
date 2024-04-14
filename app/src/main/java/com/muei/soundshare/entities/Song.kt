package com.muei.soundshare.entities

data class Song(
    val songId: String, val title: String, val artist: String, val songImage: String? = null
)