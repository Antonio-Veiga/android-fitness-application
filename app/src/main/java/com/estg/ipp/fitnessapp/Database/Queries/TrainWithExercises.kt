package com.estg.ipp.fitnessapp.Database.Queries

import androidx.room.Embedded
import androidx.room.Relation
import com.estg.ipp.fitnessapp.Database.Entities.TrainingExercise
import com.estg.ipp.fitnessapp.Database.Entities.Train
import java.io.Serializable

data class TrainWithExercises(
    @Embedded val train: Train,
    @Relation(
        parentColumn = "trainId",
        entityColumn = "trainId"
    )
    var trainingExercises: List<TrainingExercise>
) : Serializable
