package com.muei.soundshare.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.muei.soundshare.entities.PostUserCrossRef

@Dao
interface PostUserCrossRefDao {
    @Insert
    fun insertPostUserCrossRef(crossRef: PostUserCrossRef)

    @Query("SELECT * FROM PostUserCrossRef WHERE postId = :postId")
    fun getCrossRefsByPostId(postId: Long): List<PostUserCrossRef>

    @Query("SELECT * FROM PostUserCrossRef WHERE userId = :userId")
    fun getCrossRefsByUserId(userId: Long): List<PostUserCrossRef>

    @Query("DELETE FROM PostUserCrossRef WHERE postId = :postId AND userId = :userId")
    fun deletePostUserCrossRef(postId: Long, userId: Long)

}
