package com.magicalhag.autohag.auto.core.dispatch

import android.accessibilityservice.GestureDescription
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.text.find
import com.magicalhag.autohag.auto.core.text.findElements

interface DispatchUtils {
    suspend fun t(t: String): Boolean
    suspend fun te(t: String): Boolean
    suspend fun p(p: Point): Boolean
    suspend fun s(p1: Point, p2: Point): Boolean

}

fun AutoService.generateDispatchUtils(text: Text): DispatchUtils {
    return object : DispatchUtils {
        override suspend fun t(t: String): Boolean = dispatch(text.find(t).last().buildClick())
        override suspend fun te(t: String): Boolean = dispatch(text.findElements(t).buildElementClick())
        override suspend fun p(p: Point): Boolean = dispatch(p.buildClick())
        override suspend fun s(p1: Point, p2: Point): Boolean = dispatch(buildSwipe(p1, p2))
    }
}