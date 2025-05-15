package com.deixebledenkaito.despertapp.presentation.broadcast

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.deixebledenkaito.despertapp.domain.model.Alarm
import java.util.Calendar
import java.util.Date

class AlarmScheduler(private val context: Context) {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(alarm: Alarm) {
        try {
            Log.d("AlarmScheduler", "Intentando programar alarma ${alarm.id}")

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("alarm_id", alarm.id)
                putExtra("alarm_label", alarm.label)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerTime = calculateTriggerTime(alarm)
            Log.d("AlarmScheduler", "Hora de activación: ${Date(triggerTime)}")

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )

            Log.d("AlarmScheduler", "Alarma ${alarm.id} programada correctamente")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error al programar alarma: ${e.message}")
        }
    }

    private fun calculateTriggerTime(alarm: Alarm): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si la hora ya pasó hoy, programar para mañana
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            Log.d("AlarmScheduler", "La alarma es para mañana")
        }

        return calendar.timeInMillis
    }

    fun cancelAlarm(alarmId: String) {
        try {
            Log.d("AlarmScheduler", "Cancelando alarma $alarmId")
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d("AlarmScheduler", "Alarma $alarmId cancelada")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error al cancelar alarma: ${e.message}")
        }
    }
}