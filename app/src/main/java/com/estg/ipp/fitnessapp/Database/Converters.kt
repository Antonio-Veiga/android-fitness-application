package com.estg.ipp.fitnessapp.Database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.estg.ipp.fitnessapp.Database.enumerations.ExerciseType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.util.*

class Converters {

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun restoreEnumList(enumList: String): List<ExerciseType> {
        if (enumList == null) {
            return Collections.emptyList()
        }

        val type: Type = object : TypeToken<List<ExerciseType?>?>() {}.type
        val gson = Gson()
        return gson.fromJson(enumList, type)
    }

    @TypeConverter
    fun saveEnumToString(enumType: List<ExerciseType>):String {
        val type = object : TypeToken<List<ExerciseType?>?>() {}.type
        val gson = Gson()
        return gson.toJson(enumType, type)
    }
}