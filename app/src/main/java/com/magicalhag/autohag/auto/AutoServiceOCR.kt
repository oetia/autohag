package com.magicalhag.autohag.auto

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.AutoService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun AutoService.extractTextFromImage(image: InputImage): Text {
    log("extracting text from image")
    val text = suspendCoroutine {
        recognizer.process(image)
            // .addOnSuccessListener(dispatcher.executor) { visionText ->
            //     log("extraction success")
            //     it.resume(visionText)
            // }
            // .addOnFailureListener(dispatcher.executor) { exception ->
            //     it.resumeWithException(exception)
            // }
            // getting issues where task completion isn't marked correctly on thread close
            // going to stick the cb's happening on the main thread. shouldn't fuck w. much anyways...
            .addOnSuccessListener { visionText ->
                log("extraction success")
                it.resume(visionText)
            }
            .addOnFailureListener { exception ->
                it.resumeWithException(exception)
            }

    }
    log("extracted text from image")
    return text
}