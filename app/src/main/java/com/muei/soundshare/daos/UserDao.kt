package com.muei.soundshare.daos

import androidx.room.*
import com.muei.soundshare.entities.User
import java.time.LocalDate

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getUserById(userId: Long): User?

    @Query("SELECT * FROM User")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM User WHERE email = :email")
    fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM User WHERE username = :username")
    fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM User WHERE dateOfBirth BETWEEN :startDate AND :endDate")
    fun getUsersBornBetweenDates(startDate: LocalDate, endDate: LocalDate): List<User>

    @Query("UPDATE User SET friendsCount = friendsCount + 1 WHERE userId = :userId")
    fun incrementFriendsCount(userId: Long)

    @Query("UPDATE User SET friendsCount = friendsCount - 1 WHERE userId = :userId")
    fun decrementFriendsCount(userId: Long)

    @Query("SELECT * FROM User WHERE favouriteSong = :favouriteSongId")
    fun getUsersWithFavouriteSong(favouriteSongId: Long): List<User>

}
