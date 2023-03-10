package com.estg.ipp.fitnessapp.Home


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.Entities.Train
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Home.dataclass.SessaoENomeDoTreino
import com.estg.ipp.fitnessapp.Home.dataclass.WeatherDataClass
import com.estg.ipp.fitnessapp.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt



class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return this
                    }

                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                }).addOnSuccessListener { location: Location ->
                if (location != null) {
                    val weatherAPI = WeatherAPI.create()
                        .getWeather(location?.latitude.toString(), location?.longitude.toString())
                    weatherAPI.enqueue(object : Callback<WeatherDataClass> {
                        override fun onResponse(
                            call: Call<WeatherDataClass>,
                            response: Response<WeatherDataClass>?,
                        ) {
                            if (response!!.body() != null) {
                                val temperature: String = (response.body()!!.main!!.temp.toString()
                                    .toDouble() + (-273.15)).roundToInt().toString() + " ºC"
                                val clouds: String =
                                    response.body()!!.clouds!!.all.toString() + " %"
                                binding.temperatura.text = temperature
                                binding.clouds.text = clouds
                                binding.weather.text = response.body()!!.weather[0].main
                            }
                        }

                        override fun onFailure(call: Call<WeatherDataClass>?, t: Throwable?) {
                            Toast.makeText(
                                context,
                                "Não foi possivel ver a metereologia para a localização atual",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                } else {
                    Toast.makeText(
                        context,
                        "Não foi possivel obter a localização atual",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        val executors10 = Executors.newFixedThreadPool(2)
        val dao = FitnessDB.getInstance(requireContext())!!.fitnessDao()
        var listaDeSessoes: MutableList<Session> = ArrayList()
        var listaDeTreinos: MutableList<Train> = ArrayList()
        var listaDeTreinosESessoes: MutableList<SessaoENomeDoTreino> = ArrayList()

        executors10.execute {
            listaDeSessoes = dao.getAllSessionsOnADay(
                LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX)
            )
            listaDeTreinos = dao.getAllTrains()
            listaDeSessoes.removeAll { x -> x.status }
        }
        executors10.shutdown()
        try {
            executors10.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }
        for (a in 0 until listaDeSessoes.size) {
            for (b in 0 until listaDeTreinos.size) {
                if (listaDeTreinos[b].trainId == listaDeSessoes[a].sessionTrainId)
                    listaDeTreinosESessoes.add(
                        SessaoENomeDoTreino(
                            listaDeSessoes[a].day,
                            listaDeTreinos[b].name
                        )
                    )
            }
        }

        binding.recycleviewsessoes.adapter = ExerciciosHojeAdapter(listaDeTreinosESessoes)

        return binding.root
    }
}