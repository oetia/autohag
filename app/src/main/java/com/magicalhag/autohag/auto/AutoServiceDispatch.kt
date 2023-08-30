package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.AutoService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun AutoService.dispatch(gesture: GestureDescription): Boolean = suspendCoroutine {
    dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
        override fun onCompleted(gestureDescription: GestureDescription?) {
            super.onCompleted(gestureDescription)
            log("gesture completed")
            it.resume(true)
        }

        override fun onCancelled(gestureDescription: GestureDescription?) {
            super.onCancelled(gestureDescription)
            log("gesture cancelled")
            it.resume(false)
        }
    }, null)
}

fun Text.TextBlock.buildClick(duration: Long = 100L): GestureDescription {

    val center = this.getCenter()

    val clickPath = Path()
    clickPath.moveTo(center.x.toFloat(), center.y.toFloat())

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

    log("BUILDING CLICK: $center - ${this.text}")

    return gestureBuilder.build()
}

fun Point.buildClick(duration: Long = 100L): GestureDescription {
    val clickPath = Path()
    clickPath.moveTo(this.x.toFloat(), this.y.toFloat())

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

    return gestureBuilder.build()
}