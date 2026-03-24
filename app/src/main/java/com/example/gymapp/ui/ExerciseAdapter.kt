package com.example.gymapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.databinding.ItemExerciseBinding

class ExerciseAdapter(private val exercises: MutableList<ExerciseEntry>,
                      private val onDelete: (Int) -> Unit) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        with(holder.binding) {
            exerciseNameEditText.setText(exercise.name)
            setsEditText.setText(exercise.sets)
            repsEditText.setText(exercise.reps)
            weightEditText.setText(exercise.weight)

            exerciseNameEditText.addTextChangedListener { exercise.name = it.toString() }
            setsEditText.addTextChangedListener { exercise.sets = it.toString() }
            repsEditText.addTextChangedListener { exercise.reps = it.toString() }
            weightEditText.addTextChangedListener { exercise.weight = it.toString() }

            removeButton.setOnClickListener {
                onDelete(holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int = exercises.size

    fun addNewExerciseEntry() {
        exercises.add(ExerciseEntry("", "", "", ""))
        notifyItemInserted(exercises.size - 1)
    }

    fun updateExercises(newList: List<ExerciseEntry>) {
        exercises.clear()
        exercises.addAll(newList)
        notifyDataSetChanged()
    }
}

data class ExerciseEntry(
    var name: String = "",
    var sets: String = "",
    var reps: String = "",
    var weight: String = ""
)
