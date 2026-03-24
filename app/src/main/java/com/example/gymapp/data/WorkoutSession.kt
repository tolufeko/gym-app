package com.example.gymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "Unnamed Workout",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val userId: String = ""
)