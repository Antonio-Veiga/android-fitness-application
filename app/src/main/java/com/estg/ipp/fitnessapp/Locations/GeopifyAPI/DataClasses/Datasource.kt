package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Datasource (

  @SerializedName("sourcename"  ) var sourcename  : String? = null,
  @SerializedName("attribution" ) var attribution : String? = null,
  @SerializedName("license"     ) var license     : String? = null,
  @SerializedName("url"         ) var url         : String? = null

): Serializable