package com.estg.ipp.fitnessapp.train.shared

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType
import com.estg.ipp.fitnessapp.train.AddSessionFragment
import com.estg.ipp.fitnessapp.train.MainTrainingFragment
import com.estg.ipp.fitnessapp.train.services.BackgroundService
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
class SharedMethods {
    companion object {
        fun openAddSessionDialog(mainFrag: MainTrainingFragment, day: LocalDate) {
            val addDialog = AddSessionFragment.newInstance(mainFrag, day)
            addDialog.show(mainFrag.requireActivity().supportFragmentManager, null)
        }

        fun getQuantityToString(exerciseType: ExerciseType, quantity: Int): String {
            when (exerciseType) {
                ExerciseType.Time -> {
                    val hours: Int = quantity / 3600
                    val minutes: Int = quantity % 3600 / 60
                    val seconds: Int = quantity % 3600 % 60

                    var quantityString = ""

                    if (hours != 0) {
                        if (hours < 10) {
                            quantityString += "0"
                        }
                        quantityString += "${hours}:"

                        if (minutes < 10) {
                            quantityString += "0"
                        }
                        quantityString += "${minutes}:"

                        if (seconds < 10) {
                            quantityString += "0"
                        }
                        quantityString += "${seconds}h"
                    } else {
                        if (minutes != 0) {
                            quantityString += "${minutes}:"
                            if (seconds < 10) {
                                quantityString += "0"
                            }
                            quantityString += "${seconds}min"
                        } else {
                            quantityString += "${seconds}s"
                        }
                    }
                    return quantityString
                }
                ExerciseType.Distance -> {
                    val kilometers: Int = quantity / 1000
                    val meters: Int = quantity % 1000

                    val quantityString = if (kilometers != 0) {
                        "${kilometers},${meters}Km"
                    } else {
                        "${meters}m"
                    }
                    return quantityString
                }
                else -> {
                    return quantity.toString()
                }
            }
        }

        fun restartForegroundService(context: Context) {
            context.stopService(Intent(context, BackgroundService::class.java))
            context.startService(Intent(context, BackgroundService::class.java))
        }

        fun getSizeName(context: Context): String {
            var screenLayout = context.resources.configuration.screenLayout
            screenLayout = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
            return when (screenLayout) {
                Configuration.SCREENLAYOUT_SIZE_SMALL -> "small"
                Configuration.SCREENLAYOUT_SIZE_NORMAL -> "normal"
                Configuration.SCREENLAYOUT_SIZE_LARGE -> "large"
                4 -> "xlarge"
                else -> "undefined"
            }
        }
    }
}