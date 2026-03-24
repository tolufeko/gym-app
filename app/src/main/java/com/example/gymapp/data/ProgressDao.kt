package com.example.gymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: Progress)

    @Query("SELECT * FROM progress WHERE userId = :userId LIMIT 1")
    suspend fun getProgressForUser(userId: String): Progress?

    @Query("SELECT * FROM progress WHERE userId = :userId ORDER BY timestamp ASC")
    suspend fun getAllProgressForUser(userId: String): List<Progress>
}

