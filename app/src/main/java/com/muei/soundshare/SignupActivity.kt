package com.muei.soundshare

/*import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.people.v1.PeopleService
import com.google.api.services.people.v1.model.Person
import com.google.api.services.people.v1.PeopleServiceRequestInitializer*/
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import com.muei.soundshare.databinding.ActivitySignupBinding
import com.muei.soundshare.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var loadingOverlay: View

    private val firebaseAuth: FirebaseAuth by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Firebase.firestore

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
                val username = binding.textUsername.text.toString()
                val dateOfBirth = binding.textDateOfBirth.text.toString()
                val phoneNumber = binding.textPhoneNumber.text.toString()

                val user = hashMapOf(
                    "email" to email,
                    "username" to username,
                    "dateOfBirth" to dateOfBirth,
                    "phoneNumber" to phoneNumber,
                    "profileImage" to ""
                )

                val userDocRef = db.collection("users").document(email)
                userDocRef.get().addOnCompleteListener { docTask ->
                    if (docTask.isSuccessful) {
                        val document = docTask.result
                        if (document.exists()) {
                            Toast.makeText(
                                baseContext,
                                "Username already exists.",
                                Toast.LENGTH_LONG,
                            ).show()
                        } else {

                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            delay(2000)
                                            showLoading(true)
                                            db.collection("users")
                                                .document(email).set(user)
                                                .addOnSuccessListener { documentReference ->
                                                    Log.d(
                                                        "SoundShare",
                                                        "DocumentSnapshot added with ID: $email"
                                                    )
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w("SoundShare", "Error adding document", e)
                                                }
                                            val mainIntent =
                                                Intent(
                                                    this@SignupActivity,
                                                    MainActivity::class.java
                                                )
                                            startActivity(mainIntent)
                                            finish()
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
                }
            }
        }

        binding.buttonSignUpWithGoogle.setOnClickListener {
            Log.d("SoundShare", "Sign up with Google button clicked")

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("560113047004-kssj6q8mnkmoqn6lhbvqlnvfhhv8sq21.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            // Intent para iniciar el flujo de inicio de sesión de Google
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, Constants.RC_SIGN_IN)

            val mainIntent = Intent(this@SignupActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        binding.buttonLogInHere.setOnClickListener {
            Log.d("SoundShare", "Log in here button clicked")
            val loginIntent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(loginIntent)
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

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, actualizar UI con la información del usuario firmado
                    Log.d("SoundShare", "signInWithCredential:success")
                    val user = firebaseAuth.currentUser

                    /*val netHttpTransport = NetHttpTransport()
                    val gsonFactory = GsonFactory()

                    val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
                    val accessToken = googleAccount?.idToken

                    val credential1 = GoogleCredential().setAccessToken(accessToken)

                    val peopleService = PeopleService.Builder(netHttpTransport, gsonFactory, credential1)
                        .setApplicationName("SoundShare")
                        .setGoogleClientRequestInitializer(PeopleServiceRequestInitializer(accessToken))
                        .build()

                    val person: Person = peopleService.people().get("people/me")
                        .setPersonFields("birthdays")
                        .execute()

                    val birthdays = person.birthdays
                    for (birthday in birthdays) {
                        if (birthday.date != null) {
                            val date = birthday.date
                            val day = date.day
                            val month = date.month
                            val year = date.year

                            println("User's birthday is $day/$month/$year")
                            Log.d("SoundShare", "User's birthday is $day/$month/$year")
                        }
                    }*/
                } else {
                    // Si el inicio de sesión falla, mostrar un mensaje al usuario
                    Log.w("SoundShare", "signInWithCredential:failure", task.exception)
                    // ...
                }
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
            Toast.makeText(this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT)
                .show()
            binding.textRepeatPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (dateOfBirth.isEmpty()) {
            Toast.makeText(this, getString(R.string.date_of_birth_required), Toast.LENGTH_SHORT)
                .show()
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
            Toast.makeText(this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT)
                .show()
            binding.textRepeatPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        return true
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
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