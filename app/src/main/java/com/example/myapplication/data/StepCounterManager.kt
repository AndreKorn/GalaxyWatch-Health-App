package com.example.myapplication.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.time.LocalDate
import kotlin.math.max

class StepCounterManager(
    context: Context,
    private val onStepsChanged: (Int) -> Unit,
    private val onSensorUnavailable: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private var isRegistered = false

    val isSensorAvailable: Boolean
        get() = stepCounterSensor != null

    fun start() {
        val sensor = stepCounterSensor
        if (sensor == null) {
            onSensorUnavailable()
            return
        }

        if (!isRegistered) {
            isRegistered = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        if (isRegistered) {
            sensorManager.unregisterListener(this)
            isRegistered = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_STEP_COUNTER) {
            return
        }

        val totalStepsSinceBoot = event.values.firstOrNull() ?: return
        val today = LocalDate.now().toString()

        var baselineDate = preferences.getString(KEY_BASELINE_DATE, null)
        var baselineValue = preferences.getFloat(KEY_BASELINE_VALUE, -1f)

        if (baselineDate != today || baselineValue < 0f || totalStepsSinceBoot < baselineValue) {
            baselineDate = today
            baselineValue = totalStepsSinceBoot
            preferences.edit()
                .putString(KEY_BASELINE_DATE, baselineDate)
                .putFloat(KEY_BASELINE_VALUE, baselineValue)
                .apply()
        }

        val stepsToday = max(0, (totalStepsSinceBoot - baselineValue).toInt())
        onStepsChanged(stepsToday)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private companion object {
        const val PREFERENCES_NAME = "step_counter_prefs"
        const val KEY_BASELINE_DATE = "baseline_date"
        const val KEY_BASELINE_VALUE = "baseline_value"
    }
}

