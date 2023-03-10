package com.estg.ipp.fitnessapp.Database.Queries

import androidx.room.Embedded
import androidx.room.Relation
import com.estg.ipp.fitnessapp.Database.Entities.Session
import com.estg.ipp.fitnessapp.Database.Entities.Train
import java.io.Serializable

data class SessionWithTrain(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "sessionTrainId",
        entityColumn = "trainId"
    )
    val train: Train,
) : Serializable
