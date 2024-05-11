package com.magicalhag.autohag.auto.core.image

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.TakeScreenshotCallback
import android.graphics.Bitmap
import android.view.Display
import com.google.mlkit.vision.common.InputImage
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun AutoService.takeScreenshotSequential(): AccessibilityService.ScreenshotResult =
    suspendCoroutine {

        takeScreenshot(Display.DEFAULT_DISPLAY, backgroundExecutor, object : TakeScreenshotCallback {
            override fun onSuccess(screenshot: AccessibilityService.ScreenshotResult) {
                log("Screenshot Taken - ${screenshot.timestamp}")
                it.resume(screenshot)
            }

            override fun onFailure(errorCode: Int) {
                it.resumeWithException(Exception("Screenshot Failed - $errorCode"))
            }
        })
    }

suspend fun AutoService.getBitmapScreenshot(): Bitmap {
    val screenshot = takeScreenshotSequential()
    val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)


    if(bitmap != null) {

        // val out = FileOutputStream("/storage/self/primary/Documents/testing.png")
        // val scaledDown = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
        // scaledDown.compress(Bitmap.CompressFormat.PNG, 10, out)
        // bitmap.compress(Bitmap.CompressFormat.PNG, 10, out)

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