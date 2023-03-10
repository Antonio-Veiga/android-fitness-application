package com.estg.ipp.fitnessapp.runs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.estg.ipp.fitnessapp.Database.Entities.Run

class RunViewModel : ViewModel(){
    val data: MutableLiveData<List<Run>> by lazy {
        MutableLiveData<List<Run>>()
    }
}