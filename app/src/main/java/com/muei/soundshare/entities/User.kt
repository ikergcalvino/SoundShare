package com.muei.soundshare.entities

import java.time.LocalDate

data class User(
    val userId: Long = 0,
    val email: String,
    val username: String,
    val password: String,
    var dateOfBirth: LocalDate,
    var phone: String? = null,
    val favouriteSong: Song? = null,
    val profilePicture: String? = null,
    val friendsCount: Int = 0
)