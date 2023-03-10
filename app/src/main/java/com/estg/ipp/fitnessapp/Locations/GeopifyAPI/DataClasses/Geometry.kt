package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Geometry (

  @SerializedName("type"        ) var type        : String?           = null,
  @SerializedName("coordinates" ) var coordinates : ArrayList<Double> = arrayListOf()

): Serializable