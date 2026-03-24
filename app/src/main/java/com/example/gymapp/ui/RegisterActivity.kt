package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.gymapp.databinding.ActivityRegisterBinding
import com.example.gymapp.viewmodel.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        viewModel.name.observe(this) {
            if (!it.isNullOrBlank() && binding.etName.text.toString() != it) {
                binding.etName.setText(it)
            }
        }

        viewModel.email.observe(this) {
            if (!it.isNullOrBlank() && binding.etEmail.text.toString() != it) {
                binding.etEmail.setText(it)
            }
        }

        viewModel.password.observe(this) {
            if (!it.isNullOrBlank() && binding.etPassword.text.toString() != it) {
                binding.etPassword.setText(it)
            }
        }

        binding.etName.addTextChangedListener {
            val text = it?.toString()
            if (text != viewModel.name.value) {
                viewModel.name.value = text
            }
        }

        binding.etEmail.addTextChangedListener {
            val text = it?.toString()
            if (text != viewModel.email.value) {
                viewModel.email.value = text
            }
        }

        binding.etPassword.addTextChangedListener {
            val text = it?.toString()
            if (text != viewModel.password.value) {
                viewModel.password.value = text
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                binding.tvError.text = "Please fill all fields!"
            } else {
                registerUser(email, password, name)
            }
        }

        binding.btnCancel.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf("username" to name, "email" to email)

                    FirebaseFirestore.getInstance().collection("users")
                        .document(auth.currentUser!!.uid)
                        .set(user)
                        .addOnSuccessListener {
                            val intent = Intent(this, GoalsActivity::class.java).apply {
                                putExtra("USER_ID", auth.currentUser?.uid)
                            }
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            binding.tvError.text = "Error saving user data: ${e.message}"
                        }
                } else {
                    val error = task.exception
                    when (error) {
                        is FirebaseAuthInvalidCredentialsException -> binding.tvError.text = "Invalid email format"
                        is FirebaseAuthUserCollisionException -> binding.tvError.text = "Email already exists"
                        else -> {
                            binding.tvError.text = "Registration failed: ${error?.message}"
                            Log.e("REGISTRATION", "Error details:", error)
                        }
                    }
                }
            }
    }
}
