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

fun Text.find(text: String, roi: Rect? = null, idx: Int = 0): Text.TextBlock {
    return this.textBlocks.find(text, roi, idx)
}

fun List<Text.TextBlock>.find(text: String, roi: Rect? = null, idx: Int = 0): Text.TextBlock {
    val found = ArrayList<Text.TextBlock>()
    for (block in this) {
        block.lines
        if (block.text.lowercase().contains(text.toRegex())) {
            if(roi != null) {
                if(roi.contains(block.boundingBox as Rect)) {
                    found.add(block)
                }
            } else {
                found.add(block)
            }
        }
    }
    if(found.size >= idx) {
        return found[idx]
    } else {
        throw Exception("Block Not Found")
    }
}

fun Text.findAll(text: String, roi: Rect? = null): ArrayList<Text.TextBlock> {
    return this.textBlocks.findAll(text, roi)
}

fun List<Text.TextBlock>.findAll(text: String, roi: Rect? = null): ArrayList<Text.TextBlock> {
    val found = ArrayList<Text.TextBlock>()
    for (block in this) {
        if (block.text.lowercase().contains(text.toRegex())) {
            if(roi != null) {
                if(roi.contains(block.boundingBox as Rect)) {
                    found.add(block)
                }
            } else {
                found.add(block)
            }
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

fun Text.TextBlock.getCenter(): Point {
    val centerX = (this.cornerPoints!![1].x + this.cornerPoints!![0].x) / 2
    val centerY = (this.cornerPoints!![2].y + this.cornerPoints!![1].y) / 2
    return Point(centerX, centerY)
}
