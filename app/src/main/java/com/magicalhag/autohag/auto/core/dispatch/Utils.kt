package com.magicalhag.autohag.auto.core.dispatch

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

    // with metadata
    suspend fun tm(t: String, state: String): Boolean
    suspend fun tem(t: String, state: String): Boolean
    suspend fun pm(p: Point, state: String): Boolean
    suspend fun sm(p1: Point, p2: Point, state: String): Boolean


}
fun AutoService.generateDispatchUtils(text: Text): DispatchUtils {
    return object : DispatchUtils {
        override suspend fun t(t: String): Boolean = dispatch(text.find(t).last().buildClick())
        override suspend fun te(t: String): Boolean = dispatch(text.findElements(t).buildElementClick())
        override suspend fun p(p: Point): Boolean = dispatch(p.buildClick())
        override suspend fun s(p1: Point, p2: Point): Boolean = dispatch(buildSwipe(p1, p2))

        override suspend fun tm(t: String, state: String): Boolean = dispatch(text.find(t).last().buildClick(), StateActionPair(state, "click '$t'"))
        override suspend fun tem(t: String, state: String): Boolean = dispatch(text.findElements(t).buildElementClick(), StateActionPair(state, "click '$t'"))
        override suspend fun pm(p: Point, state: String): Boolean = dispatch(p.buildClick(), StateActionPair(state, "click '(${p.x}, ${p.y})'"))
        override suspend fun sm(p1: Point, p2: Point, state: String): Boolean = dispatch(buildSwipe(p1, p2), StateActionPair(state, "swipe '(${p1.x}, ${p1.y}) -> (${p2.x}, ${p2.y})'"))
    }
}