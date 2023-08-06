package com.magicalhag.autohag.utils

import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Point

interface DispatchUtils {
    fun buildClick(point: Point, duration: Long = 100L): GestureDescription {
        val clickPath = Path()
        clickPath.moveTo(point.x.toFloat(), point.y.toFloat())

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

        return gestureBuilder.build()
    }

    fun buildClick(x: Float, y: Float, duration: Long = 100L): GestureDescription {
        val clickPath = Path()
        clickPath.moveTo(x, y)

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

        return gestureBuilder.build()
    }

    fun buildScroll(type: String, duration: Long): GestureDescription {

        val screenHeight = 1080
        val screenWidth = 2300

        // indicates corners of a square
        val topY = screenHeight * 0.2
        val bottomY = screenHeight * 0.80
        val leftX = screenWidth * 0.2
        val rightX = screenWidth * 0.80

        val swipePath = Path()
        lateinit var startPoint: Point
        lateinit var endPoint: Point


        when (type) {
            "UP" -> {
                startPoint = Point(rightX.toInt(), topY.toInt())
                endPoint = Point(rightX.toInt(), bottomY.toInt())
            }

            "DOWN" -> {
                startPoint = Point(rightX.toInt(), bottomY.toInt())
                endPoint = Point(rightX.toInt(), topY.toInt())
            }

            else -> { // default on a scroll up
                startPoint = Point(rightX.toInt(), topY.toInt())
                endPoint = Point(rightX.toInt(), bottomY.toInt())
            }
        }

        swipePath.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        swipePath.lineTo(endPoint.x.toFloat(), endPoint.y.toFloat())

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))

        return gestureBuilder.build()

    }
}