package com.muei.soundshare.entities

import androidx.room.*
import java.time.LocalDate
@Entity(
    foreignKeys = [
        ForeignKey(entity = Song::class,
            parentColumns = ["songId"],
            childColumns = ["favouriteSongId"])
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val email: String,
    val username: String,
    val hashedPassword: String, // Store a hashed password for security.
    var dateOfBirth: LocalDate,
    var phone: String? = null,
    val favouriteSongId: Long? = null,
    val profilePicture: String? = null,
    val friendsCount: Int = 0
)
