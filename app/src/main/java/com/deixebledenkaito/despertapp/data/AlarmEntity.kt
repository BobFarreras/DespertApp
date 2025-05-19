package com.deixebledenkaito.despertapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// AlarmEntity.kt
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID autogenerat per cada alarma
    val hour: Int, // Hora de l'alarma
    val minute: Int, // Minut de l'alarma
    val daysOfWeek: List<Int>, // Dies de la setmana (1 = Dll, ..., 7 = Dg)
    val isActive: Boolean ,// Si l'alarma està activa o no
    val testModel: String = "Bàsic", // Nuevo campo para el modelo de prueba
    val name: String = "", // NOm de l'alarma
    val alarmSound: String = "default", // Nou camp per identificar el so
    val alarmSoundName: String = "So per defecte", // Nom amigable per mostrar a la UI
    val challengeType: String = "Matemàtiques" // Nou camp
)