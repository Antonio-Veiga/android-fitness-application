package com.estg.ipp.fitnessapp.tracking

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.estg.ipp.fitnessapp.databinding.FragmentSelectRouteBinding
import com.estg.ipp.fitnessapp.tracking.checkWaterAPI.CheckWaterAPI
import com.estg.ipp.fitnessapp.tracking.checkWaterAPI.dataclasses.Data
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectRouteFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var binding: FragmentSelectRouteBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mView: MapView
    private lateinit var gMap: GoogleMap
    private lateinit var curr_location: LatLng
    private var water: Boolean = false
    private var dest_location: LatLng? = null
    private var dest_marker: Marker? = null
    private var polyline: Polyline? = null
    val MAPVIEW_BUNDLE_KEY = "Select a Destination Map"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSelectRouteBinding.inflate(inflater, container, false)
        val root = binding.root

        mView = binding.mapView

        initGoogleMap(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        binding.startRun.setOnClickListener {
            if (dest_location != null) {
                verifyPoint(dest_location!!)
            } else {
                Toast.makeText(
                    requireActivity(), "Escolha um destino de rota!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return root
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mView.onCreate(mapViewBundle)
        mView.getMapAsync(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    override fun onResume() {
        super.onResume()
        mView.onResume()
        turnViewVisible()
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
        super.onPause()
        mView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mView.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dest_location = null
        /*
        binding.destLat.text = null
        binding.destLon.text = null
        */
        binding.destLat.setText("")
        binding.destLon.setText("")
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

            gMap = map

            if (map != null) {

                fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                            return this
                        }

                        override fun isCancellationRequested(): Boolean {
                            return false
                        }
                    }).addOnSuccessListener { location: Location? ->

                    val curr_location = LatLng(location!!.latitude, location.longitude)

                    val editable_lat: String
                    val editable_lon: String

                    if (curr_location.latitude.toString().length > 10) {
                        editable_lat = curr_location.latitude.toString().substring(0, 10)
                    } else {
                        editable_lat = curr_location.latitude.toString()
                    }

                    if (curr_location.longitude.toString().length > 10) {
                        editable_lon = curr_location.longitude.toString().substring(0, 10)
                    } else {
                        editable_lon = curr_location.longitude.toString()
                    }

                    binding.currLat.setText(editable_lat)
                    binding.currLon.setText(editable_lon)

                    this.curr_location = curr_location

                    map.setOnMapClickListener(this)

                    map.addMarker(
                        MarkerOptions().position(curr_location)
                            .title("A tua localização atual")
                    )

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(curr_location, 15F))
                }
            }
        }
    }

    override fun onMapClick(p0: LatLng) {

        binding.destLat.setText(p0.latitude.toString().substring(0, 10))
        binding.destLon.setText(p0.longitude.toString().substring(0, 10))

        if (curr_location != null) {

            dest_location = p0

            if (dest_marker != null) {
                dest_marker!!.remove()
            }

            if (polyline != null) {
                polyline!!.remove()
            }

            dest_marker = gMap.addMarker(
                MarkerOptions().position(p0)
                    .title("Destino selecionado ")
            )

            polyline = gMap.addPolyline(
                PolylineOptions()
                    .add(curr_location, p0)
                    .width(5f)
                    .color(Color.BLUE)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun verifyPoint(p1: LatLng) {

        val isOnWater =
            CheckWaterAPI.create().isOnWater(p1.latitude.toString(), p1.longitude.toString())

        binding.startRun.visibility = View.INVISIBLE
        binding.mapView.focusable = View.NOT_FOCUSABLE
        binding.mapView.isEnabled = false
        binding.loading.visibility = View.VISIBLE

        isOnWater.enqueue(object : Callback<Data> {
            override fun onResponse(
                call: Call<Data>,
                response: Response<Data>?,
            ) {
                if (response!!.body() != null) {
                    water = response.body()!!.water == true

                    if (water == false) {
                        turnViewVisible()
                        findNavController().navigate(
                            SelectRouteFragmentDirections.actionNavSelectRouteToNavTrackingWithRoute(
                                curr_location.latitude.toString(),
                                curr_location.longitude.toString(),
                                dest_location!!.latitude.toString(),
                                dest_location!!.longitude.toString()
                            )
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "O destino não é terreno elegível.",
                            Toast.LENGTH_SHORT
                        ).show()
                        turnViewVisible()
                    }
                } else {
                    Toast.makeText(context, "Tente daqui a 1 minuto.", Toast.LENGTH_SHORT).show()
                    turnViewVisible()
                }
            }

            override fun onFailure(call: Call<Data>?, t: Throwable?) {
                Log.d("Error:", t.toString())
                Toast.makeText(context, "Error while checking destination", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnViewVisible() {
        binding.startRun.visibility = View.VISIBLE
        binding.mapView.focusable = View.FOCUSABLE
        binding.loading.visibility = View.INVISIBLE
        binding.mapView.isEnabled = true
    }
}