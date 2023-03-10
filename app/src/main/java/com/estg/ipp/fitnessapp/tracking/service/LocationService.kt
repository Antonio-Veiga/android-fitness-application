package com.estg.ipp.fitnessapp.tracking.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*


class LocationService: Service() {
    private val mBinder: IBinder = BinderClass()
    private val CHANNEL_ID = "Location Updates Foreground Service"
    val NOTIFICATION_TITLE = "Estámos a monitorizar a sua corrida"
    val NOTIFICATION_BODY = "Utilizamos a sua localização para calcular a velocidade a que se move e a distância que já percorreu"
    val LocationLiveData: MutableLiveData<Location> = MutableLiveData()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 100
        fastestInterval = 50
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        maxWaitTime = 100
    }

    var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
                        for (location in locationResult.locations){
                LocationLiveData.value = location

                if(LocationLiveData.value  == null){
                    LocationLiveData.value = location
                }else{
                    if (LocationLiveData.value!!.latitude != location.latitude &&
                        LocationLiveData.value!!.longitude != location.longitude){
                        LocationLiveData.value = location
                    }
                }
            }
        }
    }

    inner class BinderClass : Binder() {

        val service: LocationService
        get() = this@LocationService
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()

        startNotification()
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    private fun startNotification() {
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                "Location Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_BODY).build()
        startForeground(1, notification)
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
    }
}