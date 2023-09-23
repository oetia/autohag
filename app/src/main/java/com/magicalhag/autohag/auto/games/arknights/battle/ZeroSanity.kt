package com.magicalhag.autohag.auto.games.arknights.battle

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.find
import kotlinx.coroutines.launch


suspend fun AutoService.arknightsZeroSanity(text: Text, onCompletion: () -> Unit) {
    if (text.check("friends", "archives", "sanity/")) {
        dispatch(text.find("sanity/").buildClick())
    } else if (text.check("to the most recent stage")) {
        dispatch(text.find("to the most recent stage").buildClick())
    } else if (text.check("auto deploy", "start")) {
        dispatch(text.find("start").buildClick())
    } else if (text.check("the roster for this operation cannot be changed", "mission", "start")) {
        dispatch(text.find("start").buildClick())
    } else if (text.check("2x", "takeover", "unit limit")) {
        coroutineScope.launch { nap(15 * 1000) } // sleep & delay halt two different ways - delay provides a minimum
    } else if (text.check("mission", "results")) {
        dispatch(text.find("results").buildClick())
    } else if (text.check("restore")) {
        onCompletion()
    } else {
        arknightsHome(text) {}
    }
}