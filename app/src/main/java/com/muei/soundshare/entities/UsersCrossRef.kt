package com.muei.soundshare.entities

import androidx.room.Entity

@Entity
data class UsersCrossRef(
    val userId: Long = 0, val friendId: Long = 0,
)