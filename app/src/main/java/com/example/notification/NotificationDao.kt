package com.example.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_data")
    fun readAllData(): List<NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: NotificationEntity)

    @Insert
    suspend fun addUser(notification: NotificationEntity)

    @Update
    suspend fun updateData(notification: NotificationEntity)
}