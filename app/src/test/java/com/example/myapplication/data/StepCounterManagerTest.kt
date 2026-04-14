package com.example.myapplication.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.LocalDate

/**
 * Unit tests für StepCounterManager
 *
 * Testet die Logik für:
 * - Sensor-Verfügbarkeit
 * - Tägliche Schrittzählung
 * - Baseline-Reset bei Tag-Wechsel
 * - SharedPreferences-Speicherung
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class StepCounterManagerTest {

    private lateinit var context: Context
    private lateinit var onStepsChanged: (Int) -> Unit
    private lateinit var onSensorUnavailable: () -> Unit
    private lateinit var sensorManager: SensorManager
    private lateinit var stepCounterManager: StepCounterManager

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        onStepsChanged = mockk(relaxed = true)
        onSensorUnavailable = mockk(relaxed = true)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Clear shared preferences before each test
        context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    @After
    fun tearDown() {
        if (::stepCounterManager.isInitialized) {
            stepCounterManager.stop()
        }
        clearAllMocks()
    }

    @Test
    fun `gibt sensor verfuegbar zurueck wenn Sensor vorhanden ist`() {
        stepCounterManager = StepCounterManager(context, onStepsChanged, onSensorUnavailable)

        // Note: Robolectric may not have sensor, so we test the logic
        // In real environment with sensor, this would be true
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        assertEquals(sensor != null, stepCounterManager.isSensorAvailable)
    }

    @Test
    fun `ruft onSensorUnavailable auf wenn kein Sensor vorhanden ist`() {
        // Mock context with no sensor available
        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns null
        every { mockContext.getSharedPreferences(any(), any()) } returns
            context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

        val manager = StepCounterManager(mockContext, onStepsChanged, onSensorUnavailable)
        manager.start()

        verify(exactly = 1) { onSensorUnavailable() }
        verify(exactly = 0) { onStepsChanged(any()) }
    }

    @Test
    fun `berechnet Schritte korrekt bei erster Verwendung`() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)
        val mockSensor = mockk<Sensor>(relaxed = true)
        val mockEvent = mockSensorEvent(Sensor.TYPE_STEP_COUNTER, floatArrayOf(1000f))

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockSensor
        every { mockSensorManager.registerListener(any(), any(), any()) } returns true
        every { mockContext.getSharedPreferences(any(), any()) } returns
            context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

        val capturedSteps = mutableListOf<Int>()
        val stepsCallback: (Int) -> Unit = { steps -> capturedSteps.add(steps) }

        val manager = StepCounterManager(mockContext, stepsCallback, onSensorUnavailable)
        manager.onSensorChanged(mockEvent)

        // Bei erster Verwendung werden 0 Schritte angezeigt (Baseline = 1000)
        assertTrue(capturedSteps.isNotEmpty())
        assertEquals(0, capturedSteps.first())
    }

    @Test
    fun `berechnet Schritte korrekt bei ansteigenden Werten`() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)
        val mockSensor = mockk<Sensor>(relaxed = true)

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockSensor
        every { mockSensorManager.registerListener(any(), any(), any()) } returns true
        every { mockContext.getSharedPreferences(any(), any()) } returns
            context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

        val capturedSteps = mutableListOf<Int>()
        val stepsCallback: (Int) -> Unit = { steps -> capturedSteps.add(steps) }

        val manager = StepCounterManager(mockContext, stepsCallback, onSensorUnavailable)

        // Erste Messung: Baseline setzen
        val event1 = mockSensorEvent(Sensor.TYPE_STEP_COUNTER, floatArrayOf(1000f))
        manager.onSensorChanged(event1)

        // Zweite Messung: 50 Schritte mehr
        val event2 = mockSensorEvent(Sensor.TYPE_STEP_COUNTER, floatArrayOf(1050f))
        manager.onSensorChanged(event2)

        // Dritte Messung: weitere 100 Schritte
        val event3 = mockSensorEvent(Sensor.TYPE_STEP_COUNTER, floatArrayOf(1150f))
        manager.onSensorChanged(event3)

        assertEquals(3, capturedSteps.size)
        assertEquals(0, capturedSteps[0])
        assertEquals(50, capturedSteps[1])
        assertEquals(150, capturedSteps[2])
    }

    @Test
    fun `speichert Baseline in SharedPreferences`() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)
        val mockSensor = mockk<Sensor>(relaxed = true)

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockSensor
        every { mockContext.getSharedPreferences(any(), any()) } returns
            context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

        val manager = StepCounterManager(mockContext, onStepsChanged, onSensorUnavailable)

        val event = mockSensorEvent(Sensor.TYPE_STEP_COUNTER, floatArrayOf(2500f))
        manager.onSensorChanged(event)

        val prefs = context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)
        val baselineDate = prefs.getString("baseline_date", null)
        val baselineValue = prefs.getFloat("baseline_value", -1f)

        assertEquals(LocalDate.now().toString(), baselineDate)
        assertEquals(2500f, baselineValue)
    }

    @Test
    fun `ignoriert Sensor-Events von anderen Sensor-Typen`() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)
        val mockSensor = mockk<Sensor>(relaxed = true)

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockSensor
        every { mockContext.getSharedPreferences(any(), any()) } returns
            context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

        val capturedSteps = mutableListOf<Int>()
        val stepsCallback: (Int) -> Unit = { steps -> capturedSteps.add(steps) }

        val manager = StepCounterManager(mockContext, stepsCallback, onSensorUnavailable)

        // Event von falschem Sensor-Typ
        val wrongEvent = mockSensorEvent(Sensor.TYPE_ACCELEROMETER, floatArrayOf(1000f))
        manager.onSensorChanged(wrongEvent)

        // Sollte keine Schritte melden
        assertTrue(capturedSteps.isEmpty())
    }

    @Test
    fun `verhindert negative Schrittzahlen`() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)
        val mockSensor = mockk<Sensor>(relaxed = true)

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockSensor
        every { mockContext.getSharedPreferences(any(), any()) } returns
            context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

        val capturedSteps = mutableListOf<Int>()
        val stepsCallback: (Int) -> Unit = { steps -> capturedSteps.add(steps) }

        val manager = StepCounterManager(mockContext, stepsCallback, onSensorUnavailable)

        // Baseline setzen
        val event1 = mockSensorEvent(Sensor.TYPE_STEP_COUNTER, floatArrayOf(1000f))
        manager.onSensorChanged(event1)

        // Sensor gibt niedrigeren Wert zurück (sollte nicht vorkommen, aber sicher ist sicher)
        // Dies triggert einen Baseline-Reset
        val event2 = mockSensorEvent(Sensor.TYPE_STEP_COUNTER, floatArrayOf(500f))
        manager.onSensorChanged(event2)

        // Alle Schrittzahlen sollten >= 0 sein
        assertTrue(capturedSteps.all { it >= 0 })
    }

    @Test
    fun `start und stop registriert und deregistriert Listener`() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)
        val mockSensor = mockk<Sensor>(relaxed = true)

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockSensor
        every { mockSensorManager.registerListener(any(), any(), any()) } returns true
        every { mockSensorManager.unregisterListener(any<SensorEventListener>()) } just runs
        every { mockContext.getSharedPreferences(any(), any()) } returns
            context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

        val manager = StepCounterManager(mockContext, onStepsChanged, onSensorUnavailable)

        manager.start()
        verify(exactly = 1) { mockSensorManager.registerListener(any(), mockSensor, SensorManager.SENSOR_DELAY_NORMAL) }

        manager.stop()
        verify(exactly = 1) { mockSensorManager.unregisterListener(any<SensorEventListener>()) }
    }

    @Test
    fun `mehrfacher Aufruf von start registriert Listener nur einmal`() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)
        val mockSensor = mockk<Sensor>(relaxed = true)

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockSensor
        every { mockSensorManager.registerListener(any(), any(), any()) } returns true
        every { mockContext.getSharedPreferences(any(), any()) } returns
            context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

        val manager = StepCounterManager(mockContext, onStepsChanged, onSensorUnavailable)

        manager.start()
        manager.start()
        manager.start()

        // Sollte nur einmal registrieren
        verify(exactly = 1) { mockSensorManager.registerListener(any(), mockSensor, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    // Helper function to create mock SensorEvent
    private fun mockSensorEvent(sensorType: Int, values: FloatArray): SensorEvent {
        val sensorEvent = mockk<SensorEvent>(relaxed = true)
        val sensor = mockk<Sensor>(relaxed = true)

        every { sensor.type } returns sensorType
        every { sensorEvent.sensor } returns sensor
        every { sensorEvent.values } returns values

        return sensorEvent
    }
}
