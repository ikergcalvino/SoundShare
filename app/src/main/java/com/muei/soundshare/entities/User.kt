package com.muei.soundshare.entities

import java.time.LocalDate

data class User(
    val userId: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    var dateOfBirth: LocalDate,
    var phone: String? = null,
    val favouriteSong: Long? = 0,
    val profilePicture: String? = null,
    val friends: List<Long> = emptyList()
)