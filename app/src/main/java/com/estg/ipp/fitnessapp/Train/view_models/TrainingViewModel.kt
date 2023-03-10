package com.estg.ipp.fitnessapp.train.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.estg.ipp.fitnessapp.Database.Queries.TrainWithExercises
import java.io.Serializable

class TrainingViewModel : ViewModel(), Serializable {
    val trainLiveData = MutableLiveData<MutableList<TrainWithExercises>>()

    fun set(trainings: MutableList<TrainWithExercises>) {
        trainLiveData.postValue(trainings)
    }
}