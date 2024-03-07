package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muei.soundshare.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener {
            val mainIntent = Intent(this@SignupActivity, MainActivity::class.java)
            startActivity(mainIntent)
        }

        binding.buttonSignUpWithGoogle.setOnClickListener {

        }
    }
}