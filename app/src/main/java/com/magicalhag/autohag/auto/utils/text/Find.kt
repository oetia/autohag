package com.magicalhag.autohag.auto.utils.text

import android.graphics.Rect
import com.google.mlkit.vision.text.Text

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