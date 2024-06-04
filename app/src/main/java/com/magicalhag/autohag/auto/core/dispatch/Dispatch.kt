package com.magicalhag.autohag.auto.core.dispatch

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun AutoService.dispatch(
    gesture: GestureDescription,
    sap: StateActionPair = StateActionPair("NA", "NA")
): Boolean = suspendCoroutine {
    dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
        override fun onCompleted(gestureDescription: GestureDescription?) {
            super.onCompleted(gestureDescription)
            log("sap: (${sap.state}, ${sap.action})")
            addActionHistoryEntry(sap)
            it.resume(true)
        }

        override fun onCancelled(gestureDescription: GestureDescription?) {
            super.onCancelled(gestureDescription)
            log("gesture cancelled")
            it.resume(false)
        }
    }, null)
}