package com.magicalhag.autohag.auto.core.dispatch

import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.core.logging.log

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

fun Text.Line.buildClick(duration: Long = 100L): GestureDescription {
    val line = this
    val center = line.getCenter()

    val clickPath = Path()
    clickPath.moveTo(center.x.toFloat(), center.y.toFloat())

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

    log("BUILDING CLICK: $center - ${line.text}")

    return gestureBuilder.build()
}

fun List<Text.Element>.buildElementClick(duration: Long = 100L): GestureDescription {
    val element = this[0]
    val center = element.getCenter()

    val clickPath = Path()
    clickPath.moveTo(center.x.toFloat(), center.y.toFloat())

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

    log("BUILDING CLICK: $center - ${element.text}")

    return gestureBuilder.build()
}

fun Text.Element.buildElementClick(duration: Long = 100L): GestureDescription {
    val element = this
    val center = element.getCenter()

    val clickPath = Path()
    clickPath.moveTo(center.x.toFloat(), center.y.toFloat())

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

    log("BUILDING CLICK: $center - ${element.text}")

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

fun Text.Line.getCenter(): Point {
    val centerX = (this.cornerPoints!![1].x + this.cornerPoints!![0].x) / 2
    val centerY = (this.cornerPoints!![2].y + this.cornerPoints!![1].y) / 2
    return Point(centerX, centerY)
}

fun Text.Element.getCenter(): Point {
    val centerX = (this.cornerPoints!![1].x + this.cornerPoints!![0].x) / 2
    val centerY = (this.cornerPoints!![2].y + this.cornerPoints!![1].y) / 2
    return Point(centerX, centerY)
}