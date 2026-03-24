
package com.example.gymapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymapp.ui.ExerciseEntry

class ExerciseViewModel : ViewModel() {
    val exercisesLiveData = MutableLiveData<MutableList<ExerciseEntry>>(mutableListOf())

    fun addExercise() {
        val current = exercisesLiveData.value ?: mutableListOf()
        current.add(ExerciseEntry())
        exercisesLiveData.value = current
    }

    fun updateExercise(index: Int, updated: ExerciseEntry) {
        exercisesLiveData.value?.let {
            if (index in it.indices) {
                it[index] = updated
                exercisesLiveData.value = it
            }
        }
    }
}
