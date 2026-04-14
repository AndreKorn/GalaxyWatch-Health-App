package com.example.myapplication.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

/**
 * Unit tests für TimeTextFormatter
 *
 * Testet alle Zeitbereiche gemäß README.md Spezifikation:
 * - :00 → "X Uhr"
 * - :01-:05 → "kurz nach X Uhr"
 * - :06-:10 → "nach X Uhr"
 * - :11-:14 → "gleich viertel nach X"
 * - :15-:19 → "viertel nach X"
 * - :20-:29 → "gleich halb Y" (Y = nächste Stunde)
 * - :30-:32 → "halb Y"
 * - :33-:40 → "nach halb Y"
 * - :41-:44 → "gleich viertel vor Y"
 * - :45 → "viertel vor Y"
 * - :46-:50 → "kurz nach viertel vor Y"
 * - :51-:59 → "kurz vor Y"
 */
class TimeTextFormatterTest {

    // Tests für 8:00 - 9:00 Uhr (wie im README Beispiel)

    @Test
    fun `gibt acht Uhr zurueck fuer 8 Uhr 00`() {
        val result = TimeTextFormatter.format(LocalTime.of(8, 0))
        assertEquals("acht Uhr", result)
    }

    @Test
    fun `gibt kurz nach acht Uhr zurueck fuer 8 Uhr 01 bis 05`() {
        val times = listOf(1, 2, 3, 4, 5)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("kurz nach acht Uhr fuer 8:$minute", "kurz nach acht Uhr", result)
        }
    }

    @Test
    fun `gibt nach acht Uhr zurueck fuer 8 Uhr 06 bis 10`() {
        val times = listOf(6, 7, 8, 9, 10)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("nach acht Uhr fuer 8:$minute", "nach acht Uhr", result)
        }
    }

    @Test
    fun `gibt gleich viertel nach acht zurueck fuer 8 Uhr 11 bis 14`() {
        val times = listOf(11, 12, 13, 14)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("gleich viertel nach acht fuer 8:$minute", "gleich viertel nach acht", result)
        }
    }

    @Test
    fun `gibt viertel nach acht zurueck fuer 8 Uhr 15 bis 19`() {
        val times = listOf(15, 16, 17, 18, 19)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("viertel nach acht fuer 8:$minute", "viertel nach acht", result)
        }
    }

    @Test
    fun `gibt gleich halb neun zurueck fuer 8 Uhr 20 bis 29`() {
        val times = listOf(20, 21, 22, 23, 24, 25, 26, 27, 28, 29)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("gleich halb neun fuer 8:$minute", "gleich halb neun", result)
        }
    }

    @Test
    fun `gibt halb neun zurueck fuer 8 Uhr 30 bis 32`() {
        val times = listOf(30, 31, 32)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("halb neun fuer 8:$minute", "halb neun", result)
        }
    }

    @Test
    fun `gibt nach halb neun zurueck fuer 8 Uhr 33 bis 40`() {
        val times = listOf(33, 34, 35, 36, 37, 38, 39, 40)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("nach halb neun fuer 8:$minute", "nach halb neun", result)
        }
    }

    @Test
    fun `gibt gleich viertel vor neun zurueck fuer 8 Uhr 41 bis 44`() {
        val times = listOf(41, 42, 43, 44)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("gleich viertel vor neun fuer 8:$minute", "gleich viertel vor neun", result)
        }
    }

    @Test
    fun `gibt viertel vor neun zurueck fuer 8 Uhr 45`() {
        val result = TimeTextFormatter.format(LocalTime.of(8, 45))
        assertEquals("viertel vor neun", result)
    }

    @Test
    fun `gibt kurz nach viertel vor neun zurueck fuer 8 Uhr 46 bis 50`() {
        val times = listOf(46, 47, 48, 49, 50)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("kurz nach viertel vor neun fuer 8:$minute", "kurz nach viertel vor neun", result)
        }
    }

    @Test
    fun `gibt kurz vor neun zurueck fuer 8 Uhr 51 bis 59`() {
        val times = listOf(51, 52, 53, 54, 55, 56, 57, 58, 59)
        times.forEach { minute ->
            val result = TimeTextFormatter.format(LocalTime.of(8, minute))
            assertEquals("kurz vor neun fuer 8:$minute", "kurz vor neun", result)
        }
    }

    @Test
    fun `gibt neun Uhr zurueck fuer 9 Uhr 00`() {
        val result = TimeTextFormatter.format(LocalTime.of(9, 0))
        assertEquals("neun Uhr", result)
    }

    // Edge Case Tests für verschiedene Stunden

    @Test
    fun `gibt zwoelf Uhr zurueck fuer Mitternacht`() {
        val result = TimeTextFormatter.format(LocalTime.of(0, 0))
        assertEquals("zwoelf Uhr", result)
    }

    @Test
    fun `gibt zwoelf Uhr zurueck fuer Mittag`() {
        val result = TimeTextFormatter.format(LocalTime.of(12, 0))
        assertEquals("zwoelf Uhr", result)
    }

    @Test
    fun `gibt halb eins zurueck fuer 0 Uhr 30`() {
        val result = TimeTextFormatter.format(LocalTime.of(0, 30))
        assertEquals("halb eins", result)
    }

    @Test
    fun `gibt halb eins zurueck fuer 12 Uhr 30`() {
        val result = TimeTextFormatter.format(LocalTime.of(12, 30))
        assertEquals("halb eins", result)
    }

    @Test
    fun `gibt viertel nach drei zurueck fuer 15 Uhr 15`() {
        val result = TimeTextFormatter.format(LocalTime.of(15, 15))
        assertEquals("viertel nach drei", result)
    }

    @Test
    fun `gibt halb elf zurueck fuer 22 Uhr 30`() {
        val result = TimeTextFormatter.format(LocalTime.of(22, 30))
        assertEquals("halb elf", result)
    }

    @Test
    fun `gibt kurz vor zwoelf zurueck fuer 23 Uhr 59`() {
        val result = TimeTextFormatter.format(LocalTime.of(23, 59))
        assertEquals("kurz vor zwoelf", result)
    }

    // Tests für Stunden-Übergänge (Mitternacht und Mittag)

    @Test
    fun `gibt halb zwoelf zurueck fuer 11 Uhr 30`() {
        val result = TimeTextFormatter.format(LocalTime.of(11, 30))
        assertEquals("halb zwoelf", result)
    }

    @Test
    fun `gibt halb zwoelf zurueck fuer 23 Uhr 30`() {
        val result = TimeTextFormatter.format(LocalTime.of(23, 30))
        assertEquals("halb zwoelf", result)
    }

    @Test
    fun `gibt viertel vor zwoelf zurueck fuer 11 Uhr 45`() {
        val result = TimeTextFormatter.format(LocalTime.of(11, 45))
        assertEquals("viertel vor zwoelf", result)
    }

    @Test
    fun `gibt viertel vor zwoelf zurueck fuer 23 Uhr 45`() {
        val result = TimeTextFormatter.format(LocalTime.of(23, 45))
        assertEquals("viertel vor zwoelf", result)
    }

    // Tests für alle 24 Stunden (volle Stunde)

    @Test
    fun `gibt korrekte Stundenwoerter fuer alle 24 Stunden`() {
        val expectedHours = mapOf(
            0 to "zwoelf Uhr",
            1 to "eins Uhr",
            2 to "zwei Uhr",
            3 to "drei Uhr",
            4 to "vier Uhr",
            5 to "fuenf Uhr",
            6 to "sechs Uhr",
            7 to "sieben Uhr",
            8 to "acht Uhr",
            9 to "neun Uhr",
            10 to "zehn Uhr",
            11 to "elf Uhr",
            12 to "zwoelf Uhr",
            13 to "eins Uhr",
            14 to "zwei Uhr",
            15 to "drei Uhr",
            16 to "vier Uhr",
            17 to "fuenf Uhr",
            18 to "sechs Uhr",
            19 to "sieben Uhr",
            20 to "acht Uhr",
            21 to "neun Uhr",
            22 to "zehn Uhr",
            23 to "elf Uhr"
        )

        expectedHours.forEach { (hour, expected) ->
            val result = TimeTextFormatter.format(LocalTime.of(hour, 0))
            assertEquals("Stunde $hour", expected, result)
        }
    }

    // Grenzwert-Tests für alle Minutenbereiche

    @Test
    fun `testet alle Grenzwerte der Minutenbereiche`() {
        // Test boundary values for each minute range
        val testCases = listOf(
            Triple(8, 0, "acht Uhr"),
            Triple(8, 1, "kurz nach acht Uhr"),
            Triple(8, 5, "kurz nach acht Uhr"),
            Triple(8, 6, "nach acht Uhr"),
            Triple(8, 10, "nach acht Uhr"),
            Triple(8, 11, "gleich viertel nach acht"),
            Triple(8, 14, "gleich viertel nach acht"),
            Triple(8, 15, "viertel nach acht"),
            Triple(8, 19, "viertel nach acht"),
            Triple(8, 20, "gleich halb neun"),
            Triple(8, 29, "gleich halb neun"),
            Triple(8, 30, "halb neun"),
            Triple(8, 32, "halb neun"),
            Triple(8, 33, "nach halb neun"),
            Triple(8, 40, "nach halb neun"),
            Triple(8, 41, "gleich viertel vor neun"),
            Triple(8, 44, "gleich viertel vor neun"),
            Triple(8, 45, "viertel vor neun"),
            Triple(8, 46, "kurz nach viertel vor neun"),
            Triple(8, 50, "kurz nach viertel vor neun"),
            Triple(8, 51, "kurz vor neun"),
            Triple(8, 59, "kurz vor neun")
        )

        testCases.forEach { (hour, minute, expected) ->
            val result = TimeTextFormatter.format(LocalTime.of(hour, minute))
            assertEquals("Fehler bei $hour:$minute", expected, result)
        }
    }
}
