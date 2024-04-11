package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.muei.soundshare.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingOverlay = findViewById(R.id.loadingOverlay)

        binding.buttonLogIn.setOnClickListener {
            Log.d("SoundShare", "Log in button clicked")

            showLoading(true)
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                showLoading(false)
                if (true) {
                    val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                } else {
                }
            }

        }

        binding.buttonLogInWithGoogle.setOnClickListener {
            Log.d("SoundShare", "Log in with Google button clicked")

            showLoading(true)
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                showLoading(false)
                if (true) {
                    val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                } else {
                }
            }

        }

        binding.buttonCreateNewAccount.setOnClickListener {
            Log.d("SoundShare", "Create new account button clicked")
            val signupIntent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(signupIntent)
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