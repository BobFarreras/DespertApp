package com.deixebledenkaito.despertapp.domain.di

import com.deixebledenkaito.despertapp.domain.repository.firestore.AlarmsRepository
import com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm.CreateAlarmUseCase
import com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm.GetAlarmsUseCase
import com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm.ToggleAlarmUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object AlarmModuleUseCase {
    @Provides
    fun provideCreateAlarmUseCase(repository: AlarmsRepository): CreateAlarmUseCase{
        return CreateAlarmUseCase(repository)
    }
    @Provides
    fun provideGetAlarmsUseCase(repository: AlarmsRepository): GetAlarmsUseCase{
        return GetAlarmsUseCase(repository)
    }
    @Provides
    fun provideToggleAlarmUseCase(repository: AlarmsRepository): ToggleAlarmUseCase{
        return ToggleAlarmUseCase(repository)
    }
}