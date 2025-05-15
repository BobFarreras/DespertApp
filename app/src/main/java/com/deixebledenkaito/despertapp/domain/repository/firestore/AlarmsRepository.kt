package com.deixebledenkaito.despertapp.domain.repository.firestore

import com.deixebledenkaito.despertapp.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmsRepository {
    fun getAlarms(): Flow<List<Alarm>>
    suspend fun toggleAlarm(alarmId: String, enabled: Boolean)
    suspend fun createAlarm(alarm: Alarm)
}

