package com.muei.soundshare.entities

import androidx.room.Entity

@Entity
data class PostUserCrossRef(
    val postId: Long = 0, val userId: Long = 0
)