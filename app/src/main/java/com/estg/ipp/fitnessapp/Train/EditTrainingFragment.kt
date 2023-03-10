package com.estg.ipp.fitnessapp.train

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.estg.ipp.fitnessapp.Database.Entities.Train
import com.estg.ipp.fitnessapp.Database.Entities.TrainingExercise
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.Queries.TrainWithExercises
import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.adapters.MyExerciseAdapter
import com.estg.ipp.fitnessapp.train.view_models.ExerciseViewModel
import com.estg.ipp.fitnessapp.train.view_models.TrainingViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
@RequiresApi(Build.VERSION_CODES.O)
class EditTrainingFragment : Fragment() {

    private val trainingModel: TrainingViewModel by activityViewModels()
    private val exerciseModel: ExerciseViewModel by activityViewModels()
    private var training: TrainWithExercises? = null
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            training = it.getSerializable(ARG_TRAIN) as TrainWithExercises?
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.add_train_fragment, container, false)

        val trainName = view.findViewById<EditText>(R.id.train_name)
        val addButton = view.findViewById<Button>(R.id.add_exe_button)
        val submitButton = view.findViewById<Button>(R.id.submit_button)

        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        with(recyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }

            if (training != null) {
                trainName.setText(training!!.train.name)
                if (training!!.trainingExercises.isEmpty()) {
                    exerciseModel.set(
                        mutableListOf(
                            TrainingExercise(
                                exerciseName = "",
                                type = ExerciseType.None,
                                quantity = -1
                            )
                        )
                    )
                } else {
                    val exercises = training!!.trainingExercises.map { it.copy() }
                    exerciseModel.set(exercises.toMutableList())
                }
            } else {
                exerciseModel.set(
                    mutableListOf(
                        TrainingExercise(
                            exerciseName = "",
                            type = ExerciseType.None,
                            quantity = -1
                        )
                    )
                )
            }

            // Send list to the adapter
            adapter = MyExerciseAdapter(exerciseModel)

            // Add exercise listener
            addButton.setOnClickListener {
                val list = exerciseModel.exerciseLiveData.value!!
                val exercise = TrainingExercise(
                    exerciseName = "",
                    quantity = -1,
                    type = ExerciseType.None
                )
                if (training != null) {
                    exercise.trainId = training!!.train.trainId
                }
                list.add(exercise)
                adapter!!.notifyItemInserted(list.size - 1)
            }

            // Submit new train listener
            submitButton.setOnClickListener {
                if (trainName.text.toString() != "") {
                    var stop = false
                    var i = 0
                    for (exe in exerciseModel.exerciseLiveData.value!!) {
                        i++
                        if (exe.exerciseName == "") {
                            Toast.makeText(
                                context,
                                "Por favor, certifique-se de que selecionou o nome no" +
                                        " Exercício $i!",
                                Toast.LENGTH_LONG
                            ).show()
                            stop = true
                            break
                        }
                        if (exe.type == ExerciseType.None) {
                            Toast.makeText(
                                context,
                                "Por favor, certifique-se de que selecionou o unidade no" +
                                        " Exercício $i!",
                                Toast.LENGTH_LONG
                            ).show()
                            stop = true
                            break
                        }
                        if (exe.quantity == -1 || exe.quantity == 0) {
                            Toast.makeText(
                                context,
                                "Por favor, certifique-se de que escolher uma quantidade" +
                                        " válida no Exercício $i!",
                                Toast.LENGTH_LONG
                            ).show()
                            stop = true
                            break
                        }
                    }
                    if (!stop) {
                        if (training != null) {
                            training!!.train.name = trainName.text.toString()
                            val trainingList = trainingModel.trainLiveData.value!!
                            trainingList.remove(training!!)
                            training!!.trainingExercises = exerciseModel.exerciseLiveData.value!!
                            trainingList.add(training!!)
                            updateTraining(view.context, training!!)
                        } else {
                            training = TrainWithExercises(
                                train = Train(name = trainName.text.toString()),
                                exerciseModel.exerciseLiveData.value!!
                            )
                            insertNewTraining(view.context, training!!)
                        }
                        findNavController().navigateUp()
                    }
                } else {
                    Toast.makeText(
                        view.context,
                        "Por favor, certifique-se de que atribuiu um nome ao treino!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        return view
    }

    /**
     * Method responsible for updating the training in the database and
     * in the training live data.
     */
    private fun updateTraining(context: Context, training: TrainWithExercises) {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        executors.execute {
            dao.updateTraining(training.train)
            dao.deleteAllExercisesOfTrain(training.train.trainId)
            for (exe in training.trainingExercises) {
                dao.insertExercises(exe)
            }
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        } catch (e: InterruptedException) {
            throw e
        }
    }

    /**
     * Method responsible for inserting a new training into the database
     * and the training live data.
     */
    private fun insertNewTraining(context: Context, training: TrainWithExercises) {
        var trainList = mutableListOf<TrainWithExercises>()
        if (trainingModel.trainLiveData.value != null) {
            trainList = trainingModel.trainLiveData.value!!
        }
        var id = 0L

        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)

        executors.execute {
            id = dao.insertTrainingWithExercises(train = training.train, training.trainingExercises)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            training.train.trainId = id
            trainList.add(training)
        } catch (e: InterruptedException) {
            throw e
        }
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_TRAIN = "training"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            TrainingListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}