package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route

import com.google.gson.annotations.SerializedName


data class Legs (

  @SerializedName("distance" ) var distance : Int?             = null,
  @SerializedName("time"     ) var time     : Double?          = null,
  @SerializedName("steps"    ) var steps    : ArrayList<Steps> = arrayListOf()

)