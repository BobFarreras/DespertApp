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
import com.deixebledenkaito.despertapp.viewmodel.AlarmScheduler
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
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
        val alarmaName = intent.getStringExtra("NOM_ALARMA") ?: "No te nom"

        Log.d("AlarmReceiver", "Alarma rebuda. ID: $alarmId, Nom: $alarmaName, Repte: $challengeType, So: $alarmSound")

        // Iniciar servei en primer pla
//        Mantenir l'alarma activa encara que l'app estigui en segon pla.
//        Mostrar una notificació persistent mentre sona l'alarma (obligatori a Android O+).
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_ID", alarmId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Iniciar activitat del repte L'ACTIVIITY
//        Això obre la pantalla del repte (matemàtiques, memorització, etc.) que l'usuari ha de completar per aturar l'alarma.
        val challengeIntent = Intent(context, AlarmChallengeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            putExtra("ALARM_SOUND", alarmSound)
            putExtra("TEST_MODEL", testModel)
            putExtra("CHALLENGE_TYPE", challengeType)
        }
        context.startActivity(challengeIntent)

        // Utilitzem un scope amb SupervisorJob per gestionar errors
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val repo = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
                val alarm = repo.getAlarmById(alarmId)

                alarm?.let {
                    if (it.isRecurring) {
                        // Obtenim una còpia FRESCA de l'alarma original
                        val originalAlarm = repo.getAlarmById(alarmId) ?: return@launch
                        Log.d("AlarmReceiver", "Alarma Original Id: $alarmId, total $originalAlarm")
                        Log.d("AlarmReceiver", "Reprogramant alarma recurrent ID: $alarmId total $it")

                        // Calculem la propera activació abans de reprogramar
                        val nextTriggerTime = calculateNextTriggerTime(it)
                        // 2. Crear una còpia de l'alarma amb la nova hora
                        val updatedAlarm = it.copy(

                            hour = nextTriggerTime.hour,
                            minute = nextTriggerTime.minute
                        )
                        // 3. Actualitzar-la a la base de dades
                        repo.update(updatedAlarm)

                        // 2. Programem la nova alarma amb AlarmScheduler
                        AlarmScheduler(context).schedule(updatedAlarm)
                    } else {
                        // Desactivem l'alarma d'una sola vegada
                        repo.update(it.copy(isActive = false))
                        Log.d("AlarmReceiver", "Alarma 'Una vegada' desactivada correctament.")
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

        val nextDay = alarm.daysOfWeek
            .sorted()
            .firstOrNull { it > currentDayOfWeek }
            ?: alarm.daysOfWeek.first() // Si no n'hi ha, agafem el primer de la setmana següent

        val daysToAdd = if (nextDay > currentDayOfWeek) {
            nextDay - currentDayOfWeek
        } else {
            7 - (currentDayOfWeek - nextDay)
        }

        now.plusDays(daysToAdd.toLong()).toLocalTime()
    } else {
        // Per alarmes diàries, simplement afegim 24 hores
        LocalTime.of(alarm.hour, alarm.minute).plusHours(24)
    }
}