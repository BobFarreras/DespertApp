package com.deixebledenkaito.despertapp.ui.screens.components

import android.util.Log
import com.deixebledenkaito.despertapp.data.AlarmEntity
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime



fun calcularTempsRestant(alarm: AlarmEntity): Duration {
    val now = LocalDateTime.now()
    val currentDayOfWeek = now.dayOfWeek.value  // 1=Monday ... 7=Sunday

    // Els dies de la setmana en alarm.daysOfWeek s'han d'expressar també de 1 a 7 (o adaptar-ho)

    // Trobar el següent dia de l'alarma (pot ser avui)
    val alarmDaysSorted = alarm.daysOfWeek.sorted()

    // Busquem el següent dia d'activació que sigui avui o després
    var daysUntilNextAlarm = Int.MAX_VALUE

    var foundTodayButAlreadyPassed = false

    for (day in alarmDaysSorted) {
        val diff = if (day >= currentDayOfWeek) {
            day - currentDayOfWeek
        } else {
            7 - (currentDayOfWeek - day)
        }

        // Si és avui, cal comparar hores i minuts per saber si ja ha passat
        if (diff == 0) {
            val alarmTime = LocalTime.of(alarm.hour, alarm.minute)
            if (now.toLocalTime().isBefore(alarmTime)) {
                daysUntilNextAlarm = 0
                break
            } else {
                // Avui ha passat → marquem-ho, però seguim buscant
                foundTodayButAlreadyPassed = true
                // ja ha passat avui, buscar proper dia
                continue
            }
        } else {
            // Guardem el mínim
            if (diff < daysUntilNextAlarm) {
                daysUntilNextAlarm = diff
            }
        }

        Log.d("TempsRestant", "Ara és: $now")
        Log.d("TempsRestant", "Proper dia: $daysUntilNextAlarm dies")
        Log.d("TempsRestant", "AlarmTime: ${alarm.hour}:${alarm.minute}")

    }
    // Si avui era el dia però ja ha passat, i no hem trobat cap altra activació abans → programar per la setmana vinent
    if (foundTodayButAlreadyPassed && daysUntilNextAlarm == Int.MAX_VALUE) {
        daysUntilNextAlarm = 7
    }

    if (daysUntilNextAlarm == Int.MAX_VALUE) {
        // Si no hi ha cap dia activat, retorna 0 o Duration.ZERO
        return Duration.ZERO
    }

    val nextAlarmDateTime = now
        .plusDays(daysUntilNextAlarm.toLong())
        .withHour(alarm.hour)
        .withMinute(alarm.minute)
        .withSecond(0)
        .withNano(0)

    return Duration.between(now, nextAlarmDateTime)
}