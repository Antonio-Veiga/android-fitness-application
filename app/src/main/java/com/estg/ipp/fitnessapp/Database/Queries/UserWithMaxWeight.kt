package com.estg.ipp.fitnessapp.Database.Queries

import androidx.room.Embedded
import androidx.room.Relation
import com.estg.ipp.fitnessapp.Database.Entities.User
import com.estg.ipp.fitnessapp.Database.Entities.Weight

data class UserWithMaxWeight(
    @Embedded val user: User,
    @Relation(
        parentColumn = "maxWeightId",
        entityColumn = "weightId"
    )
    val weight: Weight,
)