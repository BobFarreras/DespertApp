package com.deixebledenkaito.despertapp.ui.screens.components

import android.util.Log
import com.deixebledenkaito.despertapp.data.AlarmEntity
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime



fun calcularTempsRestant(alarm: AlarmEntity): Duration {
    val now = LocalDateTime.now()
    val currentDayOfWeek = now.dayOfWeek.value  // 1=Monday ... 7=Sunday
    val alarmTime = LocalTime.of(alarm.hour, alarm.minute)

    val alarmDaysSorted = alarm.daysOfWeek.sorted()
    Log.d("calcularTempsRestant", "alarmDaysSorted: $alarmDaysSorted")
    Log.d("calcularTempsRestant", "currentDayOfWeek: $currentDayOfWeek")
    Log.d("calcularTempsRestant", "alarmTime: $alarmTime")
    Log.d("calcularTempsRestant", "now: $now")

    var nextAlarmDateTime: LocalDateTime? = null

    for (i in 0 until 7) {
        val day = (currentDayOfWeek + i - 1) % 7 + 1 // normalitzem entre 1 i 7
        if (alarmDaysSorted.contains(day)) {
            val candidateDate = now.toLocalDate().plusDays(i.toLong())
            val candidateDateTime = LocalDateTime.of(candidateDate, alarmTime)
            if (candidateDateTime.isAfter(now)) {
                nextAlarmDateTime = candidateDateTime
                break
            }
        }
    }

    return nextAlarmDateTime?.let { Duration.between(now, it) } ?: Duration.ZERO
}
