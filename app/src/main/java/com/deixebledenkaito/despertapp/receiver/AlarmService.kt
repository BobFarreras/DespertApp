package com.deixebledenkaito.despertapp.receiver


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

import com.deixebledenkaito.despertapp.R

//Un Service és una component d’Android que fa feina en segon pla, però sense interfície d’usuari.
//
//Pot continuar corrent encara que l’usuari canviï d’aplicació.
//
//Un Foreground Service és un servei amb una notificació activa, obligatori a Android 8.0+ si vols que no el matin.
class AlarmService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)

        // Aquí pots iniciar el so de l’alarma o altra lògica
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificacions per alarmes"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "alarm_channel")
                .setContentTitle("Alarma activa")
                .setContentText("Resol el repte per aturar l'alarma")
                .setSmallIcon(R.drawable.ic_alarm)
                .setCategory(Notification.CATEGORY_ALARM)
                .setPriority(Notification.PRIORITY_HIGH)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("Alarma activa")
                .setContentText("Resol el repte per aturar l'alarma")
                .setSmallIcon(R.drawable.ic_alarm)
                .build()
        }
    }
}
