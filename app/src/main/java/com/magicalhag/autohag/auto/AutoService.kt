package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.magicalhag.autohag.R
import com.magicalhag.autohag.ScreenshotExecutor
import com.magicalhag.autohag.utils.DispatchUtils
import com.magicalhag.autohag.utils.StringUtils
import com.magicalhag.autohag.utils.TextBlockUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AutoService : AccessibilityService(), AutoServiceExtensions, StringUtils, TextBlockUtils,
    DispatchUtils {

    private lateinit var autoServiceUI: AutoServiceUI

    private val miscExecutor = Executor { command -> command.run() }
    val screenshotExecutor = ScreenshotExecutor()
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    // val ctx = newSingleThreadContext("asdf")
    val handlerThread = HandlerThread("asdf")
    val handler = Handler()

    private var currentRoutine = ""

    private val scope = MainScope()
    private lateinit var job: Job
    private var paused: Boolean = false
    private var iterationCount = 0

    // BASE_COLLECT
    private var backlogChecked = false

    // BASE_REMOVE_DORM_OPS
    private var removeToggledOn = false
    private var dormsCleared = 0


    suspend fun iterate() {
        val screenshot = takeScreenshotSequential()
        val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
        val image = InputImage.fromBitmap(bitmap!!, 0)
        val recognizerOut = recognizerProcessSequential(image)
        val text = recognizerOut.text // text on screen
        val blocks = recognizerOut.textBlocks

        for (block in blocks) {
            log(block.text)
        }

        // @formatter:off
        var screenState = "N/A"
        if (text.containsAll(arrayOf("friends", "archive", "depot"), this::log)) {
            screenState = getString(R.string.SCREEN_STATE_MAIN_MENU)
        } else if (text.containsAll(arrayOf("to the most recent stage"), this::log)) {
            screenState = getString(R.string.SCREEN_STATE_TERMINAL)
        } else if (text.containsAll(arrayOf("auto deploy", "start", "-\\d{1,2}"), this::log)) {
            screenState = getString(R.string.SCREEN_STATE_STAGE_SELECTED)
        } else if (text.containsAll(arrayOf("the roster for this operation cannot be changed", "mission", "start"), this::log)) {
            screenState = getString(R.string.SCREEN_STATE_OP_ROSTER_AUTO)
        } else if (text.containsAll(arrayOf("2x", "takeover", "unit limit"), this::log)) {
            screenState = getString(R.string.SCREEN_STATE_AUTO_DEPLOY)
        } else if (text.containsAll(arrayOf("mission", "results", "exp & lmd"), this::log)) {
            screenState = getString(R.string.SCREEN_STATE_MISSION_RESULTS)
        } else if (text.containsAll(arrayOf("restore", "sanity"))) {
            screenState = getString(R.string.SCREEN_STATE_RESTORE_SANITY)
        }
        log("SCREEN STATE: $screenState")
        // @formatter:on


        if (currentRoutine == "0SANITY") {
            // if - current state is main_menu -> click "all stages cleared"
            when (screenState) {
                getString(R.string.SCREEN_STATE_MAIN_MENU) -> {
                    findBOIAndClickCenter(blocks, "current")
                }

                getString(R.string.SCREEN_STATE_TERMINAL) -> {
                    findBOIAndClickCenter(blocks, "to the most recent stage")
                }

                getString(R.string.SCREEN_STATE_STAGE_SELECTED) -> {
                    findBOIAndClickCenter(blocks, "start")
                }

                getString(R.string.SCREEN_STATE_OP_ROSTER_AUTO) -> {
                    findBOIAndClickCenter(blocks, "mission")
                }

                getString(R.string.SCREEN_STATE_AUTO_DEPLOY) -> {
                    pause()
                    delay(1000 * 15)
                    unpause()
                }

                getString(R.string.SCREEN_STATE_MISSION_RESULTS) -> {
                    findBOIAndClickCenter(blocks, "mission")
                }

                getString(R.string.SCREEN_STATE_RESTORE_SANITY) -> {
                    pause()
                }
            }
        }
    }

    private suspend fun dispatchGestureSequential(gesture: GestureDescription): Boolean =
        suspendCoroutine { continuation ->
            log("SANITY SHIT")
            dispatchGesture(gesture, object : GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    return continuation.resume(false)
                }

                override fun onCompleted(gestureDescription: GestureDescription?) {
                    log("SANITY FUC")
                    super.onCompleted(gestureDescription)
                    return continuation.resume(true)
                }
            }, handler)
        }


    // gestures
    fun dispatch(gesture: GestureDescription): Boolean {
        return dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d(getString(R.string.log_tag), "Input Service: gestureCompleted")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d(getString(R.string.log_tag), "Input Service: gestureCancelled")
            }
        }, handler)
    }


    // util

    fun resetInternalState() {
        backlogChecked = false
        removeToggledOn = false
        dormsCleared = 0

        log("Internal State Reset")
    }

    // start activities
    fun launchArknights() {
        val launchIntent = Intent()
            .setComponent(ComponentName("com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        try {
            startActivity(launchIntent)
            log("Arknights Opened", true)
        } catch (e: Exception) {
            log("Arknights Failed to Open", true)
        }
    }

    fun initializeJob() {
        job = scope.launch {
            while (true) {
                if (!paused) {
                    iterate()
                    iterationCount += 1
                    log("ITERATION COUNT: $iterationCount")
                }
                delay(1000 * 3)
            }
        }
        log("JOB INITIALIZED")
    }

    fun getJobInitialized(): Boolean {
        return this::job.isInitialized
    }

    fun pause() {
        paused = true
        log("JOB PAUSED", true)
    }

    fun unpause() {
        paused = false
        log("JOB UNPAUSED", true)
    }

    fun setRoutine(newRoutine: String) {
        currentRoutine = newRoutine
        log("Routine is Now: $newRoutine", true)
    }

    // lifecycle
    override fun onServiceConnected() {
        super.onServiceConnected()
        autoServiceUI = AutoServiceUI(this)
        log("Service Connected")
    }

    override fun onCreate() {
        super.onCreate()
        log("Service Created")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        log("Service Destroyed")
    }

    override fun onInterrupt() {
        log("Service Interrupted")
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        log("AccessibilityEvent: $e")
    }

    fun log(message: Any) {
        Log.d(getString(R.string.log_tag), message.toString())
    }

    private fun log(message: Any, toast: Boolean = false) {
        Log.d(getString(R.string.log_tag), message.toString())
        if (toast) {
            miscExecutor.execute {
                Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

