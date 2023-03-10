package com.estg.ipp.fitnessapp.train.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.estg.ipp.fitnessapp.Database.Entities.TrainingExercise
import java.io.Serializable

class ExerciseViewModel : ViewModel(), Serializable {
    val exerciseLiveData = MutableLiveData<MutableList<TrainingExercise>>()

    fun set(trainingExercises: MutableList<TrainingExercise>) {
        exerciseLiveData.value = trainingExercises
    }
}