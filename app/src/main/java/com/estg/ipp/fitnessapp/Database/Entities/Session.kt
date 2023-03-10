package com.estg.ipp.fitnessapp.Database.Entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.estg.ipp.fitnessapp.Database.Converters
import java.io.Serializable
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Entity(
    foreignKeys = [ForeignKey(
        entity = Train::class,
        parentColumns = arrayOf("trainId"),
        childColumns = arrayOf("sessionTrainId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Session(
    @PrimaryKey(autoGenerate = true)
    var sessionId: Long = 0,
    val sessionTrainId: Long,
    @field:TypeConverters(Converters::class)
    var day: LocalDateTime,
    var status: Boolean = false
) : Serializable
