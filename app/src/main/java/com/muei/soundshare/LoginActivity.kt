package com.muei.soundshare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muei.soundshare.databinding.ActivityLoginBinding
import com.muei.soundshare.entities.User
import com.muei.soundshare.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.Date

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val firebase: FirebaseAuth by inject()
    private val firestore: FirebaseFirestore by inject()
    private val storage: FirebaseStorage by inject()

    private lateinit var oneTapClient: SignInClient
    private val signInRequest: BeginSignInRequest by lazy {
        BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setServerClientId(Constants.GOOGLE_CLIENT_ID).setFilterByAuthorizedAccounts(false)
                .build()
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loadingOverlay: View = findViewById(R.id.loadingOverlay)

        if (firebase.currentUser != null) {
            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        binding.buttonLogIn.setOnClickListener {
            Log.d("SoundShare", "Log in button clicked")

            val email = binding.textEmail.text.toString()
            val password = binding.textPassword.text.toString()

            if (validateFields(email, password)) {
                firebase.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        loadingOverlay.visibility = View.VISIBLE
                        if (task.isSuccessful) {
                            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext, "Sign in failed.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        binding.buttonSignInWithGoogle.setOnClickListener {
            Log.d("SoundShare", "Sign in with Google button clicked")

            oneTapClient = Identity.getSignInClient(this)
            oneTapClient.beginSignIn(signInRequest).addOnSuccessListener { result ->
                val signInIntent = result.pendingIntent.intentSender
                signInLauncher.launch(IntentSenderRequest.Builder(signInIntent).build())
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Google Sign In failed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.buttonCreateNewAccount.setOnClickListener {
            Log.d("SoundShare", "Create new account button clicked")

            val signupIntent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(signupIntent)
            finish()
        }
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val credential =
                Identity.getSignInClient(this).getSignInCredentialFromIntent(result.data)

            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                firebase.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = firebase.currentUser
                            if (firebaseUser != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    checkAndSaveUser(firebaseUser)
                                }
                            }
                            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext, "Authentication Failed.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(applicationContext, "No ID token!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun checkAndSaveUser(firebaseUser: FirebaseUser) {
        val userId = firebaseUser.uid
        val email = firebaseUser.email ?: ""
        val username = email.substringBefore("@")
        val profilePictureUrl = firebaseUser.photoUrl?.toString()

        val userRef = firestore.collection("users").document(userId)
        val document = userRef.get().await()

        if (!document.exists()) {
            var profilePictureFixedUrl: String? = null
            profilePictureUrl?.let { url ->
                profilePictureFixedUrl = saveProfilePictureToStorage(url, userId)
            }

            val user = User(
                username = username,
                email = email,
                password = "", // Password not needed for Google sign-in
                dateOfBirth = Date(), // Default or handle appropriately
                profilePicture = profilePictureFixedUrl
            )

            userRef.set(user)
        }
    }

    private suspend fun saveProfilePictureToStorage(url: String, userId: String): String? {
        return try {
            val file = withContext(Dispatchers.IO) {
                Glide.with(this@LoginActivity).asFile().load(url)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
            }

            val storageRef = storage.reference.child("profile_pictures/$userId.jpg")
            storageRef.putFile(Uri.fromFile(file)).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun validateFields(email: String, password: String): Boolean {
        binding.emailLayout.error = null
        binding.passwordLayout.error = null

        if (email.isEmpty()) {
            binding.emailLayout.error = getString(R.string.email_required)
            return false
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = getString(R.string.password_required)
            return false
        }

        return true
    }

}