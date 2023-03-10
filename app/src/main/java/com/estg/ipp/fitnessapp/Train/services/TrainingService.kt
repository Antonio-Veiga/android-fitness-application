package com.estg.ipp.fitnessapp.train.services

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.Queries.SessionWithTrain
import com.estg.ipp.fitnessapp.MainActivity
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.train.SessionExerciseFragment
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class TrainingService(name: String = "TrainingService") : IntentService(name) {
    private val CHANNEL_ID = "1"

    override fun onHandleIntent(intent: Intent?) {
        // Get Notification id from intent
        val notificationID = intent!!.getIntExtra("Id", 0)

        // Get Session
        val s = getSession(notificationID.toLong())
        val content = s.train.name

        // Create Notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setContentTitle(resources.getString(R.string.training_notification_title))
        builder.setContentText(content)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setAutoCancel(true)

        val bundle = Bundle()
        bundle.putSerializable(SessionExerciseFragment.ARG_SESSION, s.session)
        bundle.putString(SessionExerciseFragment.ARG_TITLE, s.train.name)

        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.nav_session_exercise)
            .setArguments(bundle)
            .createPendingIntent()

        builder.setContentIntent(pendingIntent)
        val notificationCompat = builder.build()

        // Create Notification Manager
        val managerCompat = NotificationManagerCompat.from(this)
        // Create channel if it doesn't exist
        if (managerCompat.getNotificationChannel(CHANNEL_ID) == null) {
            // Create Notification Channel
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            managerCompat.createNotificationChannel(channel)
        }

        managerCompat.notify(notificationID, notificationCompat)
    }

    private fun getSession(id: Long): SessionWithTrain {
        val dao = FitnessDB.getInstance(applicationContext)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        var session: SessionWithTrain? = null

        executors.execute {
            session = dao.getSessionWithTrainById(id)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            return session!!
        } catch (e: InterruptedException) {
            throw e
        }
    }
}