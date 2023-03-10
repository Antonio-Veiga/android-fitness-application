package com.estg.ipp.fitnessapp.Database.Entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType
import java.io.Serializable

@RequiresApi(Build.VERSION_CODES.O)
@Entity(
    foreignKeys = [ForeignKey(
        entity = Train::class,
        parentColumns = arrayOf("trainId"),
        childColumns = arrayOf("trainId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class TrainingExercise(
    @PrimaryKey(autoGenerate = true)
    var trainingExerciseId: Long = 0,
    var trainId: Long = 0,
    var exerciseName: String,
    var imagePath: String = "",
    var type: ExerciseType,
    var quantity: Int,
) : Serializable
