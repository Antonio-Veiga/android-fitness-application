package com.estg.ipp.fitnessapp.tracking

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.GeopifyAPI
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route.DataRoute
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route.Steps
import com.estg.ipp.fitnessapp.databinding.FragmentTrackingWithRouteBinding
import com.estg.ipp.fitnessapp.tracking.service.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class TrackingWithRouteFragment : Fragment(), TextToSpeech.OnInitListener, OnMapReadyCallback/*, RecognitionCallback*/{
    private lateinit var binding: FragmentTrackingWithRouteBinding
    val args: TrackingWithRouteFragmentArgs by navArgs()
    private lateinit var tts: TextToSpeech
    private lateinit var mView: MapView
    private lateinit var gMap: GoogleMap
    private lateinit var data_response: DataRoute
    private lateinit var steps: ArrayList<Steps>
    private var last_location :LatLng? = null
    private var current_pos = 0
    private var MAPVIEW_BUNDLE_KEY = "Tracking With Route Map"
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


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            val binder = iBinder as LocationService.BinderClass
            mService = binder.service
            mService?.LocationLiveData?.observe(viewLifecycleOwner, {

                binding.currentVelocity.text = String.format("%.2f", (it.speed * 3.6)) + " km/h"


                coords_list.add(LatLng(it.latitude, it.longitude))

                if(it.speed > max_speed){
                    max_speed = it.speed.toDouble()
                    binding.maxVelocity.text = String.format("%.2f",(max_speed * 3.6)) + " km/h"
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

                        /*
                        MARGIN FOR GPS ERROR
                         */
                        if(dist_travelled > 5) {
                            previous_distance += dist_travelled

                            /* Changes the bearing to position itself to the correct angle beetween it's position and
                        the destination
                        */
                            val bearing = bearing(
                                it.latitude,
                                it.longitude,
                                args.destLat.toDouble(),
                                args.destLon.toDouble()
                            )

                            val cameraPosition = CameraPosition.Builder()
                                .target(LatLng(it.latitude, it.longitude))
                                .zoom(28f)
                                .bearing(bearing.toFloat())
                                .tilt(50f)
                                .build()
                            /*
                        Add polyline
                         */
                            if (last_location == null) {
                                last_location =
                                    LatLng(args.currLat.toDouble(), args.currLon.toDouble())
                            }

                            gMap.addPolyline(
                                PolylineOptions()
                                    .add(last_location, LatLng(it.latitude, it.longitude))
                                    .width(5f)
                                    .color(Color.BLUE)
                            )

                            last_location = LatLng(it.latitude, it.longitude)

                            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        }
                    }

                    previous_lat = avg_lat
                    previous_lon = avg_lon
                    coords_list.clear()
                }

                binding.disTravelled.text = String.format("%.2f",previous_distance) + " m"

                if(haversine(it.latitude,it.longitude,args.destLat.toDouble(),args.destLon.toDouble()) < 5.00) {
                 binding.endRun.performClick()
                }

            })
            mIsBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mIsBound = false
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTrackingWithRouteBinding.inflate(inflater, container, false)
        val root = binding.root

        tts = TextToSpeech(context, this, "com.google.android.tts")

        val am = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        val amStreamMusicMaxVol = am!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2
        am.setStreamVolume(AudioManager.STREAM_MUSIC, amStreamMusicMaxVol, 0)

        mView = binding.map

        initGoogleMap(savedInstanceState)

        binding.startRun.setOnClickListener{
            bindService()
            it.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE

            val GeoRouteAPI = GeopifyAPI.create().getRoute("${args.currLat},${args.currLon}" +
                    "|${args.destLat},${args.destLon}")

            GeoRouteAPI.enqueue(object : Callback<DataRoute> {
                override fun onResponse(
                    call: Call<DataRoute>,
                    response: Response<DataRoute>?,
                ) {

                    if (response!!.body() != null) {
                        data_response = response.body()!!
                    }

                    binding.endRun.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE

                    steps = data_response.features[0].properties!!.legs!![0].steps
                    binding.time.base = SystemClock.elapsedRealtime()
                    binding.time.start()

                    speakOut(steps.get(current_pos).instruction!!.text!!)

                    var instruction = steps.get(current_pos).instruction!!.text!!

                    binding.jarvisLastMessage.text = instruction

                    binding.askJarvis.setOnClickListener {
                        if (binding.jarvisLastMessage.visibility == View.VISIBLE){
                            binding.jarvisLastMessage.visibility = View.INVISIBLE
                        }else{
                            binding.jarvisLastMessage.visibility = View.VISIBLE
                        }
                    }

                    binding.timer.setOnClickListener {
                        if (binding.time.visibility == View.VISIBLE){
                            binding.time.visibility = View.INVISIBLE
                        }else{
                            binding.time.visibility = View.VISIBLE
                        }
                    }


                    binding.previousButton.setOnClickListener {
                        if (current_pos == 0) {
                            speakOut("You're already on the first instruction.")
                        } else {
                            current_pos--

                            var instruction = steps.get(current_pos).instruction!!.text!!
                            binding.jarvisLastMessage.text = instruction
                            speakOut(instruction)
                        }
                    }

                    binding.nextButton.setOnClickListener {
                        if(current_pos + 1 == steps.size  ){
                            speakOut("You're already on the last instruction.")
                        }else{
                            current_pos++
                            var instruction = steps.get(current_pos).instruction!!.text!!
                            binding.jarvisLastMessage.text = instruction
                            speakOut(instruction)
                        }
                    }

                }
                override fun onFailure(call: Call<DataRoute>?, t: Throwable?) {
                    Log.d("Error:",t.toString())
                    Toast.makeText(context, "Error getting route!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            })
        }

        binding.endRun.setOnClickListener{
            val max_speed: String = binding.maxVelocity.text.toString()
            val distance_taveled: String = binding.disTravelled.text.toString()

            val medium_speed: String =  String.format("%.2f",previous_distance / (SystemClock.elapsedRealtime() - binding.time.base)/1000) + " km/h"

            binding.time.stop()
            val time: String = binding.time.text.toString()

            val navController = Navigation.findNavController(it)
            navController.navigate(TrackingWithRouteFragmentDirections.actionNavTrackingWithRouteToNavFinishedRace(medium_speed,max_speed,distance_taveled,time))
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
        if (mIsBound!!){
            Intent(requireActivity(), LocationService::class.java).also { intent ->
                requireContext().unbindService(serviceConnection)
            }
            mIsBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindService()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val a = HashSet<String>()

            a.add("male")

            var voice = Voice("en-us-x-sfg#male_2-local", Locale("en","US"),400,200,true,a)
            tts.setVoice(voice)
            tts.setSpeechRate(0.8f)

            var result = tts.setVoice(voice)

            if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported")
            } else {
                speakOut("Jarvis is active")
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
            Log.d("TTS",status.toString())
        }
    }

    private fun speakOut(message: String) {
        tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
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

    private fun bearing(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Double {
        val latitude1 = Math.toRadians(startLat)
        val latitude2 = Math.toRadians(endLat)
        val longDiff = Math.toRadians(endLng - startLng)
        val y = Math.sin(longDiff) * Math.cos(latitude2)
        val x =
            Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(
                longDiff
            )
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
                gMap = map

                val curr_location = LatLng(args.currLat.toDouble(),args.currLon.toDouble())

                val destination = LatLng(args.destLat.toDouble(),args.destLon.toDouble())

                map.addMarker(
                    MarkerOptions().position(curr_location)
                    .title("A tua localização atual"))

                map.addMarker(
                    MarkerOptions().position(destination)
                    .title("Destino"))

            val bearing = bearing(curr_location.latitude,curr_location.longitude,
            destination.latitude,destination.longitude)

            val cameraPosition = CameraPosition.Builder()
                .target(curr_location)
                .zoom(28f)
                .bearing(bearing.toFloat())
                .tilt(50f)
                .build()

            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        map.setMyLocationEnabled(true)
    }


/*
    override fun onBeginningOfSpeech() {
        TODO("Not yet implemented")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun onEndOfSpeech() {
        TODO("Not yet implemented")
    }

    override fun onError(errorCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onKeywordDetected() {
        TODO("Not yet implemented")
    }

    override fun onPartialResults(results: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onPrepared(status: RecognitionStatus) {
        TODO("Not yet implemented")
    }

    override fun onReadyForSpeech(params: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onResults(results: List<String>, scores: FloatArray?) {
        TODO("Not yet implemented")
    }

    override fun onRmsChanged(rmsdB: Float) {
        TODO("Not yet implemented")
    }
    */
}