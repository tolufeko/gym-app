package com.example.gymapp.ui

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymapp.R
import com.example.gymapp.data.WorkoutTemplate
import com.example.gymapp.databinding.FragmentWorkoutBinding

class WorkoutFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)

        binding.btnAddWorkout.setOnClickListener {
            findNavController().navigate(R.id.logWorkoutFragment)
        }

        // Sample templates
        val templates = listOf(
            WorkoutTemplate("Push Day", "Chest, Shoulders, Triceps"),
            WorkoutTemplate("Pull Day", "Back, Biceps"),
            WorkoutTemplate("Leg Day", "Quads, Hamstrings, Calves")
        )

        binding.templatesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.templatesRecyclerView.adapter = WorkoutTemplateAdapter(templates) { selectedTemplate ->
            val bundle = Bundle().apply {
                putString("templateName", selectedTemplate.name)
            }

            findNavController().navigate(R.id.logWorkoutFragment, bundle)
        }

        return binding.root
    }
}
