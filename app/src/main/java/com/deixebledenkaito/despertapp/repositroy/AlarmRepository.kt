package com.deixebledenkaito.despertapp.repositroy

import com.deixebledenkaito.despertapp.data.AlarmDao
import com.deixebledenkaito.despertapp.data.AlarmEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// 📦 com.despertapp.repository

// AlarmRepository.kt
class AlarmRepository @Inject constructor (private val dao: AlarmDao) {
    val allAlarms: Flow<List<AlarmEntity>> = dao.getAllAlarms() // Flux amb totes les alarmes

    suspend fun insert(alarm: AlarmEntity) = dao.insert(alarm) // Inserta alarma
    suspend fun delete(alarm: AlarmEntity) = dao.delete(alarm) // Elimina alarma
    suspend fun getAlarmById(alarmId: Int): AlarmEntity? = dao.getAlarmById(alarmId)
    suspend fun update(alarm: AlarmEntity) = dao.update(alarm)
    suspend fun getActiveAlarms(): List<AlarmEntity> {
        return dao.getActiveAlarms()
    }

}