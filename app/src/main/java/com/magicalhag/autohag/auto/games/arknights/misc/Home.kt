package com.magicalhag.autohag.auto.games.arknights.misc

import android.accessibilityservice.AccessibilityService
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.utils.text.check

// perhaps have a callback for onCompletion
fun AutoService.arknightsHome(text: Text, onCompleted: () -> Unit) {
    if (text.check("friends", "archives", "squads", "operator")) {
        onCompleted()
    } else {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
}