package com.example.myapplication.domain

import java.time.LocalTime

object TimeTextFormatter {
    fun format(localTime: LocalTime): String {
        val minute = localTime.minute
        val currentHourWord = hourWord(localTime.hour)
        val nextHourWord = hourWord((localTime.hour + 1) % 24)

        return when (minute) {
            0 -> "$currentHourWord Uhr"
            in 1..5 -> "kurz nach $currentHourWord Uhr"
            in 6..10 -> "nach $currentHourWord Uhr"
            in 11..14 -> "gleich viertel nach $currentHourWord"
            in 15..19 -> "viertel nach $currentHourWord"
            in 20..29 -> "gleich halb $nextHourWord"
            in 30..32 -> "halb $nextHourWord"
            in 33..40 -> "nach halb $nextHourWord"
            in 41..44 -> "gleich viertel vor $nextHourWord"
            45 -> "viertel vor $nextHourWord"
            in 46..50 -> "kurz nach viertel vor $nextHourWord"
            else -> "kurz vor $nextHourWord"
        }
    }

    private fun hourWord(hour24: Int): String {
        return when (hour24 % 12) {
            0 -> "zwoelf"
            1 -> "eins"
            2 -> "zwei"
            3 -> "drei"
            4 -> "vier"
            5 -> "fuenf"
            6 -> "sechs"
            7 -> "sieben"
            8 -> "acht"
            9 -> "neun"
            10 -> "zehn"
            else -> "elf"
        }
    }
}

