package com.example.reperibilita

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ACTIVATE -> {
                val number = intent.getStringExtra(EXTRA_NUMBER) ?: return
                ForwardingService.activate(context, number)
            }
            ACTION_DEACTIVATE -> {
                ForwardingService.deactivate(context)
            }
        }
    }

    companion object {
        const val ACTION_ACTIVATE = "com.example.reperibilita.ACTIVATE"
        const val ACTION_DEACTIVATE = "com.example.reperibilita.DEACTIVATE"
        const val EXTRA_NUMBER = "extra_number"
    }
}
