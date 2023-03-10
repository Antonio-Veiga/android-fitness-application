package com.estg.ipp.fitnessapp.train

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.Entities.TrainingExercise
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.adapters.MySessionExerciseAdapter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * A fragment representing a list of exercises of a session.
 */
@RequiresApi(Build.VERSION_CODES.O)
class SessionExerciseFragment : Fragment() {

    private var columnCount = 1
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            session = it.getSerializable(ARG_SESSION) as Session
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_session_exercise_list, container, false)
        // Set the adapter
        if (view is ViewPager2) {
            with(view) {
                adapter = MySessionExerciseAdapter(
                    getTrainingExercises(context),
                    session,
                    requireActivity(),
                    this
                )
                isUserInputEnabled = false
            }
        }
        return view
    }

    private fun getTrainingExercises(context: Context): List<TrainingExercise> {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        var listOfExercises = listOf<TrainingExercise>()

        executors.execute {
            listOfExercises = dao.getExercisesOfTrain(session.sessionTrainId)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            return listOfExercises
        } catch (e: InterruptedException) {
            throw e
        }
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_SESSION = "session"
        const val ARG_TITLE = "title"

        @JvmStatic
        fun newInstance(columnCount: Int, session: Session, title: String) =
            SessionExerciseFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putSerializable(ARG_SESSION, session)
                    putString(ARG_TITLE, title)
                }
            }
    }
}