package com.example.gymapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.data.Exercise
import com.example.gymapp.databinding.ItemExerciseBinding

class ExerciseReadOnlyAdapter(private val exercises: List<Exercise>) :
    RecyclerView.Adapter<ExerciseReadOnlyAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.binding.removeButton.visibility = View.GONE
        holder.binding.exerciseNameEditText.setText(exercise.name)
        holder.binding.setsEditText.setText(exercise.sets.toString())
        holder.binding.repsEditText.setText(exercise.reps.toString())
        holder.binding.weightEditText.setText(exercise.weight.toString())

        // Disable all input fields
        holder.binding.exerciseNameEditText.isEnabled = false
        holder.binding.setsEditText.isEnabled = false
        holder.binding.repsEditText.isEnabled = false
        holder.binding.weightEditText.isEnabled = false
    }

    override fun getItemCount(): Int = exercises.size
}
