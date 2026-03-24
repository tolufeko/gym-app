package com.example.gymapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.gymapp.ui.ExerciseEntry

class Converters {

    @TypeConverter
    fun fromExerciseEntryList(value: List<ExerciseEntry>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<ExerciseEntry>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toExerciseEntryList(value: String?): List<ExerciseEntry>? {
        val gson = Gson()
        val type = object : TypeToken<List<ExerciseEntry>>() {}.type
        return gson.fromJson(value, type)
    }
}
