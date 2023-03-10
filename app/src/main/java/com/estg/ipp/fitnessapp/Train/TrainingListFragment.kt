package com.estg.ipp.fitnessapp.train

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.adapters.MyTrainingAdapter
import com.estg.ipp.fitnessapp.train.view_models.TrainingViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A fragment representing a list of trainings.
 */
@RequiresApi(Build.VERSION_CODES.O)
class TrainingListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var columnCount = 1
    private val trainingModel: TrainingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_train_list, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.add_train_button)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_nav_train_list_to_addTrainFragment)
        }

        val orderSpinner = view.findViewById<Spinner>(R.id.order_type)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            R.array.order_training,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            orderSpinner.adapter = adapter
        }
        orderSpinner.setSelection(0)

        orderSpinner.onItemSelectedListener = this

        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        with(recyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            trainingModel.trainLiveData.observe(viewLifecycleOwner, { item ->
                when (orderSpinner.selectedItemPosition) {
                    0 -> {
                        item.sortWith(compareByDescending { it.train.dateCreation })
                    }
                    1 -> item.sortWith(compareByDescending { it.train.favorite })
                    else -> {
                        item.sortWith(compareBy { it.train.name })
                    }
                }
                adapter = MyTrainingAdapter(item)
            })
            getTrains(context)
        }
        return view
    }

    /**
     * Method responsible for getting the list of trainings in the database
     */
    private fun getTrains(context: Context) {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = FitnessDB.executors

        executors.execute {
            trainingModel.set(dao.getAllTrainsAndExercises())
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TrainingListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (trainingModel.trainLiveData.value != null)
            trainingModel.set(trainingModel.trainLiveData.value!!)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}