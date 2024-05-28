package com.muei.soundshare.ui.profile

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentProfileEditBinding
import com.muei.soundshare.entities.Song
import com.muei.soundshare.services.SpotifyClient
import com.muei.soundshare.util.Constants
import com.muei.soundshare.util.ItemClickListener
import com.muei.soundshare.util.SongAdapter
import org.koin.android.ext.android.inject
import org.koin.core.component.inject

class ProfileEditFragment : Fragment() , ItemClickListener<Song> {

    private lateinit var profileEditViewModel: ProfileEditViewModel

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences by inject()
    private val firestore: FirebaseFirestore by inject()
    private val firebaseAuth: FirebaseAuth by inject()
    private val storage: FirebaseStorage by inject()
    private val spotifyClient: SpotifyClient by inject()

    private var profilePicture: String = ""
    private var currentUserUid: String? = null
    private var imageUri: Uri? = null
    private lateinit var resultDialog: AlertDialog
    private var favouriteSongId: String = ""



    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("SoundShare", "Camera permission granted")
                imageUri = createImageUri(requireContext())
                cameraLauncher.launch(imageUri)
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                uploadImageToStorage(imageUri!!)
            } else {
                Log.e("SoundShare", "Error taking picture")
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                uploadImageToStorage(uri)
            } else {
                Log.e("SoundShare", "Error selecting image from gallery")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        profileEditViewModel = ViewModelProvider(this)[ProfileEditViewModel::class.java]

        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        if (sharedPreferences.getBoolean(Constants.LOCATION, false)) {
            binding.switchLocation.isChecked = true
            binding.switchLocation.setThumbIconResource(R.drawable.ic_location_on)
        } else {
            binding.switchLocation.isChecked = false
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
            binding.switchDarkMode.isChecked = true
            binding.switchDarkMode.setThumbIconResource(R.drawable.ic_dark_mode)
        } else {
            binding.switchDarkMode.isChecked = false
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

        currentUserUid = firebaseAuth.currentUser?.uid
        setImage()

        binding.buttonCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                imageUri = createImageUri(requireContext())
                cameraLauncher.launch(imageUri)
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.buttonUpload.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.buttonSave.setOnClickListener {
            Log.d("SoundShare", "Save button clicked")
            if (validateFields()) {
                updateUserProfile()
            }
        }
        binding.selectSong.setOnClickListener {
            val songSearchView: View =
                this.layoutInflater.inflate(R.layout.layout_song_search, null)

            val textTrackName = songSearchView.findViewById<TextInputEditText>(R.id.text_track_name)

            val songResultView: View =
                this.layoutInflater.inflate(R.layout.layout_song_result, null)

            resultDialog =
                MaterialAlertDialogBuilder(requireContext()).setView(songResultView).setTitle("Search Results")
                    .setPositiveButton("OK", null).create()

            val searchDialog =
                MaterialAlertDialogBuilder(requireContext()).setView(songSearchView).setTitle("Song Search")
                    .setPositiveButton("Search") { _, _ ->
                        spotifyClient.searchSongs(textTrackName.text.toString()) { songs ->
                            requireActivity().runOnUiThread {
                                val recyclerView =
                                    songResultView.findViewById<RecyclerView>(R.id.recycler_songs)
                                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                recyclerView.adapter = SongAdapter(songs, this)
                            }
                        }
                        resultDialog.show()
                    }.setNegativeButton("Cancel", null).create()

            searchDialog.show()
        }
        return binding.root

    }

    private fun createImageUri(context: Context): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )
    }

    private fun validateFields(): Boolean {
        val password = binding.textPassword.text.toString()
        val confirmPassword = binding.textRepeatPassword.text.toString()
        val dateOfBirth = binding.textDateOfBirth.text.toString()

        binding.textPassword.error = null
        binding.textRepeatPassword.error = null
        binding.textDateOfBirth.error = null

        if (password.isNotEmpty() && password.length < 8) {
            binding.textPassword.error = getString(R.string.password_length)
            return false
        }

        if (password.isEmpty() && confirmPassword.isNotEmpty()) {
            binding.textPassword.error = getString(R.string.password_required)
            return false
        }

        if (confirmPassword.isEmpty() && password.isNotEmpty()) {
            binding.textRepeatPassword.error = getString(R.string.password_required)
            return false
        }

        if (password != confirmPassword) {
            binding.textRepeatPassword.error = getString(R.string.passwords_dont_match)
            return false
        }

        if (dateOfBirth.isNotEmpty()) {
            val dateOfBirthArray = dateOfBirth.split("/")
            val year = dateOfBirthArray[2].toInt()
            val currentYear = 2024
            if (currentYear - year < 18) {
                binding.textDateOfBirth.error = getString(R.string.age_restriction)
                return false
            }
        }

        return true
    }

    private fun setImage() {
        currentUserUid?.let { uid ->
            val storageRef = storage.reference.child("profile_pictures/$uid.jpg")
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                profilePicture = uri.toString()
                Glide.with(this).load(uri).circleCrop().into(binding.profilePicture)
            }.addOnFailureListener {
                Log.e("SoundShare", "Error loading profile image", it)
            }
        }
    }

    private fun uploadImageToStorage(uri: Uri) {
        currentUserUid?.let { uid ->
            val storageRef = storage.reference.child("profile_pictures/$uid.jpg")
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    profilePicture = downloadUri.toString()
                    Glide.with(this).load(downloadUri).circleCrop().into(binding.profilePicture)
                    updateprofilePictureInFirestore(downloadUri.toString())
                }.addOnFailureListener {
                    Log.e("SoundShare", "Error getting download URL", it)
                }
            }.addOnFailureListener {
                Log.e("SoundShare", "Error uploading profile image", it)
            }
        }
    }

    private fun updateprofilePictureInFirestore(imageUrl: String) {
        val currentUserEmail = firebaseAuth.currentUser?.email
        if (currentUserEmail != null) {
            firestore.collection("users").document(currentUserEmail)
                .update("profilePicture", imageUrl)
                .addOnSuccessListener {
                    Log.d("SoundShare", "Profile image URL updated in Firestore")
                }.addOnFailureListener { e ->
                    Log.e("SoundShare", "Error updating profile image URL in Firestore", e)
                }
        }
    }

    private fun updateUserProfile() {
        val password = binding.textPassword.text.toString()
        val phoneNumber = binding.textPhoneNumber.text.toString()
        val dateOfBirth = binding.textDateOfBirth.text.toString()

        var success = true

        if (password.isNotEmpty()) {
            firebaseAuth.currentUser?.updatePassword(password)?.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("SoundShare", "Password not updated")
                    success = false
                }
            }
        }

        if (phoneNumber.isNotEmpty()) {
            firestore.collection("users").document(currentUserUid!!)
                .update("phoneNumber", phoneNumber).addOnFailureListener { e ->
                    Log.w("SoundShare", "Error updating phone number", e)
                    success = false
                }
        }

        if (dateOfBirth.isNotEmpty()) {
            firestore.collection("users").document(currentUserUid!!)
                .update("dateOfBirth", dateOfBirth).addOnFailureListener { e ->
                    Log.w("SoundShare", "Error updating date of birth", e)
                    success = false
                }
        }

        if (profilePicture.isNotEmpty()) {
            firestore.collection("users").document(currentUserUid!!)
                .update("profilePicture", profilePicture).addOnFailureListener { e ->
                    Log.w("SoundShare", "Error updating profile image", e)
                    success = false
                }
        }

        if (success) {
            Toast.makeText(requireContext(), getString(R.string.profile_updated), Toast.LENGTH_LONG)
                .show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(
                requireContext(), getString(R.string.profile_not_updated), Toast.LENGTH_LONG
            ).show()
        }
        firestore.collection("users").document(currentUserUid!!)
            .update("favouriteSongId", favouriteSongId).addOnFailureListener { e ->
                Log.w("SoundShare", "Error updating favourite song id", e)
                success = false
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: Song) {
        favouriteSongId=item.songId

        resultDialog.dismiss()

    }

    override fun onAddFriendButtonClick(item: Song) {
        TODO("Not yet implemented")
    }

    override fun onRemoveFriendButtonClick(item: Song) {
        TODO("Not yet implemented")
    }
}