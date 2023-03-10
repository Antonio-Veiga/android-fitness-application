package com.estg.ipp.fitnessapp.train.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.estg.ipp.fitnessapp.train.services.TrainingService

@RequiresApi(Build.VERSION_CODES.O)
class SessionTimePublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Create intent to service
        val intent1 = Intent(context, TrainingService::class.java)

        // Get extras and put them in the service intent
        val id = intent.getIntExtra("Id", 0)
        intent1.putExtra("Id", id)

        // Start service
        context.startService(intent1)
    }
}