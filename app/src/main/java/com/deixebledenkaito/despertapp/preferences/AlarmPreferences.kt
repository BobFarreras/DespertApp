package com.deixebledenkaito.despertapp.preferences

data class AlarmPreferences(
    val volume: Int = 80,
    val vibrationEnabled: Boolean = true,
    val increasingVolume: Boolean = false
)