package com.muei.soundshare.entities

import java.time.LocalDate

data class User(
    val userId: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    var dateOfBirth: LocalDate,
    var phone: String? = null,
    val favouriteSongId: String? = null,
    val profilePicture: String? = null,
    val friendsIds: List<Long> = emptyList()
)