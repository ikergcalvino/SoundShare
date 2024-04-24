package com.muei.soundshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.muei.soundshare.databinding.ActivityLoginBinding
import com.muei.soundshare.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingOverlay: View

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingOverlay = findViewById(R.id.loadingOverlay)

        auth = FirebaseAuth.getInstance()

        binding.buttonLogIn.setOnClickListener {
            Log.d("SoundShare", "Log in button clicked")

            val email = binding.textEmail.text.toString()
            val password = binding.textPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)
                            showLoading(true)
                            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Sign in failed.",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
        }

        binding.buttonLogInWithGoogle.setOnClickListener {
            Log.d("SoundShare", "Log in with Google button clicked")

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("560113047004-kssj6q8mnkmoqn6lhbvqlnvfhhv8sq21.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            // Intent para iniciar el flujo de inicio de sesión de Google
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, Constants.RC_SIGN_IN)

            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()

        }

        binding.buttonCreateNewAccount.setOnClickListener {
            Log.d("SoundShare", "Create new account button clicked")
            val signupIntent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(signupIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In fue exitoso, autenticar con Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In falló
                Log.w("SoundShare", "Google sign in failed", e)
            }
        }
    }

    // TODO: Abstraer
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, actualizar UI con la información del usuario firmado
                    Log.d("SoundShare", "signInWithCredential:success")
                    val user = auth.currentUser
                    // ...
                } else {
                    // Si el inicio de sesión falla, mostrar un mensaje al usuario
                    Log.w("SoundShare", "signInWithCredential:failure", task.exception)
                    // ...
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
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