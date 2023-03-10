package com.estg.ipp.fitnessapp.tracking

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.estg.ipp.fitnessapp.databinding.FragmentTrackingWithoutRouteBinding
import com.estg.ipp.fitnessapp.tracking.service.LocationService
import com.google.android.gms.maps.model.LatLng


class TrackingWithoutRouteFragment : Fragment() {
    private lateinit var binding: FragmentTrackingWithoutRouteBinding
    var mService: LocationService? = null
    var mIsBound: Boolean = false
    var previous_distance = 0.0
    var previous_lat: Double? = null
    var previous_lon: Double? = null
    val EARTH_RADIUS = 6371000
    var max_speed = 0.0
    val rad = Math.PI/180
    val set_size = 5
    var coords_list = ArrayList<LatLng>()

    //Used for binding and unbinding
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            val binder = iBinder as LocationService.BinderClass
            mService = binder.service
            mService?.LocationLiveData?.observe(viewLifecycleOwner, {

                binding.mediumVelocity.text = String.format("%.2f",previous_distance / ((SystemClock.elapsedRealtime() - binding.simpleChronometer.base)/1000)) + " km/h"

                Log.d("valor" , (previous_distance / ((SystemClock.elapsedRealtime() - binding.simpleChronometer.base)/1000)).toString())
                Log.d("distance" , previous_distance.toString())
                Log.d("time", (previous_distance - ((SystemClock.elapsedRealtime() - binding.simpleChronometer.base))/1000).toString())

                coords_list.add(LatLng(it.latitude, it.longitude))

                // speed
                binding.currSpeed.text = String.format("%.2f", (it.speed * 3.6)) + " km/h"


                if(it.speed > max_speed){
                    max_speed = it.speed.toDouble()
                    binding.maxSpeed.text = String.format("%.2f",(max_speed*3.6)) + " km/h"
                }

                // distance
                if( coords_list.size == set_size ){
                    var avg_lat = 0.0
                    var avg_lon = 0.0

                    for (value in coords_list){
                        avg_lat += value.latitude
                        avg_lon += value.longitude
                    }
                    avg_lat /= set_size
                    avg_lon /= set_size

                    if (previous_lat != null  && previous_lon != null){
                        var dist_travelled = haversine(previous_lat as Double,previous_lon as Double, avg_lat, avg_lon)

                        if(dist_travelled > 5) {
                            previous_distance += dist_travelled
                        }
                    }

                    previous_lat = avg_lat
                    previous_lon = avg_lon
                    coords_list.clear()
                }

                binding.disTravelled.text = String.format("%.2f",previous_distance) + " m"

            })
            mIsBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mIsBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTrackingWithoutRouteBinding.inflate(inflater, container, false)
        val root = binding.root


        binding.startRun.setOnClickListener{
            bindService()
            binding.simpleChronometer.base = SystemClock.elapsedRealtime()
            binding.simpleChronometer.start()
            it.visibility = View.INVISIBLE
            binding.endRun.visibility = View.VISIBLE
        }

        binding.endRun.setOnClickListener{
            val medium_speed:String  = binding.mediumVelocity.text.toString()
            val max_speed:String = binding.maxSpeed.text.toString()
            val distance_taveled:String = binding.disTravelled.text.toString()
            val time: String = binding.simpleChronometer.text.toString()

            binding.simpleChronometer.stop()

            val navController = Navigation.findNavController(it)
            navController.navigate(TrackingWithoutRouteFragmentDirections.actionNavTrackingWthoutRouteToFinishedRaceFragment(medium_speed,max_speed,distance_taveled,time))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindService()
    }

    /**
     * Used to bind to our service class
     */
    private fun bindService() {
        Intent(requireActivity(), LocationService::class.java).also { intent ->
            requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        mIsBound = true
    }

    /**
     * Used to unbind and stop our service class
     */
    private fun unbindService() {
        if (mIsBound){
            Intent(requireActivity(), LocationService::class.java).also { intent ->
                requireContext().unbindService(serviceConnection)
            }
            mIsBound = false
        }
    }

    private fun haversine(prev_lat: Double, prev_lon: Double, avg_lat:Double, avg_lon:Double): Double{

        var lat_1 = prev_lat * rad
        var lat_2 = avg_lat * rad

        var delta_lat = lat_2 - lat_1
        var delta_lon = (avg_lon - prev_lon) * rad

        var a = Math.sin(delta_lat/2) * Math.sin(delta_lat/2) +
                Math.cos(lat_1) * Math.cos(lat_2) *
                Math.sin(delta_lon/2) * Math.sin(delta_lon/2)

        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))

        return EARTH_RADIUS * c
    }
}