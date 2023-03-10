package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route

import com.google.gson.annotations.SerializedName


data class Properties (
  @SerializedName("mode"      ) var mode      : String?              = null,
  @SerializedName("waypoints" ) var waypoints : ArrayList<Waypoints> = arrayListOf(),
  @SerializedName("units"     ) var units     : String?              = null,
  @SerializedName("distance"  ) var distance  : Double?              = null,
  @SerializedName("distance_units") var distance_units: String?      = null,
  @SerializedName("time"      ) var time      : Double?              = null,
  @SerializedName("legs"      ) var legs      : ArrayList<Legs>?     = arrayListOf(),
)