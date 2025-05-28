package com.deixebledenkaito.despertapp.ui.screens.challenge

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deixebledenkaito.despertapp.R

fun showChallengeNotification(
    context: Context,
    alarmId: Int,
    alarmSound: String,
    testModel: String,
    challengeType: String
) {
    val channelId = "challenge_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Despertador",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, AlarmChallengeActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        putExtra("ALARM_ID", alarmId)
        putExtra("ALARM_SOUND", alarmSound)
        putExtra("TEST_MODEL", testModel)
        putExtra("CHALLENGE_TYPE", challengeType)
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        alarmId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_alarm)
        .setContentTitle("L'alarma ha sonat")
        .setContentText("Toca per resoldre el repte i aturar-la")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    notificationManager.notify(alarmId, notification)
}
