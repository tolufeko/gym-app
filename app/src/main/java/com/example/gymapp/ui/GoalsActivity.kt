package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gymapp.R
import com.example.gymapp.databinding.ActivityGoalsBinding
import com.example.gymapp.viewmodel.GoalsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val viewModel: GoalsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.weight.observe(this) { binding.etWeight.setText(it) }
        viewModel.height.observe(this) { binding.etHeight.setText(it) }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        populateExistingData(document)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnSubmitGoals.setOnClickListener {
            val activityLevel = when (binding.rgActive.checkedRadioButtonId) {
                R.id.rbLow -> "Lightly Active"
                R.id.rbMedium -> "Active"
                R.id.rbHigh -> "Very Active"
                else -> ""
            }

            val experienceLevel = when (binding.rgExperience.checkedRadioButtonId) {
                R.id.rbBeginner -> "Beginner"
                R.id.rbIntermediate -> "Intermediate"
                R.id.rbAdvanced -> "Advanced"
                else -> ""
            }

            val goals = mutableListOf<String>().apply {
                if (binding.cbWeightLoss.isChecked) add("Weight Loss")
                if (binding.cbMuscleGain.isChecked) add("Muscle Gain")
                if (binding.cbStrength.isChecked) add("Strength")
                if (binding.cbEndurance.isChecked) add("Endurance")
            }

            val ageGroup = binding.spinnerAgeGroup.selectedItem.toString()
            val weight = binding.etWeight.text.toString().toDoubleOrNull()
            val height = binding.etHeight.text.toString().toDoubleOrNull()

            val sex = when (binding.rgSex.checkedRadioButtonId) {
                R.id.rbMale -> "Male"
                R.id.rbFemale -> "Female"
                R.id.rbOther -> "Other"
                else -> ""
            }

            if (experienceLevel.isNotEmpty() && goals.isNotEmpty() && weight != null && height != null && sex.isNotEmpty()) {
                updateUserProfile(activityLevel, experienceLevel, goals, ageGroup, weight, height, sex)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserProfile(activity: String, experience: String, goals: List<String>, ageGroup: String, weight: Double, height: Double, sex: String) {
        val userId = auth.currentUser?.uid ?: return
        val userData = hashMapOf(
            "activeLevel" to activity,
            "experienceLevel" to experience,
            "goals" to goals,
            "ageGroup" to ageGroup,
            "weight" to weight,
            "height" to height,
            "sex" to sex,
            "profileComplete" to true
        )

        db.collection("users").document(userId)
            .update(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateExistingData(document: DocumentSnapshot) {
        document.getDouble("weight")?.toString()?.let { viewModel.weight.value = it }
        document.getDouble("height")?.toString()?.let { viewModel.height.value = it }

        val goals = document.get("goals") as? List<String> ?: emptyList()
        goals.forEach {
            when (it) {
                "Weight Loss" -> binding.cbWeightLoss.isChecked = true
                "Muscle Gain" -> binding.cbMuscleGain.isChecked = true
                "Strength" -> binding.cbStrength.isChecked = true
                "Endurance" -> binding.cbEndurance.isChecked = true
            }
        }

        when (document.getString("experienceLevel")) {
            "Beginner" -> binding.rbBeginner.isChecked = true
            "Intermediate" -> binding.rbIntermediate.isChecked = true
            "Advanced" -> binding.rbAdvanced.isChecked = true
        }

        when (document.getString("activeLevel")) {
            "Lightly Active" -> binding.rbLow.isChecked = true
            "Active" -> binding.rbMedium.isChecked = true
            "Very Active" -> binding.rbHigh.isChecked = true
        }

        when (document.getString("sex")) {
            "Male" -> binding.rbMale.isChecked = true
            "Female" -> binding.rbFemale.isChecked = true
            "Other" -> binding.rbOther.isChecked = true
        }

        binding.spinnerAgeGroup.setSelection(getAgeGroupPosition(document.getString("ageGroup")))
    }

    private fun getAgeGroupPosition(ageGroup: String?): Int {
        val ageGroups = resources.getStringArray(R.array.age_groups)
        return ageGroups.indexOf(ageGroup ?: "")
    }
}
