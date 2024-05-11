package com.magicalhag.autohag.auto.core.dispatch

import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log

interface DispatchHistoryEntry {
    val location: String
    val action: String
}

fun AutoService.addDispatchHistoryEntry(location: String, action: String) {
    actionHistory.add(object : DispatchHistoryEntry {
        override val action: String get() = action
        override val location: String get() = location
    })
    log("(ACTION*): $location -> $action")
}


// there are a limited set of locations available