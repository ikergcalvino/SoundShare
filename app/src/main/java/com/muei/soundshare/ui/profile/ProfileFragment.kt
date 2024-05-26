package com.muei.soundshare.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.muei.soundshare.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.buttonEdit.setOnClickListener {
            Log.d("SoundShare", "Edit button clicked")
            findNavController().navigate(R.id.navigation_edit_profile)
        }

        binding.buttonLogOut.setOnClickListener {
            Log.d("SoundShare", "Log out button clicked")
            FirebaseAuth.getInstance().signOut()
            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(loginIntent)
            activity?.finish()
        }

        setImage()

        return binding.root
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
            canvas.drawCircle(bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(), bitmap.height / 2.toFloat(), paint)
        } else {
            canvas.drawCircle(bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(), bitmap.width / 2.toFloat(), paint)
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