package com.magicalhag.autohag.auto.core.image

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun AutoService.extractTextFromImage(image: InputImage): Text {
    val text = suspendCoroutine {
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // log("Extraction Success: \n${visionText.text}")
                it.resume(visionText)
            }
            .addOnFailureListener { exception ->
                it.resumeWithException(exception)
            }

    }
    // log("extracted text from image")
    return text
}

// .addOnSuccessListener(dispatcher.executor) { visionText ->
//     log("extraction success")
//     it.resume(visionText)
// }
// .addOnFailureListener(dispatcher.executor) { exception ->
//     it.resumeWithException(exception)
// }
// getting issues where task completion isn't marked correctly on thread close
// going to stick the cb's happening on the main thread. shouldn't fuck w. much anyways...