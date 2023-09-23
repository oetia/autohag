package com.magicalhag.autohag.auto.games.arknights.battle

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.find
import kotlinx.coroutines.launch


/*
ALMOST CERTAINLY BREAKING CHANGES AFTER THE BLOCK -> LINE TRANSITION
be sure to TSET
 */
suspend fun AutoService.arknightsZeroSanity(ocrout: Text) {
    if (ocrout.check("friends", "archives", "sanity/")) {
        dispatch(ocrout.find("sanity/").buildClick())
    } else if (ocrout.check("to the most recent stage")) {
        dispatch(ocrout.find("to the most recent stage").buildClick())
    } else if (ocrout.check("auto deploy", "start")) {
        dispatch(ocrout.find("start").buildClick())
    } else if (ocrout.check("the roster for this operation cannot be changed", "mission", "start")) {
        dispatch(ocrout.find("start").buildClick())
    } else if (ocrout.check("2x", "takeover", "unit limit")) {
        coroutineScope.launch { nap(15 * 1000) } // sleep & delay halt two different ways - delay provides a minimum
    } else if (ocrout.check("mission", "results")) {
        dispatch(ocrout.find("results").buildClick())
    } else if (ocrout.check("restore")) {
        coma()
    }
}


//GOAL: Start literally wherever and then get into the groove