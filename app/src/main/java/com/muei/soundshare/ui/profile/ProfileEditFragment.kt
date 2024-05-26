package com.muei.soundshare.ui.profile

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentProfileEditBinding
import com.muei.soundshare.util.Constants
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream

class ProfileEditFragment : Fragment() {

    private val sharedPreferences: SharedPreferences by inject()

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileEditViewModel = ViewModelProvider(this)[ProfileEditViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val db = Firebase.firestore

        var profileImage = ""

        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        setImage()

        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    val circularBitmap = getCircularBitmap(bitmap)
                    binding.profileImage.setImageBitmap(circularBitmap)
                    val baos = ByteArrayOutputStream()
                    circularBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    profileImage = Base64.encodeToString(data, Base64.DEFAULT)
                }
            }

        binding.buttonUpload.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.buttonSave.setOnClickListener {
            Log.d("SoundShare", "Save button clicked")

            val password = binding.textPassword.text.toString()
            val phoneNumber = binding.textPhoneNumber.text.toString()
            val dateOfBirth = binding.textDateOfBirth.text.toString()
            val currentUserEmail = currentUser!!.email

            if (validatefields()) {
                var bool = true
                if (password.isNotEmpty()) {
                    currentUser.updatePassword(password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("SoundShare", "Password updated")
                        } else {
                            Log.d("SoundShare", "Password not updated")
                            bool = false
                        }
                    }
                }
                if (phoneNumber.isNotEmpty()) {
                    db.collection("users").document(currentUserEmail!!).update(
                        mapOf(
                            "phoneNumber" to phoneNumber
                        )
                    ).addOnSuccessListener {
                        Log.d("SoundShare", "DocumentSnapshot successfully updated!")
                    }.addOnFailureListener { e ->
                        Log.w("SoundShare", "Error updating document", e)
                        bool = false
                    }
                }

                if (dateOfBirth.isNotEmpty()) {
                    db.collection("users").document(currentUserEmail!!).update(
                        mapOf(
                            "dateOfBirth" to dateOfBirth
                        )
                    ).addOnSuccessListener {
                        Log.d("SoundShare", "DocumentSnapshot successfully updated!")
                    }.addOnFailureListener { e ->
                        Log.w("SoundShare", "Error updating document", e)
                        bool = false
                    }
                }

                if (profileImage.isNotEmpty()) {
                    db.collection("users").document(currentUserEmail!!).update(
                        mapOf(
                            "profileImage" to profileImage
                        )
                    ).addOnSuccessListener {
                        Log.d("SoundShare", "DocumentSnapshot successfully updated!")
                    }.addOnFailureListener { e ->
                        Log.w("SoundShare", "Error updating document", e)
                        bool = false
                    }
                }

                if (bool)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.profile_updated),
                        Toast.LENGTH_LONG
                    ).show()
                else
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.profile_not_updated),
                        Toast.LENGTH_LONG
                    ).show()
                findNavController().navigateUp()
            }
        }

        if (sharedPreferences.getBoolean(Constants.LOCATION, false)) {
            binding.switchLocation.setChecked(true)
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
        } else {
            binding.switchLocation.setChecked(false)
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
        }

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("SoundShare", "Location setting on")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
                sharedPreferences.edit().putBoolean(Constants.LOCATION, true).apply()
            } else {
                Log.d("SoundShare", "Location setting off")
                binding.switchLocation.setThumbIconResource(R.drawable.ic_location_off)
                sharedPreferences.edit().putBoolean(Constants.LOCATION, false).apply()
            }
        }

        if (sharedPreferences.getBoolean(Constants.DARK_MODE, false)) {
            binding.switchDarkMode.setChecked(true)
            binding.switchDarkMode.setThumbIconResource(R.drawable.ic_dark_mode)
        } else {
            binding.switchDarkMode.setChecked(false)
            binding.switchDarkMode.setThumbIconResource(R.drawable.ic_light_mode)
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("SoundShare", "Dark mode")
                binding.switchDarkMode.setThumbIconResource(R.drawable.ic_dark_mode)
                sharedPreferences.edit().putBoolean(Constants.DARK_MODE, true).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                Log.d("SoundShare", "Light mode")
                binding.switchDarkMode.setThumbIconResource(R.drawable.ic_light_mode)
                sharedPreferences.edit().putBoolean(Constants.DARK_MODE, false).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        return binding.root
    }

    private fun validatefields(): Boolean {
        val password = binding.textPassword.text.toString()
        val confirmPassword = binding.textRepeatPassword.text.toString()
        val dateOfBirth = binding.textDateOfBirth.text.toString()
        val red = 239
        val green = 119
        val blue = 113

        if (password.isNotEmpty() and (password.length < 8)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.password_length),
                Toast.LENGTH_SHORT
            ).show()
            binding.textPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (password.isEmpty() and confirmPassword.isNotEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.password_required),
                Toast.LENGTH_SHORT
            ).show()
            binding.textPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (confirmPassword.isEmpty() and password.isNotEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.password_required),
                Toast.LENGTH_SHORT
            ).show()
            binding.textRepeatPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        if (dateOfBirth.isNotEmpty()) {
            val dateOfBirthArray = dateOfBirth.split("/")
            val year = dateOfBirthArray[2].toInt()
            val currentYear = 2024
            if (currentYear - year < 18) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.age_restriction),
                    Toast.LENGTH_SHORT
                ).show()
                binding.textDateOfBirth.setBackgroundColor(Color.rgb(red, green, blue))
                return false
            }
        }

        if (password != confirmPassword) {
            Toast.makeText(
                requireContext(),
                getString(R.string.passwords_dont_match),
                Toast.LENGTH_SHORT
            )
                .show()
            binding.textRepeatPassword.setBackgroundColor(Color.rgb(red, green, blue))
            return false
        }

        return true
    }

    private fun setImage() {
        val db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        val currentUserEmail = auth.currentUser!!.email

        db.collection("users").document(currentUserEmail!!).get().addOnSuccessListener { document ->
            if (document != null) {
                val profileImage = document.getString("profileImage")
                if (profileImage != "") {
                    val decodedString =
                        Base64.decode(profileImage, Base64.DEFAULT)
                    val decodedByte = android.graphics.BitmapFactory.decodeByteArray(
                        decodedString,
                        0,
                        decodedString.size
                    )
                    val decodedImage = getCircularBitmap(decodedByte)
                    binding.profileImage.setImageBitmap(decodedImage)
                }
            }
        }
    }


    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = Color.RED
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color

        if (bitmap.width > bitmap.height) {
            canvas.drawCircle(
                bitmap.width / 2.toFloat(),
                bitmap.height / 2.toFloat(),
                bitmap.height / 2.toFloat(),
                paint
            )
        } else {
            canvas.drawCircle(
                bitmap.width / 2.toFloat(),
                bitmap.height / 2.toFloat(),
                bitmap.width / 2.toFloat(),
                paint
            )
        }

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}