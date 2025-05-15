package com.deixebledenkaito.despertapp.presentation.components.crearAlarma

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.despertapp.domain.model.Alarm
import com.deixebledenkaito.despertapp.domain.model.formState.AlarmFromState
import com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm.CreateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AlarmCreationCardViewModel @Inject constructor(
    private val createAlarmUseCase: CreateAlarmUseCase,
): ViewModel(){

    private val _formState = MutableStateFlow(AlarmFromState())


    @RequiresApi(Build.VERSION_CODES.O)
    fun createAlarm(userId: String, time: LocalTime, label: String, days: SnapshotStateList<Int>) {
        viewModelScope.launch {
            createAlarmUseCase(
                Alarm(
                    hour = time.hour,
                    minute = time.minute,
                    label = label,
                    activeDays = days.map { it }, // Firestore-friendly
                    enabled = true,
                    userId = userId

                )
            )
        }
    }
}