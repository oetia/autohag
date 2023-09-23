package com.magicalhag.autohag.auto

import android.graphics.Point
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.AutoService
import android.graphics.Rect

fun log(message: Any) {
    val threadName = Thread.currentThread().name
    val wrapper = "=".repeat(50)
    // val wrapper = "=".repeat(threadName.length + message.toString().length + 5 + 1)
    Log.d("Kal's Panties", "$wrapper\n[$threadName] - $message\n$wrapper")
}

fun AutoService.toast(message: Any) {
    mainHandler.post {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }
}

fun Text.find(
    text: String,
    roi: Rect = Rect(
        Int.MIN_VALUE, Int.MIN_VALUE,
        Int.MAX_VALUE, Int.MAX_VALUE
    )
): List<Text.Line> {
    return this.textBlocks.find1(text, roi)
}

fun List<Text.TextBlock>.find1(
    text: String,
    roi: Rect = Rect(
        Int.MIN_VALUE, Int.MIN_VALUE,
        Int.MAX_VALUE, Int.MAX_VALUE
    )
): List<Text.Line> {
    val found = mutableListOf<Text.Line>()
    for (block in this) {
        val subFound = block.lines.find2(text, roi)
        found.addAll(subFound)
    }
    return found
}

fun List<Text.Line>.find2(
    text: String,
    roi: Rect = Rect(
        Int.MIN_VALUE, Int.MIN_VALUE,
        Int.MAX_VALUE, Int.MAX_VALUE
    )
): List<Text.Line> {
    val found = mutableListOf<Text.Line>()
    for (line in this) {
        if (
            line.text.lowercase().contains(text.toRegex()) &&
            roi.contains(line.boundingBox as Rect)
        ) {
            found.add(line)
        }
    }
    return found
}


fun Text.check(vararg regExs: String): Boolean {
    return this.text.check(*regExs)
}


fun String.check(vararg regExs: String): Boolean {
    val (contains, excludes) = regExs.partition { this.lowercase().contains(it.toRegex()) }

    val joinC = contains.joinToString("`, `", "`", "`")
    val joinE = excludes.joinToString("`, `", "`", "`")

    log("STRING CHECK\nContains: $joinC\nExcludes: $joinE")

    return contains.size == regExs.size
}

fun Text.Line.getCenter(): Point {
    val centerX = (this.cornerPoints!![1].x + this.cornerPoints!![0].x) / 2
    val centerY = (this.cornerPoints!![2].y + this.cornerPoints!![1].y) / 2
    return Point(centerX, centerY)
}
