package com.muei.soundshare.entities

data class Notification(
    val message: String,
    var isRead: Boolean = false
)