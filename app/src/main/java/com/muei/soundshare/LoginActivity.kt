package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.muei.soundshare.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogIn.setOnClickListener {
            Log.d("LoginActivity", "Log in button clicked")
            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(mainIntent)
        }

        binding.buttonLogInWithGoogle.setOnClickListener {
            Log.d("LoginActivity", "Log in with Google button clicked")
        }

        binding.createNewAccount.setOnClickListener {
            Log.d("LoginActivity", "Create new account button clicked")
            val signupIntent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }
}