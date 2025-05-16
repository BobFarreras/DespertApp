package com.deixebledenkaito.despertapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.os.Handler
import android.os.Looper

import android.util.Log
import com.deixebledenkaito.despertapp.ui.screens.AlarmChallengeActivity

import com.deixebledenkaito.despertapp.utils.AlarmUtils

// AlarmReceiver.kt
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarma rebuda! Llançant activitat de repte")

        val challengeIntent = Intent(context, AlarmChallengeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(challengeIntent)


    }
}
