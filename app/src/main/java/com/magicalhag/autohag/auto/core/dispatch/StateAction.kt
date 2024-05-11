package com.magicalhag.autohag.auto.core.dispatch

import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log

// storing metadata about past actions
// you'll be observing the result in the current state
// i don't think there's anything you have to keep track of outside of information on the screen that wouldn't be mentioned in

// i think a more appropriate description is metadata.

data class StateAction (
    val state: String, // where was the action performed
    val action: String, // what was the action
    val notes: String, // extra remarks. shouldn't need this, but could be useful.
)

// fun AutoService.addDispatchHistoryEntry(state: String, action: String) {
//     actionHistory.add(StateAction(state, action, ""))
//     log("(ACTION*): $state -> $action")
// }