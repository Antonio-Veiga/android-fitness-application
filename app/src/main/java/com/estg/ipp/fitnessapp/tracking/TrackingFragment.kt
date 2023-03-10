package com.estg.ipp.fitnessapp.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.FragmentTrackingBinding


class TrackingFragment : Fragment() {
    private lateinit var binding: FragmentTrackingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentTrackingBinding.inflate(inflater, container, false)

        val root = binding.root


        binding.trackingBtn1.setOnClickListener{
            findNavController().navigate(R.id.action_nav_tracking_to_trackingWithoutRouteFragment)
        }

        binding.trackingBtn2.setOnClickListener{
            findNavController().navigate(R.id.action_nav_tracking_to_selectRouteFragment)
        }

        return root
    }

}