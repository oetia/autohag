package com.magicalhag.autohag

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.magicalhag.autohag.auto.AutoService
import java.util.Timer
import java.util.TimerTask


class ArknightsService : Service() {

    private val thread = Timer()
    private var counter = 0

    //    private val mpm = (MediaProjectionManager)getSystem
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate() {
        super.onCreate()

        startForeground(1, buildNotification())

        val callback = object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
                Log.d(getString(R.string.log_tag), "gesture completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                super.onCancelled(gestureDescription)
                Log.d(getString(R.string.log_tag), "gesture cancelled")
            }
        }

        thread.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
//                ass.dispatch(ass.buildClick(550f, 650f, 500))
                counter += 1
                Log.d(getString(R.string.log_tag), "" + counter)

                val intent = Intent(this@ArknightsService, AutoService::class.java)
                    .setAction("SCREENSHOT")
                startService(intent)


//                val intent = Intent(this@ArknightsService, AutoAccessibilityService::class.java)
//                    .setAction("CLICK")
//                    .putExtra("x", 550f)
//                    .putExtra("y", 650f)
//                    .putExtra("duration", 500)
//                startService(intent)
            }
        }, 0, 3000)

        Log.d(getString(R.string.log_tag), "Arknights Service Created")
    }

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingNotificationIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, ArknightsService::class.java)
            .setAction("STOP_SERVICE")
        val pendingStopIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        return NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setContentTitle("Arknights FS").setContentText("Running...")
            .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingNotificationIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", pendingStopIntent)
            .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(true)
            .build()
    }

    private fun openArknights() {
        val launchIntent = Intent().setComponent(
            ComponentName(
                "com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"
            )
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        try {
            startActivity(launchIntent)
            Log.d(getString(R.string.log_tag), "Arknights Opened")
        } catch (e: Exception) {
            stopSelf()
            Log.d(getString(R.string.log_tag), "Arknights Open Failed")
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == "STOP_SERVICE") {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        thread.cancel()
        Log.d(getString(R.string.log_tag), "Arknights Service Destroyed")
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not Needed")
    }
}