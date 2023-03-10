package com.estg.ipp.fitnessapp.Locations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses.Data

class DataViewModel : ViewModel(){
    val data: MutableLiveData<Data> by lazy {
        MutableLiveData<Data>()
    }
}