package com.example.gymapp.ui

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.gymapp.R
import com.example.gymapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.profileImage.setImageURI(it)  // Set the selected image URI to the ImageView
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set profile image (assuming a default image here)
        binding.profileImage.setImageResource(R.drawable.ic_profile)

        // Set the username - fetch it dynamically
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Retrieve user data from Firestore
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "Unknown"
                        binding.username.text = username  // Set username dynamically

                        // Set experience - dynamically populate the experience dropdown
                        val experienceLevel = document.getString("experienceLevel") ?: "N/A"
                        val activeLevel = document.getString("activeLevel") ?: "N/A"
                        val experienceDetails = listOf("experienceLevel: $experienceLevel", "activeLevel: $activeLevel")
                        populateExperienceDropdown(experienceDetails)

                        // Set goals - dynamically populate the goals dropdown
                        val goals = document.get("goals") as? List<String> ?: emptyList()
                        if (goals.isNotEmpty()) {
                            binding.goalsText.text = "Goals"
                            populateGoalsDropdown(goals)  // Populate dynamically
                        } else {
                            binding.goalsText.text = "No goals set"
                        }

                        // Set personal details - dynamically populate personal details dropdown
                        val age = document.getString("ageGroup") ?: "N/A"
                        val weight = document.getDouble("weight")?.toString() ?: "N/A"
                        val height = document.getDouble("height")?.toString() ?: "N/A"
                        val sex = document.getString("sex") ?: "Not specified"
                        val personalDetails = listOf("Age: $age", "Weight: $weight kg", "Height: $height cm", "Sex: $sex")
                        populatePersonalDetailsDropdown(personalDetails)  // Populate dynamically
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        //  Change Profile
        binding.editText.setOnClickListener {
            // Navigate to GoalsActivity when the 'Edit Profile' TextView is clicked
            val intent = Intent(requireContext(), GoalsActivity::class.java)

            // You can pass extra data if needed, such as user ID, here
            // intent.putExtra("USER_ID", userId)

            startActivity(intent)
        }

        // Set profile image picker click listener
        binding.profileImage.setOnClickListener {
            // Launch the image picker
            pickImageLauncher.launch("image/*")
        }

        // Toggle experience dropdown visibility
        binding.experienceText.setOnClickListener {
            val isVisible = binding.experienceDropdown.visibility == View.VISIBLE
            binding.experienceDropdown.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        // Toggle goals dropdown visibility
        binding.goalsText.setOnClickListener {
            val isVisible = binding.goalsDropdown.visibility == View.VISIBLE
            binding.goalsDropdown.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        // Toggle personal details dropdown visibility
        binding.personalDetailsText.setOnClickListener {
            val isVisible = binding.personalDetailsDropdown.visibility == View.VISIBLE
            binding.personalDetailsDropdown.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        // Sign-out functionality
        binding.signOutText.setOnClickListener {
            // Clear login state
            val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

            // Redirect to LoginActivity
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()  // Close host activity
        }
    }

    // Functions to populate dropdowns
    private fun populateGoalsDropdown(goals: List<String>) {
        // Clear previous items
        binding.goalsDropdown.removeAllViews()

        // Add each goal as a TextView
        goals.forEach { goal ->
            val goalTextView = TextView(requireContext())
            goalTextView.text = "• $goal"
            goalTextView.textSize = 25f
            binding.goalsDropdown.addView(goalTextView)
        }
    }

    // Function to populate the personal details dropdown dynamically
    private fun populatePersonalDetailsDropdown(details: List<String>) {
        // Clear previous items
        binding.personalDetailsDropdown.removeAllViews()

        // Add each personal detail as a TextView
        details.forEach { detail ->
            val detailTextView = TextView(requireContext())
            detailTextView.text = "• $detail"
            detailTextView.textSize = 25f
            binding.personalDetailsDropdown.addView(detailTextView)
        }
    }

    // Function to populate the experience dropdown dynamically
    private fun populateExperienceDropdown(experiences: List<String>) {
        // Clear previous items
        binding.experienceDropdown.removeAllViews()

        // Add each personal detail as a TextView
        experiences.forEach { experience ->
            val experienceTextView = TextView(requireContext())
            experienceTextView.text = "• $experience"
            experienceTextView.textSize = 25f
            binding.experienceDropdown.addView(experienceTextView)
        }
    }
}
