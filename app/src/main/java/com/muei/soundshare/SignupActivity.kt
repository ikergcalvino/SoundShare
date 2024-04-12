package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.muei.soundshare.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var loadingOverlay: View

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

    private fun showLoading(show: Boolean) {
        if (show) {
            loadingOverlay.visibility = View.VISIBLE
        } else {
            loadingOverlay.visibility = View.GONE
        }
    }
}