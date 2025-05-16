package com.deixebledenkaito.despertapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

import android.os.Handler
import android.os.Looper

import android.util.Log
import com.deixebledenkaito.despertapp.ui.screens.AlarmChallengeActivity

import com.deixebledenkaito.despertapp.utils.AlarmUtils

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

        context.startActivity(challengeIntent)
    }
}