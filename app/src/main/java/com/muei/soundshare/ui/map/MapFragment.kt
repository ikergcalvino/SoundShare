package com.muei.soundshare.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentMapBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        _binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        moveCameraToCurrentLocation()
        addPostMarkers(map,1.0)
    }
    private fun addPostMarkers(googleMap: GoogleMap, threshold: Double) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    // Límite para no obtener todos los posts, pensando en un futuro número muy alto
                    val lowerLat = currentLatLng.latitude - threshold
                    val upperLat = currentLatLng.latitude + threshold
                    val lowerLon = currentLatLng.longitude - threshold
                    val upperLon = currentLatLng.longitude + threshold
                    googleMap.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater))

                    // Firebase no deja utilizar más de un atributo para filtrar, así que solo hace latitud
                    db.collection("posts")
                        .whereGreaterThanOrEqualTo("latitude", lowerLat)
                        .whereLessThanOrEqualTo("latitude", upperLat)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                val postLatitude = document.getDouble("latitude") ?: continue
                                val postLongitude = document.getDouble("longitude") ?: continue

                                // Aquí filtramos dentro del cliente en longitud
                                if (postLongitude in lowerLon..upperLon) {
                                    val postLocation = LatLng(postLatitude, postLongitude)
                                    val marker = googleMap.addMarker(
                                        MarkerOptions()
                                            .position(postLocation)
                                            .title("Post: ${document.id}")
                                    )
                                    val content = document.getString("content") ?: continue
                                    val songId = document.getString("songId") ?: continue
                                    marker?.tag = CustomInfoWindowAdapter.PostInfo(content,songId)
//                                    googleMap.setOnMarkerClickListener { marker ->
//                                        marker.showInfoWindow()
//                                        true
//                                    }
//                                    googleMap.setOnInfoWindowClickListener { marker ->
//                                        val postInfo = marker.tag as? CustomInfoWindowAdapter.PostInfo
//                                        postInfo?.let {
//                                            showPostInfoDialog(it)
//                                        }
//                                    }

                                }
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error obteniendo ubicación actual: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }
        map.isMyLocationEnabled = true
    }

    private fun moveCameraToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error obteniendo ubicación actual: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
//    private fun showPostInfoDialog(postInfo: CustomInfoWindowAdapter.PostInfo) {
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Post Info")
//            .setMessage("Content: ${postInfo.content}\nSong ID: ${postInfo.songId}")
//            .setPositiveButton("OK") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}