package com.estg.ipp.fitnessapp.Database.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.estg.ipp.fitnessapp.Database.Converters
import java.io.Serializable
import java.time.LocalDateTime

@Entity
data class Weight(
    @PrimaryKey(autoGenerate = true)
    var weightId: Int = 0,
    var weight: Float,
    @field:TypeConverters(Converters::class)
    var date: LocalDateTime,
) : Serializable
