package com.magicalhag.autohag

import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class ArknightsService : Service() {

    private val thread = Timer()
    private var counter = 0
    override fun onCreate() {
        super.onCreate()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingNotificationIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val stopIntent =
            Intent(this, ArknightsService::class.java).setAction("STOP_SERVICE")
        val pendingStopIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notification = NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setContentTitle("Arknights FS").setContentText("Running...")
            .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingNotificationIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", pendingStopIntent)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE).setOngoing(true).build()

        startForeground(1, notification)

        thread.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                counter += 1
                Log.d(getString(R.string.log_tag), "" + counter)
            }
        }, 0, 1000)

        Log.d(getString(R.string.log_tag), "Arknights Service Created")

        val launchIntent =
            packageManager.getLaunchIntentForPackage("com.YoStarEN.Arknights/com.u8.sdk.U8UnityContext")
        val intent = Intent().setComponent(
            ComponentName(
                "com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"
            )
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        try {
            startActivity(intent)
            Log.d(getString(R.string.log_tag), "Arknights Opened")
        } catch (e: Exception) {
            Log.d(getString(R.string.log_tag), "Failed to Open Arknights")
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        thread.cancel()
        Log.d(getString(R.string.log_tag), "Arknights Service Destroyed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == "STOP_SERVICE") {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}