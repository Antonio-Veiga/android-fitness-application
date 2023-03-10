package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route

import com.google.gson.annotations.SerializedName


data class Steps (

  @SerializedName("from_index"  ) var fromIndex   : Int?         = null,
  @SerializedName("to_index"    ) var toIndex     : Int?         = null,
  @SerializedName("distance"    ) var distance    : Int?         = null,
  @SerializedName("time"        ) var time        : Double?      = null,
  @SerializedName("instruction" ) var instruction : Instruction? = Instruction()

)