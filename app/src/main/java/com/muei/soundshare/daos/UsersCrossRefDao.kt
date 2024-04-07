package com.muei.soundshare.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.muei.soundshare.entities.UsersCrossRef

@Dao
interface UsersCrossRefDao {
    @Insert
    fun insertUsersCrossRef(crossRef: UsersCrossRef)

    @Query("SELECT * FROM UsersCrossRef WHERE userId = :userId")
    fun getFriendsByUserId(userId: Long): List<UsersCrossRef>

    @Query("SELECT * FROM UsersCrossRef WHERE friendId = :friendId")
    fun getUsersByFriendId(friendId: Long): List<UsersCrossRef>

    @Delete
    fun deleteUsersCrossRef(crossRef: UsersCrossRef)

}
