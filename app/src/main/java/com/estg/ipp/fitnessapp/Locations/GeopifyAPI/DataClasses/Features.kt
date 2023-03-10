package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Features (

  @SerializedName("type"       ) var type       : String?     = null,
  @SerializedName("properties" ) var properties : Properties? = Properties(),
  @SerializedName("geometry"   ) var geometry   : Geometry?   = Geometry()

):Serializable