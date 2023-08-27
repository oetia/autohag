package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.magicalhag.autohag.R
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class AutoService : AccessibilityService() {

    private var autoServiceUI: AutoServiceUI? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    val dispatcher =
        Executors.newSingleThreadExecutor { r -> Thread(r, "rorikon") }.asCoroutineDispatcher()

    private val coroutineScope = CoroutineScope(CoroutineName("AutoServiceScope") + dispatcher)
    private var heartbeat: Job? = null
    private var badumps = 0
    private var sleeping = true

    val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder().setExecutor(dispatcher.executor).build()
    )

    private suspend fun badump() {
        val image = getImageScreenshot()
        log(image.height)

        val visionText = extractTextFromImage(image)
        log(visionText.text)

        // choosing which decision to take

    }

    fun sleep() {
        sleeping = true
    }
    fun wakeup() {
        sleeping = false
    }

    // lifecycle
    override fun onServiceConnected() {
        super.onServiceConnected()
        autoServiceUI = AutoServiceUI(this)
        heartbeat = coroutineScope.launch {
            while (true) { // heartbeat is the entire thread

                if (sleeping) {
                    badump()
                    // log("zzzZZZzz")
                } else {

                }

                badumps += 1
                log("*~badump~* ($badumps)")
                delay(1000L)
            }
        }
        log("Auto Service Connected")
    }

    override fun onCreate() {
        super.onCreate()
        log("Auto Service Created")
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        dispatcher.close()
        log("Auto Service Destroyed")
    }

    override fun onInterrupt() {
        log("Auto Service Interrupted")
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        log("AccessibilityEvent: $e")
        if (e.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            log("the fuck?")
        }
    }

    // logging
    fun log(message: Any, toast: Boolean = false) {
        val threadName = Thread.currentThread().name
        val wrapper = "=".repeat(threadName.length + message.toString().length + 5 + 1)
        Log.d(
            getString(R.string.log_tag), "$wrapper\n[$threadName] - $message\n$wrapper"
        )
        if (toast && threadName == "main") {
            Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}

