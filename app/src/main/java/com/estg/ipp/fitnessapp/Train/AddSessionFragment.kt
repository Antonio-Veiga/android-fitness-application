package com.estg.ipp.fitnessapp.train

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.Entities.Train
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.Queries.SessionWithTrain
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.shared.SharedMethods
import com.estg.ipp.fitnessapp.train.view_models.SessionViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class AddSessionFragment(private val mainFrag: MainTrainingFragment) : DialogFragment() {
    private lateinit var day: LocalDate
    private val sessionModel: SessionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            day = it.getSerializable(ARG_DAY) as LocalDate
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val builder = AlertDialog.Builder(it)
            val titleView = inflater.inflate(R.layout.custom_dialog_title, null)
            builder.setCustomTitle(titleView)

            // Get the layout inflater
            val view = inflater.inflate(R.layout.fragment_add_session, null)
            val timePicker: TimePicker = view.findViewById(R.id.time_picker)
            val trainingView: AutoCompleteTextView = view.findViewById(R.id.train)
            val saveButton: Button = view.findViewById(R.id.save_button)
            val cancelButton: Button = view.findViewById(R.id.cancel_button)

            val listOfTrainings = getListOfTrainings(view.context)

            if (listOfTrainings.isEmpty()) {
                val toast = Toast.makeText(
                    context,
                    getString(R.string.empty_training_list),
                    Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                dismiss()
            }

            val trainNames = listOfTrainings.map { train -> train.name }

            val trainAdapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_list_item_1, trainNames
            )

            var selectedIndex: Int = -1
            trainingView.setAdapter(trainAdapter)
            trainingView.setOnItemClickListener { _, _, position, _ ->
                selectedIndex = position
            }

            saveButton.setOnClickListener {
                if (selectedIndex == -1) {
                    val toast = Toast.makeText(
                        context,
                        getString(R.string.select_train),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                } else {
                    val time = LocalTime.of(timePicker.hour, timePicker.minute)
                    val date = LocalDateTime.of(day, time)
                    if (LocalDateTime.now() > date) {
                        val toast = Toast.makeText(
                            context,
                            getString(R.string.select_valid_date),
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                    } else {
                        val session = Session(
                            sessionTrainId = listOfTrainings[selectedIndex].trainId,
                            day = date
                        )
                        val id = createSession(session, view.context)
                        mainFrag.updateEvents()
                        val data = sessionModel.sessionLiveData.value
                        if (data != null) {
                            session.sessionId = id
                            val sessionWithTrain =
                                SessionWithTrain(session, listOfTrainings[selectedIndex])
                            data.add(sessionWithTrain)
                            sessionModel.set(data)
                        }

                        // Restart ForegroundService
                        SharedMethods.restartForegroundService(requireContext())
                        dismiss()
                    }
                }
            }

            cancelButton.setOnClickListener {
                dismiss()
            }

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Method responsible for inserting a training session into the database.
     */
    private fun createSession(session: Session, context: Context): Long {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        var id = 0L
        executors.execute {
            id = dao.insertSession(session)
        }

        executors.shutdown()
        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            return id
        } catch (e: InterruptedException) {
            throw e
        }
    }

    /**
     * Method responsible for getting all training from database.
     */
    private fun getListOfTrainings(context: Context): List<Train> {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        var listOfTrains: List<Train>? = listOf()

        executors.execute {
            listOfTrains = dao.getAllTrains()
            if (listOfTrains == null)
                listOfTrains = listOf()
        }

        executors.shutdown()

        try {
            val dialog = ProgressDialog.show(
                context, "",
                getString(R.string.database_warning), true
            )
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            dialog.dismiss()
            return listOfTrains!!
        } catch (e: InterruptedException) {
            throw e
        }
    }

    companion object {
        const val ARG_DAY = "day"

        @JvmStatic
        fun newInstance(mainFrag: MainTrainingFragment, date: LocalDate) =
            AddSessionFragment(mainFrag).apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DAY, date)
                }
            }
    }
}