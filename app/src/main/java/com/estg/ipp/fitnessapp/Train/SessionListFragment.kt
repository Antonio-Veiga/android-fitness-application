package com.estg.ipp.fitnessapp.train

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.Queries.SessionWithTrain
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.adapters.MySessionAdapter
import com.estg.ipp.fitnessapp.train.shared.SharedMethods
import com.estg.ipp.fitnessapp.train.view_models.SessionViewModel
import java.io.Serializable
import java.time.LocalDate

/**
 * A fragment representing a list of Sessions of Training.
 */
@RequiresApi(Build.VERSION_CODES.O)
class SessionListFragment(private var mainFrag: MainTrainingFragment) : DialogFragment() {

    private var columnCount = 1
    private lateinit var day: LocalDate
    private lateinit var sessionList: MutableList<SessionWithTrain>
    private val sessionModel: SessionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            day = it.getSerializable(DAY_OF_SESSION) as LocalDate
            sessionList = it.getSerializable(ARG_SESSION_LIST) as MutableList<SessionWithTrain>
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_session_list, null)

            val addButton = view.findViewById<Button>(R.id.add_session_button)
            val recyclerView = view.findViewById<RecyclerView>(R.id.list)

            // Set the adapter
            if (recyclerView != null) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }

                    sessionModel.sessionLiveData.observe(
                        this@SessionListFragment,
                        { item ->
                            item.sortBy { data -> data.session.day }
                            adapter = MySessionAdapter(
                                mainFrag,
                                item,
                                this@SessionListFragment
                            )
                        })

                    sessionModel.set(sessionList)
                }
            }

            addButton.setOnClickListener {
                SharedMethods.openAddSessionDialog(mainFrag, day)
            }

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        const val DAY_OF_SESSION = "day"
        const val ARG_SESSION_LIST = "session-list"

        @JvmStatic
        fun newInstance(
            mainFrag: MainTrainingFragment,
            columnCount: Int,
            day: LocalDate,
            sessionList: MutableList<SessionWithTrain>
        ) =
            SessionListFragment(mainFrag).apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putSerializable(DAY_OF_SESSION, day)
                    putSerializable(ARG_SESSION_LIST, sessionList as Serializable)
                }
            }
    }
}