package com.muei.soundshare

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
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

        auth = FirebaseAuth.getInstance()

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

                val email = binding.textEmail.text.toString()
                val password = binding.textPassword.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            CoroutineScope(Dispatchers.Main).launch {
                                showLoading(true)
                                delay(2000)
                                showLoading(false)
                                if (true) {
                                    val mainIntent = Intent(this@SignupActivity, MainActivity::class.java)
                                    startActivity(mainIntent)
                                    finish()
                                } else {
                                }
                            }
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Sign up failed.",
                                Toast.LENGTH_LONG,
                            ).show()
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
        val red = 239
        val green = 119
        val blue = 113

        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.email_required), Toast.LENGTH_LONG).show()
            binding.textEmail.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (username.isEmpty()) {
            Toast.makeText(this, getString(R.string.username_required), Toast.LENGTH_SHORT).show()
            binding.textUsername.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (username.length < 6) {
            Toast.makeText(this, getString(R.string.username_length), Toast.LENGTH_SHORT).show()
            binding.textUsername.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
            binding.textEmail.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (password.length < 8) {
            Toast.makeText(this, getString(R.string.password_length), Toast.LENGTH_SHORT).show()
            binding.textPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.password_required), Toast.LENGTH_SHORT).show()
            binding.textPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT).show()
            binding.textRepeatPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (dateOfBirth.isEmpty()) {
            Toast.makeText(this, getString(R.string.date_of_birth_required), Toast.LENGTH_SHORT).show()
            binding.textDateOfBirth.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        // TODO: Poner esto más exacto
        val dateOfBirthArray = dateOfBirth.split("/")
        val year = dateOfBirthArray[2].toInt()
        val currentYear = 2024
        if (currentYear - year < 18) {
            Toast.makeText(this, getString(R.string.age_restriction), Toast.LENGTH_SHORT).show()
            binding.textDateOfBirth.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT).show()
            binding.textRepeatPassword.setBackgroundColor(Color.rgb(red, green, blue))
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