package com.example.gymapp.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymapp.data.AppDatabase
import com.example.gymapp.databinding.FragmentHomeBinding
import com.example.gymapp.provider.ProgressProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: WorkoutHistoryAdapter
    private val db = FirebaseFirestore.getInstance()

    // Get the userId from FirebaseAuth
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter for the RecyclerView
        adapter = WorkoutHistoryAdapter()

        // Set up the RecyclerView
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = adapter

        // Show a loading indicator while fetching data
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.historyRecyclerView.visibility = View.GONE

        // Observe workout sessions from the Room database using Flow
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val context = context ?: return@launch // Early exit if context is null
                FirebaseAuth.getInstance().currentUser?.uid?.let {
                    AppDatabase.getDatabase(context.applicationContext).workoutDao()
                        .getAllSessionsWithExercises(
                            it
                        )
                        .collectLatest { sessionsWithExercises ->
                            // Check fragment is still attached
                            if (!isAdded) return@collectLatest

                            binding.loadingIndicator.visibility = View.GONE
                            binding.historyRecyclerView.visibility = View.VISIBLE

                            if (sessionsWithExercises.isEmpty()) {
                                binding.emptyStateText.visibility = View.VISIBLE
                            } else {
                                binding.emptyStateText.visibility = View.GONE
                            }

                            adapter.updateSessions(sessionsWithExercises.toMutableList())
                        }
                }
            } catch (e: Exception) {
                if (isAdded) { // Only show Toast if attached
                    Toast.makeText(context, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // If userId is null, prompt the user to log in
        if (userId != null) {
            fetchUserChallenge(userId)
            binding.shareButton.setOnClickListener {
                launchShareSheet()
            }
        } else {
            Log.e("AuthError", "User is not logged in")
            Toast.makeText(requireContext(), "You need to log in first", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to fetch user challenge based on goals
    private fun fetchUserChallenge(userId: String) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (!isAdded) return@addOnSuccessListener // Exit if fragment detached

                val goals = documentSnapshot.get("goals") as? List<String>
                if (goals != null) {
                    updateChallenge(goals)
                }
            }
            .addOnFailureListener { e ->
                if (isAdded) { // Only show Toast if attached
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Function to update the challenge details based on goals
    private fun updateChallenge(goals: List<String>) {
        // Determine challenge text based on user's goals
        val challengeText = when {
            "Weight Loss" in goals -> "3 cardio sessions this week"
            "Muscle Gain" in goals -> "3 strength training sessions this week"
            "Strength" in goals -> "2 strength sessions and 1 cardio session this week"
            "Endurance" in goals -> "4 endurance sessions this week"
            else -> "Custom challenge based on your goals"
        }

        binding.weeklyChallengeDetails.text = challengeText
    }

    private fun launchShareSheet() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(requireContext(), "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val contentUri = Uri.withAppendedPath(ProgressProvider.CONTENT_URI, userId)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "My Gym Progress Report")
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Grant permissions to all potential receivers
        context?.let {
            val resolvedInfo = it.packageManager.queryIntentActivities(shareIntent, 0)
            resolvedInfo.forEach { info ->
                it.grantUriPermission(
                    info.activityInfo.packageName,
                    contentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }

        startActivity(Intent.createChooser(shareIntent, "Share Progress"))
    }
}
