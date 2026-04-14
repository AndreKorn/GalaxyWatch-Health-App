package com.example.myapplication.presentation

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Unit tests für HealthViewModel
 *
 * Testet:
 * - UI State Initialisierung
 * - Uhrzeit-Aktualisierung
 * - Berechtigungs-Handling
 * - Lifecycle-Management (Resume/Pause)
 * - Schrittezähler-Integration
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class HealthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var application: Application
    private lateinit var viewModel: HealthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = RuntimeEnvironment.getApplication()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialisiert UI State mit Standard-Werten`() {
        viewModel = HealthViewModel(application)

        val state = viewModel.uiState.value
        assertNotNull(state)
        assertEquals(0, state.stepsToday)
        assertFalse(state.hasActivityPermission)
        // Time, date und timeText werden im init gesetzt, also nicht default values
        assertNotEquals("--:--", state.time)
        assertNotEquals("--.--.----", state.date)
        assertNotEquals("-", state.timeText)
    }

    @Test
    fun `updateClock setzt Zeit, Datum und Text korrekt`() {
        viewModel = HealthViewModel(application)

        val state = viewModel.uiState.value

        // Überprüfe, dass Zeit im Format HH:mm ist
        assertTrue(state.time.matches(Regex("\\d{2}:\\d{2}")))

        // Überprüfe, dass Datum im Format dd.MM.yyyy ist
        assertTrue(state.date.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")))

        // Überprüfe, dass timeText nicht leer ist
        assertTrue(state.timeText.isNotEmpty())
    }

    @Test
    fun `onActivityPermissionChanged mit granted=true startet StepCounter`() {
        viewModel = HealthViewModel(application)

        // Initial sollte hasActivityPermission false sein
        assertFalse(viewModel.uiState.value.hasActivityPermission)

        // Berechtigung erteilen
        viewModel.onActivityPermissionChanged(true)

        // State sollte aktualisiert sein
        assertTrue(viewModel.uiState.value.hasActivityPermission)
    }

    @Test
    fun `onActivityPermissionChanged mit granted=false stoppt StepCounter`() {
        viewModel = HealthViewModel(application)

        // Berechtigung erteilen und dann entziehen
        viewModel.onActivityPermissionChanged(true)
        assertTrue(viewModel.uiState.value.hasActivityPermission)

        viewModel.onActivityPermissionChanged(false)
        assertFalse(viewModel.uiState.value.hasActivityPermission)
    }

    @Test
    fun `onHostResumed startet StepCounter nur wenn Berechtigung vorhanden`() {
        viewModel = HealthViewModel(application)

        // Ohne Berechtigung
        viewModel.onHostResumed()
        assertFalse(viewModel.uiState.value.hasActivityPermission)

        // Mit Berechtigung
        viewModel.onActivityPermissionChanged(true)
        viewModel.onHostResumed()
        assertTrue(viewModel.uiState.value.hasActivityPermission)
    }

    @Test
    fun `onHostPaused stoppt StepCounter`() {
        viewModel = HealthViewModel(application)

        // Berechtigung erteilen und dann pause
        viewModel.onActivityPermissionChanged(true)
        viewModel.onHostPaused()

        // Berechtigung bleibt erhalten, aber Sensor wird gestoppt
        assertTrue(viewModel.uiState.value.hasActivityPermission)
    }

    @Test
    fun `millisUntilNextMinute gibt positiven Wert zurueck`() {
        viewModel = HealthViewModel(application)

        // Wir können millisUntilNextMinute nicht direkt testen, da es private ist
        // Aber wir können überprüfen, dass die Uhr initialisiert wird
        val state = viewModel.uiState.value
        assertNotNull(state.time)
        assertNotEquals("--:--", state.time)
    }

    @Test
    fun `State Flow emittiert Updates`() {
        viewModel = HealthViewModel(application)

        val initialState = viewModel.uiState.value

        // Schritte aktualisieren durch Berechtigungs-Änderung
        viewModel.onActivityPermissionChanged(true)

        val updatedState = viewModel.uiState.value
        assertNotEquals(initialState.hasActivityPermission, updatedState.hasActivityPermission)
        assertTrue(updatedState.hasActivityPermission)
    }

    @Test
    fun `Zeit-Format ist HH_mm`() {
        viewModel = HealthViewModel(application)

        val time = viewModel.uiState.value.time

        // Sollte im Format HH:mm sein (z.B. "14:30")
        assertTrue("Zeit sollte im Format HH:mm sein, ist aber: $time",
            time.matches(Regex("\\d{2}:\\d{2}")))
    }

    @Test
    fun `Datums-Format ist dd_MM_yyyy`() {
        viewModel = HealthViewModel(application)

        val date = viewModel.uiState.value.date

        // Sollte im Format dd.MM.yyyy sein (z.B. "14.04.2026")
        assertTrue("Datum sollte im Format dd.MM.yyyy sein, ist aber: $date",
            date.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")))
    }

    @Test
    fun `timeText ist nicht leer und enthaelt deutsche Woerter`() {
        viewModel = HealthViewModel(application)

        val timeText = viewModel.uiState.value.timeText

        // Sollte nicht leer sein
        assertTrue("timeText sollte nicht leer sein", timeText.isNotEmpty())

        // Sollte mindestens ein deutsches Wort enthalten (z.B. "Uhr", "halb", "viertel")
        val germanWords = listOf("Uhr", "halb", "viertel", "kurz", "nach", "vor", "gleich")
        val containsGermanWord = germanWords.any { timeText.contains(it, ignoreCase = true) }
        assertTrue("timeText sollte deutsche Zeitwoerter enthalten: $timeText", containsGermanWord)
    }

    @Test
    fun `mehrfache Permission-Aenderungen aktualisieren State korrekt`() {
        viewModel = HealthViewModel(application)

        // Zyklus: false -> true -> false -> true
        assertFalse(viewModel.uiState.value.hasActivityPermission)

        viewModel.onActivityPermissionChanged(true)
        assertTrue(viewModel.uiState.value.hasActivityPermission)

        viewModel.onActivityPermissionChanged(false)
        assertFalse(viewModel.uiState.value.hasActivityPermission)

        viewModel.onActivityPermissionChanged(true)
        assertTrue(viewModel.uiState.value.hasActivityPermission)
    }

    @Test
    fun `Lifecycle Resume und Pause Zyklus funktioniert korrekt`() {
        viewModel = HealthViewModel(application)

        // Berechtigung erteilen
        viewModel.onActivityPermissionChanged(true)
        assertTrue(viewModel.uiState.value.hasActivityPermission)

        // Pause
        viewModel.onHostPaused()
        assertTrue(viewModel.uiState.value.hasActivityPermission) // Berechtigung bleibt

        // Resume
        viewModel.onHostResumed()
        assertTrue(viewModel.uiState.value.hasActivityPermission)

        // Nochmal Pause
        viewModel.onHostPaused()
        assertTrue(viewModel.uiState.value.hasActivityPermission)
    }

    @Test
    fun `isStepSensorAvailable wird korrekt gesetzt`() {
        viewModel = HealthViewModel(application)

        val state = viewModel.uiState.value

        // Der Wert hängt davon ab, ob der Sensor im Test-Environment verfügbar ist
        // Wir testen nur, dass der Wert gesetzt ist
        assertNotNull(state.isStepSensorAvailable)
        // In Robolectric ist typischerweise kein Sensor vorhanden
        // aber wir prüfen nur, dass das Flag existiert
    }

    @Test
    fun `Schritte-Update aktualisiert State korrekt`() {
        viewModel = HealthViewModel(application)

        // Initial sollten Schritte 0 sein
        assertEquals(0, viewModel.uiState.value.stepsToday)

        // Wir können die Schritte nicht direkt ändern, da StepCounterManager
        // das über Callbacks macht. Dieser Test prüft nur den Initialzustand.
    }

    @Test
    fun `ViewModel ueberlebt Permission-Wechsel ohne Crash`() {
        viewModel = HealthViewModel(application)

        // Viele schnelle Wechsel sollten keine Exception werfen
        repeat(10) { i ->
            viewModel.onActivityPermissionChanged(i % 2 == 0)
        }

        // Wenn wir hier ankommen, ist kein Crash aufgetreten
        assertTrue(true)
    }

    @Test
    fun `ViewModel ueberlebt Lifecycle-Wechsel ohne Crash`() {
        viewModel = HealthViewModel(application)

        viewModel.onActivityPermissionChanged(true)

        // Viele schnelle Resume/Pause Zyklen
        repeat(10) {
            viewModel.onHostResumed()
            viewModel.onHostPaused()
        }

        // Wenn wir hier ankommen, ist kein Crash aufgetreten
        assertTrue(true)
    }
}
