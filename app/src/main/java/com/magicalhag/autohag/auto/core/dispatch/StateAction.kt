package com.magicalhag.autohag.auto.core.dispatch

import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log

// storing metadata about past actions
// you'll be observing the result in the current state
// i don't think there's anything you have to keep track of outside of information on the screen that wouldn't be mentioned in

// i think a more appropriate description is metadata.

data class StateActionPair (
    val state: String, // where was the action performed
    val action: String // what was the action
)

fun AutoService.addActionHistoryEntry(sap: StateActionPair) {
    actionHistory.add(sap)
    log("(ACTION*): ${sap.state} -> ${sap.action}")
}