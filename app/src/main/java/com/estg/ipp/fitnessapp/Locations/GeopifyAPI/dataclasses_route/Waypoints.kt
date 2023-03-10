package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route

import com.google.gson.annotations.SerializedName


data class Waypoints (

  @SerializedName("lat" ) var lat : Double? = null,
  @SerializedName("lon" ) var lon : Double? = null

)