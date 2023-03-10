package com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Properties (

  @SerializedName("name"          ) var name         : String?           = null,
  @SerializedName("street"        ) var street       : String?           = null,
  @SerializedName("suburb"        ) var suburb       : String?           = null,
  @SerializedName("district"      ) var district     : String?           = null,
  @SerializedName("city"          ) var city         : String?           = null,
  @SerializedName("county"        ) var county       : String?           = null,
  @SerializedName("postcode"      ) var postcode     : String?           = null,
  @SerializedName("country"       ) var country      : String?           = null,
  @SerializedName("country_code"  ) var countryCode  : String?           = null,
  @SerializedName("lon"           ) var lon          : Double?           = null,
  @SerializedName("lat"           ) var lat          : Double?           = null,
  @SerializedName("formatted"     ) var formatted    : String?           = null,
  @SerializedName("address_line1" ) var addressLine1 : String?           = null,
  @SerializedName("address_line2" ) var addressLine2 : String?           = null,
  @SerializedName("categories"    ) var categories   : ArrayList<String> = arrayListOf(),
  @SerializedName("details"       ) var details      : ArrayList<String> = arrayListOf(),
  @SerializedName("datasource"    ) var datasource   : Datasource?       = Datasource(),
  @SerializedName("distance"      ) var distance     : String?           = null,
  @SerializedName("place_id"      ) var placeId      : String?           = null

): Serializable