package com.estg.ipp.fitnessapp.train.threads

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.estg.ipp.fitnessapp.train.services.BackgroundService
@RequiresApi(Build.VERSION_CODES.O)
class StartBackgroundServiceThread(private var context: Context) : Thread() {

    override fun run() {

        val intent = Intent(context, BackgroundService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            Toast.makeText(context, "Erro ao correr o serviços.", Toast.LENGTH_LONG)
                .show()
        }
    }
}