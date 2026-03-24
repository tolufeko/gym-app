package com.example.gymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class Progress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val totalWorkouts: Int,
    val timestamp: Long = System.currentTimeMillis()
)
