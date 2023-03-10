package com.estg.ipp.fitnessapp.Locations

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentLocationDetailsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class LocationDetailsFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentLocationDetailsBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val binding get() = _binding!!
    private var requestingLocationUpdates = true
    private var REQUESTING_LOCATION_UPDATES_KEY = "LOCATION_KEY"
    private lateinit var mView: MapView
    private lateinit var destination: LatLng
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationViewModel: LocationViewModel
    val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 100
        fastestInterval = 50
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        maxWaitTime = 100
    }
    val args: LocationDetailsFragmentArgs by navArgs()
    val MAPVIEW_BUNDLE_KEY = "Location Details Map"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationViewModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]

        updateValuesFromBundle(savedInstanceState)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (locationViewModel.location.value == null) {
                        locationViewModel.location.postValue(location)
                    } else {
                        if (locationViewModel.location.value!!.latitude != location.latitude &&
                            locationViewModel.location.value!!.longitude != location.longitude
                        ) {
                            locationViewModel.location.postValue(location)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mView = binding.map


        /*
        Starting buttons actions
         */

        binding.maps.setOnClickListener {
            val gmmIntentUri = Uri.parse(
                "google.navigation:q=${args.data.properties!!.lat}," +
                        "${args.data.properties!!.lon}"
            )

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        binding.sendMessage.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_VIEW)

            sendIntent.data = Uri.parse("sms:")

            val message: String

            if (args.data.properties!!.name!!.isNotBlank()) {
                message =
                    requireContext().getString(R.string.invitation) + " ${args.data.properties!!.name}," +
                            "${args.data.properties!!.addressLine2}?"
            } else {
                message =
                    requireContext().getString(R.string.invitation) + " ${args.data.properties!!.addressLine2}?"
            }

            sendIntent.putExtra("sms_body", message)

            startActivity(sendIntent)
        }


        val LocationObserver = Observer<Location> {
            mView.getMapAsync(this)
        }

        locationViewModel.location.observe(viewLifecycleOwner, LocationObserver)

        binding.startRun.setOnClickListener {
            if (locationViewModel.location.value != null) {
                findNavController().navigate(
                    LocationDetailsFragmentDirections.actionSeeLocationDetailsToNavTrackingWithRoute(
                        locationViewModel.location.value!!.latitude.toString(),
                        locationViewModel.location.value!!.longitude.toString(),
                        destination.latitude.toString(),
                        destination.longitude.toString()
                    )
                )
            }
        }


        initGoogleMap(savedInstanceState)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener {

                }
        } catch (e: SecurityException) {
            Log.d("What? ", ") Algo de errado não está certo")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mView.onCreate(mapViewBundle)
        mView.getMapAsync(this)
    }


    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mView.onSaveInstanceState(mapViewBundle)
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    @Override
    override fun onStart() {
        super.onStart()
        mView.onStart()
    }

    @Override
    override fun onStop() {
        super.onStop()
        mView.onStop()
    }

    override fun onPause() {
        mView.onPause()
        super.onPause()
        stopLocationUpdates()
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            if (locationViewModel.location.value != null) {
                val currLocation = LatLng(
                    locationViewModel.location.value!!.latitude,
                    locationViewModel.location.value!!.longitude
                )

                destination = LatLng(
                    args.data.properties!!.lat as Double,
                    args.data.properties!!.lon as Double
                )

                map.addMarker(
                    MarkerOptions().position(currLocation)
                        .title("A tua localização atual")
                )

                map.addMarker(
                    MarkerOptions().position(destination)
                        .title("Destino")
                )

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 10F))

                map.addPolyline(
                    PolylineOptions()
                        .add(
                            LatLng(
                                locationViewModel.location.value!!.latitude,
                                locationViewModel.location.value!!.longitude
                            ), LatLng(
                                args.data.properties!!.lat as Double,
                                args.data.properties!!.lon as Double
                            )
                        )
                        .width(5f)
                        .color(Color.BLUE)
                )
            }
        }
        map.isMyLocationEnabled = true
    }
}