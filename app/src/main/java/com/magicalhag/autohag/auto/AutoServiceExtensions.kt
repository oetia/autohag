package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.view.Display
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface AutoServiceExtensions {

    suspend fun AutoService.findBOIAndClickCenter(blocks: List<Text.TextBlock>, text: String) {
        val boi = blocks.find(text)
        val boiCenter = boi.getCenter()
        if (dispatchGestureSequential(buildClick(boiCenter))) {
            this.log("findAndClickBOICenter: CLICKED '$text'")
        } else {
            this.log("findAndClickBOICenter: CFAILED '$text'")
        }
    }

    suspend fun AutoService.dispatchGestureSequential(gesture: GestureDescription): Boolean =
        suspendCoroutine { continuation ->
            log("SANITY SHIT")
            dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    return continuation.resumeWithException(Exception("Dispatch Cancelled"))
                }

                override fun onCompleted(gestureDescription: GestureDescription?) {
                    log("SANITY FUC")
                    super.onCompleted(gestureDescription)
                    return continuation.resume(true)
                }
            }, handler)
        }

    // @formatter:off
    suspend fun AutoService.recognizerProcessSequential(image: InputImage): Text =
        suspendCoroutine { continuation ->
            val task = recognizer.process(image)
            while (!task.isSuccessful and !task.isComplete) {} // this is so fucking jank.
            continuation.resume(task.result)
        }
    // @formatter:on

    suspend fun AutoService.takeScreenshotSequential(): AccessibilityService.ScreenshotResult =
        suspendCoroutine { continuation ->
            takeScreenshot(
                Display.DEFAULT_DISPLAY,
                screenshotExecutor,
                object : AccessibilityService.TakeScreenshotCallback {
                    override fun onFailure(errorCode: Int) {
                        continuation.resumeWithException(Exception("Screenshot Failed"))
                    }

                    override fun onSuccess(screenshot: AccessibilityService.ScreenshotResult) {
                        continuation.resume(screenshot)
                    }
                })
        }

}