package com.deixebledenkaito.despertapp.receiver



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

import com.deixebledenkaito.despertapp.data.AlarmDatabase
import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository

import com.deixebledenkaito.despertapp.ui.screens.challenge.AlarmChallengeActivity
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

// AlarmReceiver.kt

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Obtenir dades de l'alarma
        val alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: return
        val testModel = intent.getStringExtra("TEST_MODEL") ?: "Bàsic"
        val alarmSound = intent.getStringExtra("ALARM_SOUND") ?: "default"
        val challengeType = intent.getStringExtra("CHALLENGE_TYPE") ?: "Matemàtiques"
        val repeatType = intent.getStringExtra("REPEAT_TYPE") ?: "Diàriament"

        Log.d("AlarmReceiver", "Alarma rebuda. ID: $alarmId, Repte: $challengeType, So: $alarmSound, Repetició: $repeatType")

        // Iniciar servei en primer pla
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_ID", alarmId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Iniciar activitat del repte
        val challengeIntent = Intent(context, AlarmChallengeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            putExtra("ALARM_SOUND", alarmSound)
            putExtra("TEST_MODEL", testModel)
            putExtra("CHALLENGE_TYPE", challengeType)
            putExtra("REPEAT_TYPE", repeatType)

        }
        context.startActivity(challengeIntent)

        // Si l'alarma és recurrent, reprogramem-la amb WorkManager; sinó, la desactivem
        if (repeatType != "Una vegada") {
            val workRequest = OneTimeWorkRequestBuilder<AlarmRescheduleWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES) // Esperar 1 minut per reprogramar
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
            Log.d("AlarmReceiver", "Alarma recurrent. Es reprogramarà amb WorkManager.")

        } else{
            // Desactivem l'alarma "Una vegada"
            val repo = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
            CoroutineScope(Dispatchers.IO).launch {
                val alarm = repo.getAlarmById(alarmId)
                alarm?.let {
                    repo.insert(it.copy(isActive = false))
                    Log.d("AlarmReceiver", "Alarma 'Una vegada' desactivada.")

                }
            }
        }
    }
}