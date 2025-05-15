package com.deixebledenkaito.despertapp.viewmodel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope

import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.receiver.AlarmReceiver
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    @ApplicationContext private val context: Context

) : ViewModel() {


    val alarms: StateFlow<List<AlarmEntity>> = repository.allAlarms.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    ) // LiveData per observar les alarmes

    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            Log.d("AlarmViewModel", "Afegint alarma: $alarm")
            repository.insert(alarm)
            scheduleAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            Log.d("AlarmViewModel", "Eliminant alarma: $alarm")
            repository.delete(alarm) // Elimina l'alarma de la BD
        }
    }
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(alarm: AlarmEntity) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id, // ID única per a cada alarma
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Configura el calendari per l’hora de l’alarma
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si l'hora és anterior a l'actual, avança un dia
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Programes una alarma exacta
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("AlarmViewModel", "Alarma programada per ${calendar.time}")
    }
}