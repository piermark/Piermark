package com.example.reperibilita

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class ForwardingService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ACTIVATE -> {
                val phoneNumber = intent.getStringExtra(EXTRA_NUMBER) ?: return START_NOT_STICKY
                startForeground(NOTIFICATION_ID, createNotification(phoneNumber))
                sendActivationCode(phoneNumber)
                return START_STICKY
            }
            ACTION_DEACTIVATE -> {
                sendDeactivationCode()
                stopForeground(true)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> return START_NOT_STICKY
        }
    }

    private fun createNotification(phoneNumber: String): Notification {
        val channelId = "reperibilita_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Reperibilita", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Reperibilità attiva")
            .setContentText("Inoltro verso $phoneNumber")
            .setSmallIcon(android.R.drawable.sym_call_outgoing)
            .setContentIntent(pending)
            .build()
    }

    private fun sendActivationCode(phoneNumber: String) {
        val ussd = "**21*+39${phoneNumber}#"
        val uri = Uri.parse("tel:" + Uri.encode(ussd))
        val intent = Intent(Intent.ACTION_CALL, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun sendDeactivationCode() {
        val uri = Uri.parse("tel:%23%23002%23")
        val intent = Intent(Intent.ACTION_CALL, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val EXTRA_NUMBER = "extra_number"
        private const val ACTION_ACTIVATE = "com.example.reperibilita.ACTIVATE_SERVICE"
        private const val ACTION_DEACTIVATE = "com.example.reperibilita.DEACTIVATE_SERVICE"

        fun activate(context: Context, number: String) {
            val intent = Intent(context, ForwardingService::class.java).apply {
                action = ACTION_ACTIVATE
                putExtra(EXTRA_NUMBER, number)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun deactivate(context: Context) {
            val intent = Intent(context, ForwardingService::class.java).apply {
                action = ACTION_DEACTIVATE
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
