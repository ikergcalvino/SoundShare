package com.muei.soundshare.daos

import androidx.room.*
import com.muei.soundshare.entities.Song

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(song: Song): Long

    @Update
    fun updateSong(song: Song)

    @Delete
    fun deleteSong(song: Song)

    @Query("SELECT * FROM Song WHERE songId = :songId")
    fun getSongById(songId: Long): Song?

    @Query("SELECT * FROM Song WHERE title = :title")
    fun getSongsByTitle(title: String): List<Song>

    @Query("SELECT * FROM Song WHERE artist = :artist")
    fun getSongsByArtist(artist: String): List<Song>

    @Query("SELECT * FROM Song WHERE title LIKE :searchQuery OR artist LIKE :searchQuery")
    fun searchSongs(searchQuery: String): List<Song>

    @Query("SELECT * FROM Song")
    fun getAllSongs(): List<Song>

}
