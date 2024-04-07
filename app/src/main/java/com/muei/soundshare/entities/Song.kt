package com.muei.soundshare.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) val songId: Long = 0,
    val title: String,
    val artist: String,
    val songImage: String?
)
