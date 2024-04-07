package com.muei.soundshare.daos

import androidx.room.*
import com.muei.soundshare.entities.Notification
import java.time.LocalDateTime

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification): Long

//    @Update
//    fun updateNotification(notification: Notification)

    @Delete
    fun deleteNotification(notification: Notification)

    @Query("SELECT * FROM Notification WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): List<Notification>


}
