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
        val candidateDay = (currentDayOfWeek + i - 1) % 7 + 1  // 1..7
        if (alarmDaysSorted.contains(candidateDay)) {
            val candidateDate = now.toLocalDate().plusDays(i.toLong())
            val candidateDateTime = LocalDateTime.of(candidateDate, alarmTime)

            // Si és avui i l’hora ja ha passat, ignora’l
            if (i == 0 && candidateDateTime.isBefore(now)) {
                continue
            }

            nextAlarmDateTime = candidateDateTime
            break
        }
    }

    // Si no hem trobat cap dia vàlid (molt improbable), afegim 7 dies a avui
    if (nextAlarmDateTime == null) {
        val nextDate = now.toLocalDate().plusDays(7)
        nextAlarmDateTime = LocalDateTime.of(nextDate, alarmTime)
    }

    return Duration.between(now, nextAlarmDateTime)
}
