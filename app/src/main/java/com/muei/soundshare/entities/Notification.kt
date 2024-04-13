package com.muei.soundshare.entities

data class Notification(
    val notificationId: Long = 0,
    val message: String,
    var isRead: Boolean = false
)