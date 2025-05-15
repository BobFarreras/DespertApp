package com.deixebledenkaito.despertapp.presentation.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.deixebledenkaito.despertapp.MainActivity
import com.deixebledenkaito.despertapp.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarma recibida!")

        val alarmId = intent.getStringExtra("alarm_id") ?: run {
            Log.e("AlarmReceiver", "No se encontró alarm_id en el intent")
            return
        }

        val label = intent.getStringExtra("alarm_label") ?: "Alarma"
        Log.d("AlarmReceiver", "ID: $alarmId, Etiqueta: $label")

        showNotification(context, alarmId, label)
        startAlarmService(context)
    }

    private fun showNotification(context: Context, alarmId: String, label: String) {
        Log.d("AlarmReceiver", "Preparando notificación...")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // Crear canal (necesario para Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarmes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal per a notificacions d'alarma"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 500, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("alarm_id", alarmId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir notificación
        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(label)
            .setContentText("És hora de l'alarma!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .build()

        notificationManager.notify(alarmId.hashCode(), notification)
        Log.d("AlarmReceiver", "Notificación mostrada")
    }

    private fun startAlarmService(context: Context) {
        Log.d("AlarmReceiver", "Iniciando servicio de alarma...")
        val intent = Intent(context, AlarmSoundService::class.java).apply {
            putExtra("alarm_id", "current_alarm")
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            Log.d("AlarmReceiver", "Servicio de alarma iniciado")
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error al iniciar servicio: ${e.message}")
        }
    }
}