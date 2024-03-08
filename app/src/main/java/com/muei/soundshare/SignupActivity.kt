package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.muei.soundshare.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener {
            Log.d("SignupActivity", "Sign up button clicked")
            val mainIntent = Intent(this@SignupActivity, MainActivity::class.java)
            startActivity(mainIntent)
        }

        binding.buttonSignUpWithGoogle.setOnClickListener {
            Log.d("SignupActivity", "Sign up with Google button clicked")

        }
    }
}