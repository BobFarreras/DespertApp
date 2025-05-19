package com.deixebledenkaito.despertapp.receiver



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

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
        val alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: return
        val testModel = intent.getStringExtra("TEST_MODEL") ?: "Bàsic"
        val alarmSound = intent.getStringExtra("ALARM_SOUND") ?: "default"
        val challengeType = intent.getStringExtra("CHALLENGE_TYPE") ?: "Matemàtiques"

        val challengeIntent = Intent(context, AlarmChallengeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            putExtra("FROM_LOCK_SCREEN", true)
            putExtra("ALARM_SOUND", alarmSound)
            putExtra("TEST_MODEL", testModel)
            putExtra("CHALLENGE_TYPE", challengeType)
        }

        // Iniciar servei i activitat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, AlarmService::class.java))
        } else {
            context.startService(Intent(context, AlarmService::class.java))
        }
        context.startActivity(challengeIntent)

        // Reprogramar
        val repo = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
        CoroutineScope(Dispatchers.IO).launch {
            val alarm = repo.getAlarmById(alarmId)
            alarm?.let {
                AlarmViewModel(repo, context).addAlarm(it.copy())
            }
        }
    }
}