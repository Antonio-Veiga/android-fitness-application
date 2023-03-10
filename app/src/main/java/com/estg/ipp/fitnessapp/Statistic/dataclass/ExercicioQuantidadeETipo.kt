package com.estg.ipp.fitnessapp.Statistic.dataclass

import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType
import java.io.Serializable


data class ExercicioQuantidadeETipo (
    var quantidade: Int,
    var name: String,
    var tipo:ExerciseType
): Serializable