package com.magicalhag.autohag.auto

import android.graphics.Point
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.R
import com.magicalhag.autohag.AutoService
import android.content.Context

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

fun Text.find(text: String, idx: Int = 0): Text.TextBlock {
    return this.textBlocks.find(text, idx)
}

fun List<Text.TextBlock>.find(text: String, idx: Int = 0): Text.TextBlock {
    val found = ArrayList<Text.TextBlock>()
    for (block in this) {
        if (block.text.lowercase().contains(text.toRegex())) {
            found.add(block)
        }
    }
    if (found.size >= idx) {
        return found[idx]
    } else {
        throw Exception("Block Not Found")
    }
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
