package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.gymapp.databinding.ActivityLoginBinding
import com.example.gymapp.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                binding.tvError.text = "Please fill all fields!"
            } else {
                loginUser(email, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit().putBoolean("IS_LOGGED_IN", true).apply()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    binding.tvError.text = "Invalid login please try again"
                }
            }
    }
}