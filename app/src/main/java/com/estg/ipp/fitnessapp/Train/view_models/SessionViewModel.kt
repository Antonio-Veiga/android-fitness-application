package com.estg.ipp.fitnessapp.train.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.estg.ipp.fitnessapp.Database.Queries.SessionWithTrain

class SessionViewModel : ViewModel() {
    val sessionLiveData = MutableLiveData<MutableList<SessionWithTrain>>()

    fun set(sessions: MutableList<SessionWithTrain>) {
        sessionLiveData.postValue(sessions)
    }
}