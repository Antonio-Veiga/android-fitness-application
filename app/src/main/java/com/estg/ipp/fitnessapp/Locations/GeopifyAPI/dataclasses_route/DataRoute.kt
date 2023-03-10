package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route

import com.google.gson.annotations.SerializedName


data class DataRoute (

  @SerializedName("features"   ) var features   : ArrayList<Features> = arrayListOf(),
  @SerializedName("properties" ) var properties : Properties?         = Properties(),
  @SerializedName("type"       ) var type       : String?             = null

)