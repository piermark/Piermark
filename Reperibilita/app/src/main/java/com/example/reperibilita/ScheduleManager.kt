package com.example.reperibilita

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.Calendar

object ScheduleManager {
    private const val PREFS = "schedules"
    private const val KEY_NUMBER = "number"
    private const val KEY_NAME = "name"
    private const val KEY_INTERVALS = "intervals"
    private const val KEY_ENABLED = "enabled"

    fun schedule(context: Context, intervals: List<Interval>, number: String, name: String) {
        cancelAlarms(context)
        val prefs = prefs(context)
        prefs.edit()
            .putString(KEY_NUMBER, number)
            .putString(KEY_NAME, name)
            .putString(KEY_INTERVALS, serialize(intervals))
            .putBoolean(KEY_ENABLED, true)
            .apply()
        intervals.forEachIndexed { index, interval ->
            setAlarm(context, interval.start.timeInMillis, true, number, index)
            setAlarm(context, interval.end.timeInMillis, false, number, index)
        }
    }

    private fun setAlarm(context: Context, timeMillis: Long, activate: Boolean, number: String, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = if (activate) AlarmReceiver.ACTION_ACTIVATE else AlarmReceiver.ACTION_DEACTIVATE
            putExtra(AlarmReceiver.EXTRA_NUMBER, number)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            id * 2 + if (activate) 1 else 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pending)
    }

    fun restoreAlarms(context: Context) {
        val prefs = prefs(context)
        if (!prefs.getBoolean(KEY_ENABLED, false)) return
        val number = prefs.getString(KEY_NUMBER, null) ?: return
        val serialized = prefs.getString(KEY_INTERVALS, null) ?: return
        val intervals = deserialize(serialized)
        intervals.forEachIndexed { index, interval ->
            setAlarm(context, interval.start.timeInMillis, true, number, index)
            setAlarm(context, interval.end.timeInMillis, false, number, index)
        }
    }

    private fun cancelAlarms(context: Context) {
        val prefs = prefs(context)
        val number = prefs.getString(KEY_NUMBER, null) ?: return
        val serialized = prefs.getString(KEY_INTERVALS, null) ?: return
        val intervals = deserialize(serialized)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intervals.forEachIndexed { index, _ ->
            listOf(true, false).forEach { activate ->
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    action = if (activate) AlarmReceiver.ACTION_ACTIVATE else AlarmReceiver.ACTION_DEACTIVATE
                    putExtra(AlarmReceiver.EXTRA_NUMBER, number)
                }
                val pending = PendingIntent.getBroadcast(
                    context,
                    index * 2 + if (activate) 1 else 0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pending)
            }
        }
    }

    fun cancelSchedule(context: Context) {
        cancelAlarms(context)
        prefs(context).edit().putBoolean(KEY_ENABLED, false).apply()
    }

    fun getScheduledNumber(context: Context): String? =
        prefs(context).getString(KEY_NUMBER, null)

    fun getScheduledName(context: Context): String? =
        prefs(context).getString(KEY_NAME, null)

    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun getIntervals(context: Context): List<Interval> {
        val data = prefs(context).getString(KEY_INTERVALS, null) ?: return emptyList()
        return deserialize(data)
    }

    private fun serialize(intervals: List<Interval>): String =
        intervals.joinToString(";") { "${it.start.timeInMillis},${it.end.timeInMillis}" }

    private fun deserialize(data: String): List<Interval> =
        data.split(';').mapNotNull { part ->
            val pieces = part.split(',')
            if (pieces.size == 2) {
                val start = Calendar.getInstance().apply { timeInMillis = pieces[0].toLong() }
                val end = Calendar.getInstance().apply { timeInMillis = pieces[1].toLong() }
                Interval(start, end)
            } else null
        }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
