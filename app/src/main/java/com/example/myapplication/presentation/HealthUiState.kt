package com.example.myapplication.presentation

data class HealthUiState(
    val time: String = "--:--",
    val timeText: String = "-",
    val date: String = "--.--.----",
    val stepsToday: Int = 0,
    val hasActivityPermission: Boolean = false,
    val isStepSensorAvailable: Boolean = true
)

