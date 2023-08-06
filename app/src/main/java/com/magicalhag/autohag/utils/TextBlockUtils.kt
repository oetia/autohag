package com.magicalhag.autohag.utils

import android.graphics.Point
import com.google.mlkit.vision.text.Text
interface TextBlockUtils {
    fun List<Text.TextBlock>.find(text: String): Text.TextBlock {
        for (block in this) {
            if (block.text.lowercase().contains(text.toRegex())) {
                return block
            }
        }
        throw Exception("Block Not Found")
    }

    fun Text.TextBlock.getCenter(): Point {
        val centerX = (this.cornerPoints!![1].x + this.cornerPoints!![0].x) / 2
        val centerY = (this.cornerPoints!![2].y + this.cornerPoints!![1].y) / 2
        return Point(centerX, centerY)
    }

    fun Text.TextBlock.getArea(): Int {
        return this.getWidth() * this.getHeight()
    }


    fun Text.TextBlock.getWidth(): Int {
        return this.cornerPoints!![1].x - this.cornerPoints!![0].x
    }

    fun Text.TextBlock.getHeight(): Int {
        return this.cornerPoints!![2].y - this.cornerPoints!![1].y
    }

}