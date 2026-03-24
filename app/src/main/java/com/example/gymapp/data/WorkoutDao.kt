package com.example.gymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertSession(workoutSession: WorkoutSession): Long

    @Insert
    suspend fun insertExercise(exercise: Exercise)

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY timestamp DESC ")
    fun getAllSessions(userId: String): Flow<List<WorkoutWithExercises>>

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId")
    fun getAllSessionsWithExercises(userId: String): Flow<List<WorkoutWithExercises>>
}
