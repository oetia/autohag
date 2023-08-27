package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.TakeScreenshotCallback
import android.graphics.Bitmap
import android.view.Display
import com.google.mlkit.vision.common.InputImage
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun AutoService.takeScreenshotSequential(): AccessibilityService.ScreenshotResult =
    suspendCoroutine {
        log("taking screenshot")

        takeScreenshot(Display.DEFAULT_DISPLAY, dispatcher.executor, object : TakeScreenshotCallback {
            override fun onSuccess(screenshot: AccessibilityService.ScreenshotResult) {
                log("screenshot taken")
                it.resume(screenshot)
            }

            override fun onFailure(errorCode: Int) {
                it.resumeWithException(Exception("Screenshot Failed"))
            }
        })
    }

suspend fun AutoService.getBitmapScreenshot(): Bitmap {
    val screenshot = takeScreenshotSequential()
    val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)

    if(bitmap != null) {
        return bitmap
    } else {
        throw Exception("Failed to extract Bitmap")
    }
}

suspend fun AutoService.getImageScreenshot(): InputImage {
    val bitmap = getBitmapScreenshot()
    val image = InputImage.fromBitmap(bitmap, 0)
    bitmap.hardwareBuffer.close()

    return image
}