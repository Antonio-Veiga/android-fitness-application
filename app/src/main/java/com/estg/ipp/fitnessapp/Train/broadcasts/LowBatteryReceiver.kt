package com.estg.ipp.fitnessapp.train.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.estg.ipp.fitnessapp.train.services.BackgroundService

@RequiresApi(Build.VERSION_CODES.O)
class LowBatteryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Battery Manager
        val myBatteryManager = context!!.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        // Services Intent
        val serviceIntent = Intent(context, BackgroundService::class.java)

        // If device isn't charging and with low battery the services are stopped
        if (intent!!.getBooleanExtra(
                BatteryManager.EXTRA_BATTERY_LOW,
                false
            ) && !myBatteryManager.isCharging
        ) {
            context.stopService(serviceIntent)
        }
    }

}