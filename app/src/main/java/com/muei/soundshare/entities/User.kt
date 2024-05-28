package com.muei.soundshare.entities

import java.util.Date

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    var dateOfBirth: Date? = null,
    var phoneNumber: String? = null,
    val favouriteSongId: String? = null,
    val profilePicture: String? = null
)