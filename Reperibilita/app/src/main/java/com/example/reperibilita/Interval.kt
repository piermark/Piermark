package com.example.reperibilita

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Interval(
    val start: Calendar,
    val end: Calendar,
    val number: String,
    val name: String
) {
    override fun toString(): String {
        val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val range = "${fmt.format(start.time)} - ${fmt.format(end.time)}"
        return "$range → $name"
    }
}
