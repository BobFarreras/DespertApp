package com.deixebledenkaito.despertapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import com.deixebledenkaito.despertapp.R
import com.deixebledenkaito.despertapp.ui.screens.AlarmChallengeActivity

// AlarmReceiver.kt
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Quan sona l'alarma, llança l'activitat de repte matemàtic
        Log.d("AlarmReceiver", "Alarma rebuda! Llançant activitat de repte")
        // ▶ Reprodueix el so
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
        mediaPlayer?.start()

        // ⏰ Llança l'activitat del repte matemàtic
        val activityIntent = Intent(context, AlarmChallengeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(activityIntent)
    }
}