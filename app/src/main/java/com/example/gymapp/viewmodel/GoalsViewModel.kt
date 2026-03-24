
package com.example.gymapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoalsViewModel : ViewModel() {
    val weight = MutableLiveData<String>()
    val height = MutableLiveData<String>()
    val ageGroup = MutableLiveData<String>()
    val sex = MutableLiveData<String>()
    val experienceLevel = MutableLiveData<String>()
    val activityLevel = MutableLiveData<String>()
    val selectedGoals = MutableLiveData<MutableSet<String>>(mutableSetOf())
}
