package com.muei.soundshare.entities

import java.util.Date

data class Post(
    val songId: String = "",
    val content: String? = null,
    val timestamp: Date = Date(),
    val daily: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    // Necesario para Firestore
    constructor() : this("", null, Date(), false, null, null)
}