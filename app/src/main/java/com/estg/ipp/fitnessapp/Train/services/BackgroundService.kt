package com.estg.ipp.fitnessapp.train.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.estg.ipp.fitnessapp.Database.FitnessDB
import com.estg.ipp.fitnessapp.Database.Queries.SessionWithTrain
import com.estg.ipp.fitnessapp.train.broadcasts.LowBatteryReceiver
import com.estg.ipp.fitnessapp.train.broadcasts.SessionTimePublisher
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RequiresApi(api = Build.VERSION_CODES.O)
class BackgroundService : Service() {

    /**
     * Alarm Manager
     */
    private lateinit var alarmManager: AlarmManager

    /**
     * List of training sessions
     */
    private lateinit var sessionsList: List<SessionWithTrain>

    /**
     * Battery Broadcast Receiver
     */
    private lateinit var batteryReceiver: LowBatteryReceiver

    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Registers the battery receiver
        batteryReceiver = LowBatteryReceiver()
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get Sessions List
        sessionsList = getAllSessionAfterDateTime(applicationContext, LocalDateTime.now())
        setAllAlarms(sessionsList)
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        removeAllAlarm()
        return super.stopService(name)
    }

    override fun onDestroy() {
        unregisterReceiver(batteryReceiver)
        super.onDestroy()
    }

    /**
     * Method responsible for returning the session list
     */
    private fun getAllSessionAfterDateTime(
        context: Context,
        day: LocalDateTime
    ): List<SessionWithTrain> {
        val dao = FitnessDB.getInstance(context)!!.fitnessDao()
        val executors = Executors.newFixedThreadPool(2)
        var listOfSessions = mutableListOf<SessionWithTrain>()

        executors.execute {
            listOfSessions =
                dao.getAllSessionsWithTrainingAfterADay(day)
        }

        executors.shutdown()

        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            return listOfSessions
        } catch (e: InterruptedException) {
            throw e
        }
    }

    /**
     * Method responsible for setting up an alarm to create a notification at the given time
     */
    private fun setAlarm(date: Long, name: String, id: Int) {
        // Create Intent
        val notifyIntent = Intent(applicationContext, SessionTimePublisher::class.java)
        notifyIntent.putExtra("Id", id)

        // Create Pending Intent
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, id, notifyIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Set Alarm
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, date, pendingIntent
        )
    }

    private fun removeAllAlarm() {
        if (sessionsList != null) {
            for (s in sessionsList) {
                val id = s.session.sessionId.toInt()

                val notifyIntent = Intent(applicationContext, SessionTimePublisher::class.java)
                notifyIntent.putExtra("Id", id)

                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext, id, notifyIntent, PendingIntent.FLAG_IMMUTABLE
                )
                // Cancel alarm
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    private fun setAllAlarms(sessionList: List<SessionWithTrain>) {
        for (s in sessionList) {
            val zdt: ZonedDateTime = s.session.day.atZone(ZoneId.systemDefault())
            val millis = zdt.toInstant().toEpochMilli()
            setAlarm(millis, s.train.name, s.session.sessionId.toInt())
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}