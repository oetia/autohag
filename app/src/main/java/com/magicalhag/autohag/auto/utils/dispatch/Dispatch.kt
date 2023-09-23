package com.magicalhag.autohag.auto.utils.dispatch

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.utils.logging.log
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun AutoService.dispatch(gesture: GestureDescription): Boolean = suspendCoroutine {
    dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
        override fun onCompleted(gestureDescription: GestureDescription?) {
            super.onCompleted(gestureDescription)
            log("gesture completed")
            it.resume(true)
        }

        override fun onCancelled(gestureDescription: GestureDescription?) {
            super.onCancelled(gestureDescription)
            log("gesture cancelled")
            it.resume(false)
        }
    }, null)
}

