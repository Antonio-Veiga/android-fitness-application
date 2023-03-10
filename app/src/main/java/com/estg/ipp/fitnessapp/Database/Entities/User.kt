package com.estg.ipp.fitnessapp.Database.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var age: Int?,
    var height: Float,
    var actualWeightId: Long?,
    var minWeightId: Long?,
    var maxWeightId: Long?,
    var overtimeWeight: Float,
    var trainNumber: Int = 0,
    var bmi: Float,
) : Serializable
