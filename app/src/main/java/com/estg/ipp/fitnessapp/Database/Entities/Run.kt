package com.estg.ipp.fitnessapp.Database.Entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Entity @RequiresApi(Build.VERSION_CODES.O)
data class Run(
    @PrimaryKey(autoGenerate = true)
    var runId: Long = 0,
    var max_velocity: String,
    var avg_velocity: String,
    var distance: String,
    var date: LocalDateTime = LocalDateTime.now(),
    var Time: String,
    var burned_calories: String,
) : Serializable
