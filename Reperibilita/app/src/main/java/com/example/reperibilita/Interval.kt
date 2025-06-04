package com.example.reperibilita

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Interval(val start: Calendar, val end: Calendar) {
    override fun toString(): String {
        val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return "${fmt.format(start.time)} - ${fmt.format(end.time)}"
    }
}
