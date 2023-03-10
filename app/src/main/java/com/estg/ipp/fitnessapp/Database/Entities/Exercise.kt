package com.estg.ipp.fitnessapp.Database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType

@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    var exerciseId: Long = 0,
    var exerciseName: String,
    var imagePath: String,
    @ColumnInfo(name = "types_allowed")
    var typesAllowed: List<ExerciseType>,
)
