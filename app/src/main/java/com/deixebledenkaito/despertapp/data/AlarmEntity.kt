package com.deixebledenkaito.despertapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// AlarmEntity.kt
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID autogenerat
    val hour: Int, // Hora de l'alarma (0-23)
    val minute: Int, // Minut de l'alarma (0-59)
    val daysOfWeek: List<Int>, // Dies activats (1=Dilluns, 7=Diumenge)
    val isActive: Boolean, // Estat d'activació
    val testModel: String = "Bàsic", // Dificultat del repte
    val name: String = "", // Nom descriptiu
    val alarmSound: String = "default", // ID del so
    val alarmSoundName: String = "So per defecte", // Nom del so
    val challengeType: String = "Matemàtiques", // Tipus de repte
    val isRecurring: Boolean = true
)