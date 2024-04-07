package com.muei.soundshare.daos

import androidx.room.*
import com.muei.soundshare.entities.Post
import java.time.LocalDateTime

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: Post): Long

    @Update
    fun updatePost(post: Post)

    @Delete
    fun deletePost(post: Post)

    @Query("SELECT * FROM Post WHERE postId = :postId")
    fun getPostById(postId: Long): Post?

    @Query("SELECT * FROM Post WHERE userId = :userId ORDER BY dateTime DESC")
    fun getPostsByUserId(userId: Long): List<Post>

    @Query("SELECT * FROM Post WHERE songId = :songId")
    fun getPostsBySongId(songId: Long): List<Post>

    @Query("SELECT * FROM Post ORDER BY likesCount DESC")
    fun getPopularPosts(): List<Post>

    @Query("UPDATE Post SET likesCount = likesCount + 1 WHERE postId = :postId")
    fun incrementLikes(postId: Long)

    @Query("UPDATE Post SET likesCount = likesCount - 1 WHERE postId = :postId")
    fun decrementLikes(postId: Long)

}
