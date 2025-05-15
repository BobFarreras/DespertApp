package com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm

import com.deixebledenkaito.despertapp.domain.model.Alarm
import com.deixebledenkaito.despertapp.domain.repository.firestore.AlarmsRepository
import javax.inject.Inject

class CreateAlarmUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository
) {
    suspend operator  fun invoke(alarm: Alarm){
        alarmsRepository.createAlarm(alarm)
    }
}