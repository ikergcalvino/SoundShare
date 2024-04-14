package com.muei.soundshare

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.muei.soundshare.daos.*
import com.muei.soundshare.entities.*

@Database(entities = [Notification::class,Post::class, PostUserCrossRef::class, Song::class, User::class, UsersCrossRef::class], version = 1)
//@TypeConverters(Converters::class)
abstract class SoundShareDatabase : RoomDatabase() {
    abstract fun notificationDao():NotificationDao
    abstract fun postDao(): PostDao
    abstract fun postUserCrossRefDao(): PostUserCrossRefDao
    abstract fun songDao(): SongDao
    abstract fun userDao(): UserDao
    abstract fun usersCrossRefDao(): UsersCrossRefDao

}

//import androidx.room.TypeConverter
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter

//class Converters {
//    @TypeConverter
//    fun fromLocalDateTime(value: LocalDateTime?): String? {
//        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
//    }
//
//    @TypeConverter
//    fun toLocalDateTime(value: String?): LocalDateTime? {
//        return value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
//    }
//
//    @TypeConverter
//    fun fromLocalDate(value: LocalDate?): String? {
//        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE)
//    }
//
//    @TypeConverter
//    fun toLocalDate(value: String?): LocalDate? {
//        return value?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
//    }
//}
