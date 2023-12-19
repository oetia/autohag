package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.magicalhag.autohag.auto.games.arknights.arknights
import com.magicalhag.autohag.auto.utils.image.extractTextFromImage
import com.magicalhag.autohag.auto.utils.image.getImageScreenshot
import com.magicalhag.autohag.auto.utils.logging.log
import com.magicalhag.autohag.auto.utils.logging.toast
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AutoService : AccessibilityService() {

    private var autoServiceUI: AutoServiceUI? = null

    val mainHandler = Handler(Looper.getMainLooper())

    val backgroundExecutor:ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "background") }
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().setExecutor(backgroundExecutor).build())
    private val dispatcher = backgroundExecutor.asCoroutineDispatcher()

    val coroutineScope = CoroutineScope(CoroutineName("AutoServiceCoroutineScope") + dispatcher)

    private var heartbeat: Job? = null
    private var badumps = 0
    private var sleeping = true

    var task: String = ""

    suspend fun badump(testing: Boolean = false) {

        if (sleeping && !testing) {
            log("zzzZZZz")
            return
        }

        val image = getImageScreenshot()
        val ocrout = extractTextFromImage(image)

        // log(ocrout.text)

        try {
            arknights(ocrout)
        } catch (e: Exception) {
            println(e.stackTraceToString())
        }
    }

    fun BEEPBEEPBEEP() {
        toast("BEEPBEEPBEEP")
        sleeping = false
    }

    fun coma() {
        toast("coma induced")
        sleeping = true
    }

    suspend fun nap(duration: Long = 3000L) {
        log("taking a nap")
        sleeping = true
        delay(duration)
        sleeping = false
        log("woke up")
    }

    fun updateState(newState: String) {
        task = newState
        log("STATE: $task")
        toast("STATE: $task")
    }

    // lifecycle
    override fun onServiceConnected() {
        super.onServiceConnected()
        autoServiceUI = AutoServiceUI(this)
        heartbeat = coroutineScope.launch {
            while (true) {
                badumps += 1
                // log("*~badump~* ($badumps)")
                badump()
                delay(1000L)
            }
        }
        log("Auto Service Connected")
    }

    override fun onCreate() {
        super.onCreate()
        log("Auto Service Created")

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val asdf = alarmManager?.canScheduleExactAlarms()
        Log.d("FAGGOT", "$asdf")

        alarmManager?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 3000L,
            "CUNNY",
            { onAlarm() },
            mainHandler
        )
    }

    fun onAlarm() {
        log("CUNNY CUNNY CUNNT uoooooooooooooooooooooooooogh")
    }

    override fun onDestroy() {
        super.onDestroy()
        // handlerThread.quitSafely()
        coroutineScope.cancel()
        dispatcher.close()
        log("Auto Service Destroyed")
    }

    override fun onInterrupt() {
        log("Auto Service Interrupted")
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        log("AccessibilityEvent: $e")
    }
}

