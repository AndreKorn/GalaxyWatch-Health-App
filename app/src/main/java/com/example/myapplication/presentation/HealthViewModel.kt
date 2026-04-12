package com.example.myapplication.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.StepCounterManager
import com.example.myapplication.domain.TimeTextFormatter
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val uiDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val uiTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    private val stepCounterManager = StepCounterManager(
        context = application.applicationContext,
        onStepsChanged = { steps ->
            _uiState.update { it.copy(stepsToday = steps) }
        },
        onSensorUnavailable = {
            _uiState.update { it.copy(isStepSensorAvailable = false) }
        }
    )

    init {
        _uiState.update { it.copy(isStepSensorAvailable = stepCounterManager.isSensorAvailable) }
        updateClock()
        startClockTicker()
    }

    fun onActivityPermissionChanged(granted: Boolean) {
        _uiState.update { it.copy(hasActivityPermission = granted) }

        if (granted) {
            stepCounterManager.start()
        } else {
            stepCounterManager.stop()
        }
    }

    fun onHostResumed() {
        if (_uiState.value.hasActivityPermission) {
            stepCounterManager.start()
        }
    }

    fun onHostPaused() {
        stepCounterManager.stop()
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (isActive) {
                updateClock()
                delay(millisUntilNextMinute())
            }
        }
    }

    private fun updateClock() {
        val now = LocalDateTime.now()
        val nowTime = now.toLocalTime()
        val nowDate = now.toLocalDate()

        _uiState.update {
            it.copy(
                time = nowTime.format(uiTimeFormatter),
                timeText = TimeTextFormatter.format(nowTime),
                date = nowDate.format(uiDateFormatter)
            )
        }
    }

    private fun millisUntilNextMinute(): Long {
        val now = LocalTime.now()
        val nextMinute = now
            .plusMinutes(1)
            .withSecond(0)
            .withNano(0)

        return Duration.between(now, nextMinute).toMillis().coerceAtLeast(200L)
    }
}

