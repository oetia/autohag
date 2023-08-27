package com.magicalhag.autohag.auto

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun AutoService.extractTextFromImage(image: InputImage): Text {
    log("extracting text from image")
    val text = suspendCoroutine {
        val task = recognizer.process(image)
            .addOnSuccessListener(dispatcher.executor) { visionText ->
                // processed on main. that's not expected behavior. does recognizer.process run on main as well? or is it just cb's?
                log("extraction success")
                it.resume(visionText)
            }
            .addOnFailureListener(dispatcher.executor) { exception ->
                it.resumeWithException(exception)
            }
    }
    log("extracted text from image")
    return text
}