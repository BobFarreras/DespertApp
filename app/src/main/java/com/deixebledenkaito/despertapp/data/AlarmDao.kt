package com.deixebledenkaito.despertapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// AlarmDao.kt
@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmEntity) // Inserta o reemplaça una alarma

    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): Flow<List<AlarmEntity>> // Retorna totes les alarmes com a flux reactiu

    @Delete
    suspend fun delete(alarm: AlarmEntity) // Elimina una alarma existent

    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Int): AlarmEntity? // Obté una alarma específica per ID
}