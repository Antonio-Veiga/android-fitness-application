package com.estg.ipp.fitnessapp.tracking.checkWaterAPI.dataclasses

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("query"      ) var query     : String?  = null,
    @SerializedName("request_id" ) var requestId : String?  = null,
    @SerializedName("lat"        ) var lat       : Double?  = null,
    @SerializedName("lon"        ) var lon       : Double?  = null,
    @SerializedName("water"      ) var water     : Boolean? = null,
    @SerializedName("error"      ) var error     : String?  = null,
)
