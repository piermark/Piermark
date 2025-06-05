package com.example.reperibilita

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.Calendar

object ScheduleManager {
    private const val PREFS = "schedules"
    private const val KEY_INTERVALS = "intervals"
    private const val KEY_ENABLED = "enabled"

    fun schedule(context: Context, intervals: List<Interval>) {
        cancelAlarms(context)
        val sorted = intervals.sortedBy { it.start.timeInMillis }
        prefs(context).edit()
            .putString(KEY_INTERVALS, serialize(sorted))
            .putBoolean(KEY_ENABLED, true)
            .apply()
        sorted.forEachIndexed { index, interval ->
            setAlarm(context, interval.start.timeInMillis, true, interval.number, index)
            val nextStartSame = sorted.getOrNull(index + 1)?.start?.timeInMillis == interval.end.timeInMillis
            if (!nextStartSame) {
                setAlarm(context, interval.end.timeInMillis, false, interval.number, index)
            }
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
        val serialized = prefs.getString(KEY_INTERVALS, null) ?: return
        val intervals = deserialize(serialized).sortedBy { it.start.timeInMillis }
        intervals.forEachIndexed { index, interval ->
            setAlarm(context, interval.start.timeInMillis, true, interval.number, index)
            val nextStartSame = intervals.getOrNull(index + 1)?.start?.timeInMillis == interval.end.timeInMillis
            if (!nextStartSame) {
                setAlarm(context, interval.end.timeInMillis, false, interval.number, index)
            }
        }
    }

    private fun cancelAlarms(context: Context) {
        val prefs = prefs(context)
        val serialized = prefs.getString(KEY_INTERVALS, null) ?: return
        val intervals = deserialize(serialized)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intervals.forEachIndexed { index, interval ->
            listOf(true, false).forEach { activate ->
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    action = if (activate) AlarmReceiver.ACTION_ACTIVATE else AlarmReceiver.ACTION_DEACTIVATE
                    putExtra(AlarmReceiver.EXTRA_NUMBER, interval.number)
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
        prefs(context).edit()
            .remove(KEY_INTERVALS)
            .putBoolean(KEY_ENABLED, false)
            .apply()
    }

    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun getIntervals(context: Context): List<Interval> {
        val data = prefs(context).getString(KEY_INTERVALS, null) ?: return emptyList()
        return deserialize(data).sortedBy { it.start.timeInMillis }
    }

    private fun serialize(intervals: List<Interval>): String {
        val arr = org.json.JSONArray()
        intervals.forEach { interval ->
            val obj = org.json.JSONObject()
            obj.put("s", interval.start.timeInMillis)
            obj.put("e", interval.end.timeInMillis)
            obj.put("n", interval.number)
            obj.put("name", interval.name)
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun deserialize(data: String): List<Interval> {
        val arr = org.json.JSONArray(data)
        val list = mutableListOf<Interval>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val start = Calendar.getInstance().apply { timeInMillis = obj.getLong("s") }
            val end = Calendar.getInstance().apply { timeInMillis = obj.getLong("e") }
            val number = obj.getString("n")
            val name = obj.getString("name")
            list.add(Interval(start, end, number, name))
        }
        return list
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
