package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route

import com.google.gson.annotations.SerializedName


data class Features (

  @SerializedName("type"       ) var type       : String?     = null,
  @SerializedName("properties" ) var properties : Properties? = Properties(),
  @SerializedName("geometry"   ) var geometry   : Geometry?   = Geometry()

)