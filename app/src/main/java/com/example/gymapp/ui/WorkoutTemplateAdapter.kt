package com.example.gymapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.data.WorkoutTemplate

class WorkoutTemplateAdapter(
    private val templates: List<WorkoutTemplate>,
    private val onClick: (WorkoutTemplate) -> Unit
) : RecyclerView.Adapter<WorkoutTemplateAdapter.TemplateViewHolder>() {

    inner class TemplateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.templateName)
        val desc: TextView = view.findViewById(R.id.templateDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_template, parent, false)
        return TemplateViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val template = templates[position]
        holder.name.text = template.name
        holder.desc.text = template.description
        holder.itemView.setOnClickListener { onClick(template) }
    }

    override fun getItemCount() = templates.size
}
