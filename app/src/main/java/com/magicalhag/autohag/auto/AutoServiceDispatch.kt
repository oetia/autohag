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

fun List<Text.Line>.buildClick(duration: Long = 100L): GestureDescription {

    val line = this[0]
    val center = line.getCenter()

    val clickPath = Path()
    clickPath.moveTo(center.x.toFloat(), center.y.toFloat())

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

    log("BUILDING CLICK: $center - ${line.text}")

    return gestureBuilder.build()
}

fun Point.buildClick(duration: Long = 100L): GestureDescription {
    val clickPath = Path()
    clickPath.moveTo(this.x.toFloat(), this.y.toFloat())

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

    return gestureBuilder.build()
}

fun buildSwipe(startPoint: Point, endPoint: Point, duration: Long = 1000L): GestureDescription {

    val swipePath = Path()
    swipePath.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
    swipePath.lineTo(endPoint.x.toFloat(), endPoint.y.toFloat())

    val clickPath = Path()
    clickPath.moveTo(endPoint.x.toFloat(), endPoint.y.toFloat())

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))
    // gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, 500L))

    return gestureBuilder.build()
}
