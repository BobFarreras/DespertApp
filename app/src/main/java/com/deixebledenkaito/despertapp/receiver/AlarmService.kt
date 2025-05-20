package com.deixebledenkaito.despertapp.receiver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.deixebledenkaito.despertapp.R

class AlarmService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        // Creem un canal de notificació per les alarmes (obligatori a Android O+)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            // Construïm una notificació per mostrar durant el servei

            val notification = Notification.Builder(this, "alarm_channel")
                .setContentTitle("Alarma activa")
                .setContentText("Resol el repte per aturar l'alarma")
                .setSmallIcon(R.drawable.ic_alarm)
                .build()

            // Iniciem el servei en mode Primer Pla (foreground), el que impedeix que sigui aturat pel sistema
            startForeground(1, notification)
            Log.d("AlarmService", "Servei en primer pla iniciat amb notificació")

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}