package com.magicalhag.autohag.auto.core.dispatch

import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.text.find
import com.magicalhag.autohag.auto.core.text.findElements

suspend fun AutoService.dispatchc(text: Text, textToFind: String, findElement: Boolean = false) {
    if(!findElement) {
        dispatch(text.find(textToFind).buildClick())
    } else {
        dispatch(text.findElements(textToFind).buildElementClick())
    }
}

suspend fun AutoService.dispatchc(point: Point) {
    dispatch(point.buildClick())
}