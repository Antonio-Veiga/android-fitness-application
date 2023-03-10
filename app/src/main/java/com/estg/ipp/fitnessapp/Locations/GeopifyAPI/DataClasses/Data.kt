package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Data (

  @SerializedName("type"     ) var type     : String?             = null,
  @SerializedName("features" ) var features : ArrayList<Features> = arrayListOf()

):Serializable