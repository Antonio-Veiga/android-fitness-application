package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route

import com.google.gson.annotations.SerializedName


data class Geometry (

  @SerializedName("type"        ) var type        : String?                                 = null,
  @SerializedName("coordinates" ) var coordinates : ArrayList<ArrayList<ArrayList<Double>>> = arrayListOf()

)