package com.deixebledenkaito.despertapp.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deixebledenkaito.despertapp.R
import com.deixebledenkaito.despertapp.data.AlarmDatabase
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository

import com.deixebledenkaito.despertapp.ui.screens.challenge.AlarmChallengeActivity
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// AlarmReceiver.kt
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val challengeIntent = Intent(context, AlarmChallengeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            putExtra("FROM_LOCK_SCREEN", true)
        }

        // Afegim aquests flags per mostrar sobre el patró de bloqueig
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, AlarmService::class.java))
        } else {
            context.startService(Intent(context, AlarmService::class.java))
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            challengeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal si cal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal per notificacions d'alarma"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Alarma activa")
            .setContentText("Toca per resoldre el repte i aturar l'alarma")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true) // <<< Aquest és el punt clau
            .setAutoCancel(true)
            .build()


        context.startActivity(challengeIntent)

        // ==============  Reprogramar per la setmana següent ========================
        val repo = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
        CoroutineScope(Dispatchers.IO).launch {
            val alarm = repo.getAlarmById(intent?.getIntExtra("ALARM_ID", -1) ?: return@launch)
            alarm?.let {
                val nextAlarm = it.copy() // mateixa hora i dies
                AlarmViewModel(repo, context).addAlarm(nextAlarm) // o fes servir algun helper
            }
        }
    }
}