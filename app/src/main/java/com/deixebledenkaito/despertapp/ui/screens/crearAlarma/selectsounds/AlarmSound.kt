package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds

// AlarmSound data class
data class AlarmSound(
    val id: String,
    val name: String,
    val resourceId: Int? = null,
    val startTimeMs: Long = 0L // Tant a AlarmSound com CustomAlarmSound
)
