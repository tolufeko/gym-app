package com.example.gymapp.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "workout_sessions")
data class WorkoutWithExercises(
    @Embedded val session: WorkoutSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutSessionId"
    )
    val exercises: List<Exercise>
)
