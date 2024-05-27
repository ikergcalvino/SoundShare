package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muei.soundshare.databinding.ActivitySignupBinding
import com.muei.soundshare.entities.User
import com.muei.soundshare.util.Constants
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val firebase: FirebaseAuth by inject()
    private val firestore: FirebaseFirestore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loadingOverlay: View = findViewById(R.id.loadingOverlay)

        binding.dateOfBirthLayout.setStartIconOnClickListener {
            val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date_of_birth))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

            // Handle selected date
            datePickerDialog.addOnPositiveButtonClickListener { selectedDate ->
                binding.textDateOfBirth.setText(
                    SimpleDateFormat(Constants.DATE_PATTERN, Locale.getDefault()).format(
                        Date(selectedDate)
                    )
                )
            }

            datePickerDialog.show(supportFragmentManager, "DATE_PICKER")
        }

        binding.buttonSignUp.setOnClickListener {
            Log.d("SoundShare", "Sign up button clicked")

            val username = binding.textUsername.text.toString()
            val email = binding.textEmail.text.toString()
            val password = binding.textPassword.text.toString()
            val repeatPassword = binding.textRepeatPassword.text.toString()
            val dateOfBirth = binding.textDateOfBirth.text.toString()
            val phoneNumber = binding.textPhoneNumber.text.toString()

            if (validateFields(username, email, password, repeatPassword, dateOfBirth)) {
                loadingOverlay.visibility = View.VISIBLE

                firebase.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = firebase.currentUser?.uid ?: ""
                            val dateOfBirthDate =
                                SimpleDateFormat(Constants.DATE_PATTERN, Locale.getDefault()).parse(
                                    dateOfBirth
                                )

                            val user = User(
                                username = username,
                                email = email,
                                password = password,
                                dateOfBirth = dateOfBirthDate!!,
                                phoneNumber = phoneNumber
                            )

                            firestore.collection("users").document(userId).set(user)
                                .addOnSuccessListener {
                                    startActivity(
                                        Intent(
                                            this@SignupActivity, LoginActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                        } else {
                            loadingOverlay.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Sign Up Failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
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

    private fun validateFields(
        username: String,
        email: String,
        password: String,
        repeatPassword: String,
        dateOfBirth: String
    ): Boolean {

        binding.emailLayout.error = null
        binding.textEmail.error = null
        binding.usernameLayout.error = null
        binding.textUsername.error = null
        binding.passwordLayout.error = null
        binding.textPassword.error = null
        binding.repeatPasswordLayout.error = null
        binding.textRepeatPassword.error = null
        binding.dateOfBirthLayout.error = null
        binding.textDateOfBirth.error = null

        if (username.isEmpty()) {
            binding.usernameLayout.error = getString(R.string.username_required)
            return false
        }

        if (username.length < 6) {
            binding.textUsername.error = getString(R.string.username_length)
            return false
        }

        if (email.isEmpty()) {
            binding.emailLayout.error = getString(R.string.email_required)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textEmail.error = getString(R.string.invalid_email)
            return false
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = getString(R.string.password_required)
            return false
        }

        if (password.length < 8) {
            binding.textPassword.error = getString(R.string.password_length)
            return false
        }

        if (repeatPassword.isEmpty()) {
            binding.repeatPasswordLayout.error = getString(R.string.passwords_dont_match)
            return false
        }

        if (password != repeatPassword) {
            binding.textRepeatPassword.error = getString(R.string.passwords_dont_match)
            return false
        }

        if (dateOfBirth.isEmpty()) {
            binding.dateOfBirthLayout.error = getString(R.string.date_of_birth_required)
            return false
        }

        val dateOfBirthArray = dateOfBirth.split("/")
        if (Calendar.getInstance().get(Calendar.YEAR) - dateOfBirthArray[2].toInt() < 18) {
            binding.textDateOfBirth.error = getString(R.string.age_restriction)
            return false
        }

        return true
    }

}