package com.deixebledenkaito.despertapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.deixebledenkaito.despertapp.data.AlarmDatabase
import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository
import com.deixebledenkaito.despertapp.ui.screens.challenge.AlarmChallengeActivity
import com.deixebledenkaito.despertapp.viewmodel.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// AlarmReceiver.kt

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // 1. Obtenir dades bàsiques
        val alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: return
        val testModel = intent.getStringExtra("TEST_MODEL") ?: "Bàsic"
        val alarmSound = intent.getStringExtra("ALARM_SOUND") ?: "default"
        val challengeType = intent.getStringExtra("CHALLENGE_TYPE") ?: "Matemàtiques"


        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val repo = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
                val alarm = repo.getAlarmById(alarmId)

                // 2. Validar si avui toca
                alarm?.let {
                    val currentDayOfWeek =
                        LocalDate.now().dayOfWeek.value % 7 // Dilluns = 1, ..., Diumenge = 0

                    if (it.isRecurring && it.daysOfWeek.isNotEmpty()) {
                        if (currentDayOfWeek !in it.daysOfWeek) {
                            Log.d(
                                "AlarmReceiver",
                                "Avui ($currentDayOfWeek) no és un dia seleccionat. Alarma cancel·lada."
                            )
                            return@launch // No continua
                        }
                    }

                    // 3. Iniciar el servei
                    val serviceIntent = Intent(context, AlarmService::class.java).apply {
                        putExtra("ALARM_ID", alarmId)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }

                    // 4. Obrir la pantalla del repte
                    val challengeIntent =
                        Intent(context, AlarmChallengeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                            putExtra("ALARM_ID", alarmId)
                            putExtra("ALARM_SOUND", alarmSound)
                            putExtra("TEST_MODEL", testModel)
                            putExtra("CHALLENGE_TYPE", challengeType)
                        }
                    context.startActivity(challengeIntent)

                    // 5. Reprogramar o desactivar segons tipus
                    if (it.isRecurring) {
                        val nextTriggerTime = calculateNextTriggerTime(it)
                        val updatedAlarm = it.copy(
                            hour = nextTriggerTime.hour,
                            minute = nextTriggerTime.minute
                        )
                        repo.update(updatedAlarm)
                        AlarmScheduler(context).schedule(updatedAlarm)
                    } else {
                        repo.update(it.copy(isActive = false))
                        Log.d("AlarmReceiver", "Alarma de 'una sola vegada' desactivada.")
                    }
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error en gestionar l'alarma", e)
            }
        }
    }
}


private fun calculateNextTriggerTime(alarm: AlarmEntity): LocalTime {
    return if (alarm.isRecurring && alarm.daysOfWeek.isNotEmpty()) {
        // Per alarmes setmanals, calculem el proper dia
        val now = LocalDateTime.now()
        val currentDayOfWeek = now.dayOfWeek.value % 7 // Convertim a 1-7 (Dilluns-Diumenge)
        Log.e("CalcularAlarmReceiver", "now: $now, currentDayOfWeek: $currentDayOfWeek")

        val nextDay = alarm.daysOfWeek
            .sorted()
            .firstOrNull { it > currentDayOfWeek }
            ?: alarm.daysOfWeek.first() // Si no n'hi ha, agafem el primer de la setmana següent

        Log.e("CalcularAlarmReceiver", "nextDay: $nextDay")
        val daysToAdd = if (nextDay > currentDayOfWeek) {
            nextDay - currentDayOfWeek
        } else {
            7 - (currentDayOfWeek - nextDay)

        }
        Log.e("CalcularAlarmReceiver", "daysToAdd: $daysToAdd")

        now.plusDays(daysToAdd.toLong()).toLocalTime()

    } else {
        // Per alarmes diàries, simplement afegim 24 hores
        LocalTime.of(alarm.hour, alarm.minute).plusHours(24)
    }
}