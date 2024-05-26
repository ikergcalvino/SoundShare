package com.muei.soundshare.entities

import java.util.Date

data class User(
    val userId: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    var dateOfBirth: Date,
    var phone: String? = null,
    val favouriteSongId: String? = null,
    val profilePicture: String? = null,
)