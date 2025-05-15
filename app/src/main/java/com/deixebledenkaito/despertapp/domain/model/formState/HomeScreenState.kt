package com.deixebledenkaito.despertapp.domain.model.formState

import com.deixebledenkaito.despertapp.domain.model.Alarm

data class HomeScreenState(
    val userName: String = "",
    val profileImageUrl: String? = null,
    val alarms: List<Alarm> = emptyList(),
    val isLoading: Boolean = false
)