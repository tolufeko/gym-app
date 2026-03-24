package com.example.gymapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymapp.data.AppDatabase
import com.example.gymapp.data.Exercise
import com.example.gymapp.data.Progress
import com.example.gymapp.data.WorkoutSession
import com.example.gymapp.databinding.FragmentLogBinding
import com.example.gymapp.viewmodel.ExerciseViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LogWorkoutFragment : Fragment() {
    private lateinit var binding: FragmentLogBinding
    private val viewModel: ExerciseViewModel by viewModels()
    private lateinit var adapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExerciseAdapter(
            viewModel.exercisesLiveData.value ?: mutableListOf()
        ) { index ->
            val current = viewModel.exercisesLiveData.value ?: return@ExerciseAdapter
            val updated = current.toMutableList()
            updated.removeAt(index)
            viewModel.exercisesLiveData.value = updated
        }
        binding.exercisesRecyclerView.adapter = adapter
        binding.exercisesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ExerciseAdapter(
            viewModel.exercisesLiveData.value ?: mutableListOf()
        ) { index ->
            val current = viewModel.exercisesLiveData.value ?: return@ExerciseAdapter
            val updated = current.toMutableList()
            updated.removeAt(index)
            viewModel.exercisesLiveData.value = updated
        }
        binding.exercisesRecyclerView.adapter = adapter

        viewModel.exercisesLiveData.observe(viewLifecycleOwner) {
            adapter.updateExercises(it)
        }
        val templateName = arguments?.getString("templateName") ?: ""
        if (templateName.isNotBlank()) {
            binding.templateNameTextView.text = "Using Template: $templateName"
            binding.templateNameTextView.visibility = View.VISIBLE
            binding.etWorkoutName.setText(templateName)
            templateExercises[templateName]?.let {
                viewModel.exercisesLiveData.value = it.toMutableList()
            }
        } else if (viewModel.exercisesLiveData.value.isNullOrEmpty()) {
            viewModel.addExercise()
        }

        binding.btnAddExercise.setOnClickListener {
            viewModel.addExercise()
        }

        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveWorkout()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateInput(): Boolean {
        val nameValid = if (binding.etWorkoutName.text.isNullOrBlank()) {
            arguments?.getString("templateName").isNullOrBlank()
        } else {
            binding.etWorkoutName.text?.isNotBlank() ?: false
        }

        if (!nameValid) {
            showError("Please enter a workout name")
            return false
        }

        val entries = viewModel.exercisesLiveData.value ?: return false
        return if (entries.isEmpty()) {
            showError("Please add at least one exercise")
            false
        } else {
            entries.all { it.name.isNotBlank() && it.sets.isNotBlank() && it.reps.isNotBlank() && it.weight.isNotBlank() }
                .also { valid -> if (!valid) showError("All fields must be filled") }
        }
    }

    private fun saveWorkout() {
        lifecycleScope.launch {
            try {
                val database = AppDatabase.getDatabase(requireContext().applicationContext)

                val sessionId = database.workoutDao().insertSession(
                    WorkoutSession(
                        name = binding.etWorkoutName.text.toString(),
                        notes = binding.etWorkoutNotes.text.toString(),
                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                    )
                )

                viewModel.exercisesLiveData.value?.forEach { entry ->
                    val exercise = Exercise(
                        workoutSessionId = sessionId,
                        name = entry.name,
                        sets = entry.sets.toInt(),
                        reps = entry.reps.toInt(),
                        weight = entry.weight.toFloat()
                    )
                    database.workoutDao().insertExercise(exercise)
                }

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val currentProgress = database.progressDao().getProgressForUser(userId)

                val updatedProgress = if (currentProgress != null) {
                    Progress(
                        id = currentProgress.id,
                        userId = userId,
                        totalWorkouts = currentProgress.totalWorkouts + 1,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    Progress(userId = userId, totalWorkouts = 1, timestamp = System.currentTimeMillis())
                }

                database.progressDao().insertProgress(updatedProgress)
                Log.d("ProgressDebug", "Saved Progress: $updatedProgress")
                findNavController().popBackStack()

            } catch (e: Exception) {
                showError("Failed to save workout: ${e.localizedMessage}")
                Log.e("Save Error", e.localizedMessage)
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private val templateExercises = mapOf(
        "Push Day" to listOf(
            ExerciseEntry("Bench Press", "3", "10", "135"),
            ExerciseEntry("Overhead Press", "3", "10", "95"),
            ExerciseEntry("Triceps Pushdown", "3", "12", "50")
        ),
        "Pull Day" to listOf(
            ExerciseEntry("Pull-ups", "3", "8", "0"),
            ExerciseEntry("Barbell Row", "3", "10", "115"),
            ExerciseEntry("Bicep Curl", "3", "12", "25")
        ),
        "Leg Day" to listOf(
            ExerciseEntry("Squat", "3", "10", "185"),
            ExerciseEntry("Leg Press", "3", "12", "200"),
            ExerciseEntry("Calf Raise", "3", "15", "90")
        )
    )
}
