package com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm

import com.deixebledenkaito.despertapp.domain.model.Alarm
import com.deixebledenkaito.despertapp.domain.repository.firestore.AlarmsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmsUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository
) {
    operator fun invoke(): Flow<List<Alarm>> {
        return alarmsRepository.getAlarms()
    }
}