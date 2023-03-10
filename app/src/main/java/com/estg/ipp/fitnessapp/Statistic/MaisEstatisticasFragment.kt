package com.estg.ipp.fitnessapp.Statistic

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.estg.ipp.fitnessapp.Database.Entities.Weight
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.databinding.FragmentMaisEstatisticasBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MaisEstatisticasFragment : Fragment() {
    private val args: MaisEstatisticasFragmentArgs by navArgs()
    private lateinit var binding: FragmentMaisEstatisticasBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMaisEstatisticasBinding.inflate(inflater, container, false)

        val dao = FitnessDB.getInstance(requireContext())!!.fitnessDao()
        val executors7 = Executors.newFixedThreadPool(2)
        val executors8 = Executors.newFixedThreadPool(2)



        var pesosDoUser: Array<Weight> = emptyArray()


        executors7.execute {
           pesosDoUser = dao.getAllWeight()
        }
        executors7.shutdown()
        try {
            executors7.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }



        if(pesosDoUser.isNotEmpty()) {
            binding.pesoOvertime.text =
                (args.pesoAtual.toFloat() - pesosDoUser.first().weight).toString()
            pesosDoUser = pesosDoUser.sortedByDescending { it.weight }.toTypedArray()
            binding.pesoMin.text = pesosDoUser.last().weight.toString()
            binding.pesoMax.text = pesosDoUser.first().weight.toString()
            args.user.maxWeightId = pesosDoUser.first().weightId.toLong()
            args.user.minWeightId = pesosDoUser.last().weightId.toLong()
            args.user.overtimeWeight =   binding.pesoOvertime.text.toString().toFloat()
            executors8.execute {
                dao.updateUser(args.user)
            }
            executors8.shutdown()
            try {
                executors8.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            } catch (e: InterruptedException) {
                throw e
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

}