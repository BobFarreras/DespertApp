package com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm

import com.deixebledenkaito.despertapp.domain.repository.firestore.AlarmsRepository
import javax.inject.Inject

class ToggleAlarmUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository
){
    suspend operator fun invoke(alarmId: String, enabled: Boolean){
        alarmsRepository.toggleAlarm(alarmId, enabled)

    }
}