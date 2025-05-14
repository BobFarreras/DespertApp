package com.deixebledenkaito.despertapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.app.*
import android.content.Context

import android.os.Build

import androidx.annotation.RequiresApi

import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        setContent {
            DespertAppTheme {
                AlarmScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "alarm_channel",
            "Alarma DespertApp",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Canal per alarmes amb repte"

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}