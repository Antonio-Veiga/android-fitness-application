package com.estg.ipp.fitnessapp.runs

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.estg.ipp.fitnessapp.Database.Entities.Run
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.databinding.FragmentRunsBinding
import java.util.concurrent.Executors


class RunsFragment : Fragment() {
    private lateinit var binding : FragmentRunsBinding
    private lateinit var dataViewModel: RunViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewModel = ViewModelProvider(requireActivity())[RunViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentRunsBinding.inflate(inflater,container,false)
        val root = binding.root
        val adapter = RunsAdapter(requireContext())
        val rv = binding.rv


        val dao = FitnessDB.getInstance(requireContext())!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        executors.execute {
            dataViewModel.data.postValue(dao.getAllRuns())
        }

        executors.shutdown()

        val dataObserver = Observer<List<Run>> { data ->
            if (data == null){
                binding.empty.visibility = View.INVISIBLE
            }else{
                adapter.data = data
                rv.adapter = adapter
            }
        }

        dataViewModel.data.observe(viewLifecycleOwner, dataObserver)

        return root
    }
}