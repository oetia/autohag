package com.magicalhag.autohag.auto.games.arknights

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBaseRealign
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBaseTradingPost
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsCredits
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsRewards
import com.magicalhag.autohag.auto.games.arknights.battle.arknightsZeroSanity
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsRecruitment
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsStartup


// so this currently is ugly as fuck.
// but it's extremely promising
// i need to find some way to pass in state for function parameters
// if there's some way to pass in parameters for the next call throug the cb's
// what to do next upon finishing

var state = ArknightsState()

suspend fun AutoService.arknights(text: Text) {
    when (task) {
        "STRTUP" -> arknightsStartup(text)
        "ALL" -> arknightsAll(text)
        "HOME" -> arknightsHome(text) { coma() }
        "B4SE" -> arknightsBase(text, state) { coma() }
        "CREDITS" -> arknightsCredits(text) { coma() }
        "RECR" -> arknightsRecruitment(text) { coma() }
        "0SANITY" -> arknightsZeroSanity(text) { coma() }
        "REWARDS" -> arknightsRewards(text) { coma() }

        "RESET" -> state = ArknightsState()
    }
}

// have some way to generate a chain
// chain can be a no op
// conditional branches through cb's
// and then specify what to do at the end of that chain

// ok. break up into functions. have internal state for function. cb to the end of time.
//
suspend fun AutoService.arknightsBase(text: Text, state: ArknightsState, onCompletion: () -> Unit) {
    when (state.baseTask) {
        "REALIGN1" -> arknightsBaseRealign(text, state) { state.baseTask = "TP1" }
        "TP1" -> arknightsBaseTradingPost(text, 1, state) { state.baseTask = "REALIGN2" }
        "REALIGN2" -> arknightsBaseRealign(text, state) { state.baseTask = "TP2" }
        "TP2" -> arknightsBaseTradingPost(text, 2, state) { state.baseTask = "REALIGN3" }
        "REALIGN3" -> arknightsBaseRealign(text, state) { state.baseTask = "TP3" }
        "TP3" -> arknightsBaseTradingPost(text, 3, state) { state.baseTask = "ASDF"; coma() }
    }
}


suspend fun AutoService.arknightsAll(text: Text) {
    when (state.allState) {
        "HOME" -> arknightsHome(text) { state.allState = "RECR" }
        "RECR" -> arknightsRecruitment(text) { state.allState = "CREDITS" }
        "CREDITS" -> arknightsCredits(text) { state.allState = "0SANITY" }
        "0SANITY" -> arknightsZeroSanity(text) { state.allState = "REWARDS" }
        "REWARDS" -> arknightsRewards(text) { coma() }
    }
}


