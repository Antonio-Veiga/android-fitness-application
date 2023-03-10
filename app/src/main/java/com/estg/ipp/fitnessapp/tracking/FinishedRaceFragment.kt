package com.estg.ipp.fitnessapp.tracking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.estg.ipp.fitnessapp.Database.Entities.Run
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentFinishedRaceBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class FinishedRaceFragment : Fragment() {
    private lateinit var binding: FragmentFinishedRaceBinding
    val args: FinishedRaceFragmentArgs by navArgs()
    var weight: Float = 0F
    var calories_burned = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFinishedRaceBinding.inflate(inflater,container,false)
        val root = binding.root

        binding.disTravelled.text = args.distTraveled
        binding.maxVelocity.text = args.maxSpeed
        binding.mediumVelocity.text = args.mediumSpeed
        binding.time.text = args.totalTime

        val dao = FitnessDB.getInstance(requireContext())!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        executors.execute {
            weight = dao.getUsersAndActualWeight().get(0).weight.weight
            val units =
                args.totalTime.split(":".toRegex()).toTypedArray()

            val minutes = units[0].toInt()

            val seconds = units[1].toInt()

            val duration = (minutes/60 + seconds) /3600

            calories_burned =  10 * weight * duration

            val run = Run(max_velocity = args.maxSpeed, avg_velocity = args.mediumSpeed,
                distance = args.distTraveled, Time = args.totalTime, burned_calories = "${calories_burned} kcal")

            dao.insertRun(run)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }

        val units =
            args.totalTime.split(":".toRegex()).toTypedArray()

        val minutes = units[0].toInt()

        val seconds = units[1].toInt()

        val duration = (minutes/60 + seconds) /3600

        val calories_burned =  10 * weight * duration

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_nav_finished_race_to_nav_home)
        }

        binding.burntFat.text = "${calories_burned} kcal"

        val avgVelocity = args.mediumSpeed.replace(" km/h","").toDouble()
        val maxVelocity = args.maxSpeed.replace(" km/h", "").toDouble()
        val distance = args.distTraveled.replace(" m", "").toDouble()

        var summary = ""

        if(avgVelocity > 20.0){
            summary += requireContext().getString(R.string.high_avg_velocity)  + "($avgVelocity), "
        }else if(avgVelocity > 10.0 && avgVelocity < 20.0){
            summary += requireContext().getString(R.string.medium_avg_velocity) + "($avgVelocity), "
        }else if(avgVelocity < 10.0){
            summary += requireContext().getString(R.string.low_avg_velocity) + "($avgVelocity), "
        }

        if(maxVelocity > 30.0){
            summary +=requireContext().getString(R.string.high_max_velocity) + "($maxVelocity), "
        }else if(maxVelocity > 20.0 && maxVelocity < 30.0){
            summary += requireContext().getString(R.string.medium_max_velocity) + "($maxVelocity), "
        }else if(maxVelocity < 20.0){
            summary +=requireContext().getString(R.string.low_max_velocity) + "($maxVelocity), "
        }

        if(distance > 5000){
            summary +=  requireContext().getString(R.string.high_dis_travelled)+ "($distance)"
        }else if(distance > 2500 && distance < 5000){
            summary += requireContext().getString(R.string.medium_dis_travelled) + "($distance)"
        }else if(distance < 1000){
            summary += requireContext().getString(R.string.low_dis_travelled) + "($distance)"
        }

        binding.activitySumary.text = summary

        binding.shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${requireContext().getString(R.string.race_done)}\n" +
                        "${requireContext().getString(R.string.max_speed)}: ${args.maxSpeed}\n" +
                        "${requireContext().getString(R.string.medium_velocity)}: ${args.mediumSpeed}\n" +
                        "${requireContext().getString(R.string.distance_travelled)}: ${args.distTraveled}\n" +
                        "${requireContext().getString(R.string.timer)}: ${args.totalTime}")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        return root
    }
}