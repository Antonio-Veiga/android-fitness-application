package com.estg.ipp.fitnessapp.Home.dataclass

import com.google.gson.annotations.SerializedName


data class Sys (

    @SerializedName("type"    ) var type    : Int?    = null,
    @SerializedName("id"      ) var id      : Int?    = null,
    @SerializedName("message" ) var message : Double? = null,
    @SerializedName("country" ) var country : String? = null,
    @SerializedName("sunrise" ) var sunrise : Int?    = null,
    @SerializedName("sunset"  ) var sunset  : Int?    = null

)