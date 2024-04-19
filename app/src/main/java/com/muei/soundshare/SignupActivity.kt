package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.muei.soundshare.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var loadingOverlay: View

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingOverlay = findViewById(R.id.loadingOverlay)

        binding.dateOfBirthLayout.setStartIconOnClickListener {
            val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date_of_birth))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

            // Handle selected date
            datePickerDialog.addOnPositiveButtonClickListener { selectedDate ->
                binding.textDateOfBirth.setText(datePickerDialog.headerText)
            }

            datePickerDialog.show(supportFragmentManager, "DATE_PICKER")
        }

        binding.buttonSignUp.setOnClickListener {
            Log.d("SoundShare", "Sign up button clicked")

            if (validateFields()) {
                showLoading(true)
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    showLoading(false)
                    if (true) {
                        val mainIntent = Intent(this@SignupActivity, MainActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    } else {
                    }
                }
            }
        }

        binding.buttonSignUpWithGoogle.setOnClickListener {
            Log.d("SoundShare", "Sign up with Google button clicked")

            showLoading(true)
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                showLoading(false)
                if (true) {
                    val mainIntent = Intent(this@SignupActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                } else {
                }
            }
        }

        binding.buttonLogInHere.setOnClickListener {
            Log.d("SoundShare", "Log in here button clicked")
            val loginIntent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }

    private fun validateFields(): Boolean {
        val email = binding.textEmail.text.toString()
        val username = binding.textUsername.text.toString()
        val password = binding.textPassword.text.toString()
        val confirmPassword = binding.textRepeatPassword.text.toString()
        val dateOfBirth = binding.textDateOfBirth.text.toString()

        if (email.isEmpty()) {
            binding.textEmail.error = getString(R.string.email_required)
            return false
        }

        if (username.isEmpty()) {
            binding.textUsername.error = getString(R.string.username_required)
            return false
        }

        if (username.length < 6) {
            binding.textUsername.error = getString(R.string.username_length)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textEmail.error = getString(R.string.invalid_email)
            return false
        }

        if (password.length < 8) {
            binding.textPassword.error = getString(R.string.password_length)
            return false
        }

        if (password.isEmpty()) {
            binding.textPassword.error = getString(R.string.password_required)
            return false
        }

        if (confirmPassword.isEmpty()) {
            binding.textRepeatPassword.error = getString(R.string.passwords_dont_match)
            return false
        }

        if (dateOfBirth.isEmpty()) {
            binding.textDateOfBirth.error = getString(R.string.date_of_birth_required)
            return false
        }

        if (password != confirmPassword) {
            binding.textRepeatPassword.error = getString(R.string.passwords_dont_match)
            return false
        }

        return true
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val mainIntent = Intent(this@SignupActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            loadingOverlay.visibility = View.VISIBLE
        } else {
            loadingOverlay.visibility = View.GONE
        }
    }
}