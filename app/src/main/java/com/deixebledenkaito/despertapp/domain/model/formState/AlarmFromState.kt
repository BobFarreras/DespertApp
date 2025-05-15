package com.deixebledenkaito.despertapp.domain.model.formState

class AlarmFromState (
    val id: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val label: String = "",
    val activeDays: List<Int> = emptyList(), // DayOfWeek.getValue()
    val enabled: Boolean = true
)