package com.estg.ipp.fitnessapp.Home.dataclass

import com.google.gson.annotations.SerializedName


data class WeatherDataClass (

    @SerializedName("coord"    ) var coord    : Coord?             = Coord(),
    @SerializedName("weather"  ) var weather  : ArrayList<Weather> = arrayListOf(),
    @SerializedName("base"     ) var base     : String?            = null,
    @SerializedName("main"     ) var main     : Main?              = Main(),
    @SerializedName("wind"     ) var wind     : Wind?              = Wind(),
    @SerializedName("clouds"   ) var clouds   : Clouds?            = Clouds(),
    @SerializedName("dt"       ) var dt       : Int?               = null,
    @SerializedName("sys"      ) var sys      : Sys?               = Sys(),
    @SerializedName("timezone" ) var timezone : Int?               = null,
    @SerializedName("id"       ) var id       : Int?               = null,
    @SerializedName("name"     ) var name     : String?            = null,
    @SerializedName("cod"      ) var cod      : Int?               = null
)