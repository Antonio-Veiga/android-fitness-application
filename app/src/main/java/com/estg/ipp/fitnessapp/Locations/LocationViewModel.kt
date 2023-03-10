package com.estg.ipp.fitnessapp.Locations

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel(){
    val location: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>()
    }
}