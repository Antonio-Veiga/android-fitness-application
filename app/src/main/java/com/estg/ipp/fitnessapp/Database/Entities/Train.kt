package com.estg.ipp.fitnessapp.Database.Entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@RequiresApi(Build.VERSION_CODES.O)
@Entity
data class Train(
    @PrimaryKey(autoGenerate = true)
    var trainId: Long = 0,
    var name: String,
    var favorite: Boolean = false,
    var dateCreation: Long = System.currentTimeMillis()/1000
) : Serializable
