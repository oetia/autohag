package com.magicalhag.autohag.auto.games.arknights.battle

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import com.magicalhag.autohag.auto.core.dispatch.buildClick
import com.magicalhag.autohag.auto.core.text.check
import com.magicalhag.autohag.auto.core.dispatch.dispatch
import com.magicalhag.autohag.auto.core.text.find
import kotlinx.coroutines.launch


suspend fun AutoService.arknightsZeroSanity(text: Text, onCompletion: () -> Unit) {
    if (text.check("friends", "archives", "sanity/")) {
        dispatch(text.find("sanity/").buildClick())
    } else if (text.check("to the most recent stage")) {
        dispatch(text.find("to the most recent stage").buildClick())
    } else if (text.check("auto deploy", "start")) {
        dispatch(text.find("start").buildClick())
    } else if (
        text.check("the roster for this operation cannot be changed") && (
            text.check("mission", "start") ||
            text.check("operation", "start")
        )
    ) {
        dispatch(text.find("start").buildClick())
    } else if (text.check("2x", "takeover")) { // "unit limit" - can break if volume adjusted
        coroutineScope.launch { nap(15 * 1000) } // sleep & delay halt two different ways - delay provides a minimum
    } else if (text.check("mission", "results")) {
        dispatch(text.find("results").buildClick())
    } else if (text.check("restore")) {
        onCompletion()
    } else {
        arknightsHome(text) {}
    }
}