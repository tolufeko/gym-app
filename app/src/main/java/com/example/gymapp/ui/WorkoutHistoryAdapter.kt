package com.example.gymapp.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.data.WorkoutWithExercises
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutHistoryAdapter(
    private var sessions: MutableList<WorkoutWithExercises> = mutableListOf()
) : RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutViewHolder>() {

    private val expandedState = mutableListOf<Boolean>()
    private val exerciseAdapters = mutableListOf<ExerciseReadOnlyAdapter?>()

    init {
        sessions.forEach {
            expandedState.add(false)
            exerciseAdapters.add(null)
        }
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.workoutTitle)
        val date: TextView = itemView.findViewById(R.id.workoutDate)
        val exerciseRecyclerView: RecyclerView = itemView.findViewById(R.id.exerciseRecyclerView)
        val expandButton: ImageButton = itemView.findViewById(R.id.expandButton)

        init {
            expandButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    expandedState[position] = !expandedState[position]

                    // Initialize ExerciseAdapter if expanded
                    if (expandedState[position]) {
                        if (exerciseAdapters[position] == null) {
                            exerciseAdapters[position] = ExerciseReadOnlyAdapter(sessions[position].exercises)
                        }
                    } else {
                        exerciseAdapters[position] = null
                    }

                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_history, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = sessions[position]

        holder.title.text = workout.session.name
        holder.date.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Date(workout.session.timestamp))

        val isExpanded = expandedState[position]
        holder.exerciseRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE

        if (isExpanded) {
            holder.exerciseRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.exerciseRecyclerView.setHasFixedSize(false)
            holder.exerciseRecyclerView.isNestedScrollingEnabled = false
            holder.exerciseRecyclerView.adapter = exerciseAdapters[position]
        }
    }

    override fun getItemCount(): Int = sessions.size

    fun updateSessions(newSessions: List<WorkoutWithExercises>) {
        sessions.clear()
        sessions.addAll(newSessions)

        expandedState.clear()
        exerciseAdapters.clear()

        sessions.forEach {
            expandedState.add(false)
            exerciseAdapters.add(null)
        }

        notifyDataSetChanged()
    }
}
