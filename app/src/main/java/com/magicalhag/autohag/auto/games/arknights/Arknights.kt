package com.magicalhag.autohag.auto.games.arknights

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBaseRealign
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBaseTradingPost
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
        "RECR" -> arknightsRecruitment(text) { coma() }
        "0SANITY" -> arknightsZeroSanity(text) { coma() }
        "B4SE" -> arknightsBase(text, state) { coma() }

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




        // "COLLECT" -> arknightsBaseCollection(text, state) { task = "TP1" }
        // "COLLECT_E" -> arknightsBaseCollection(text, state) { task = "TP1" }
        // // "TP1" -> arknightsBaseTradingPost(text, 1, av) { task = "TP1_CHECK_OPS" }
        // "DONE" -> onCompletion()
        // "TP_CO_P1" -> arknightsBaseCheckAvailableOps(text, 1) { availableTpOpNames.addAll(it); state = "B4SE_TP_CO_P2"}
        // "TP_CO_P2" -> arknightsBaseCheckAvailableOps(text, 2) { availableTpOpNames.addAll(it); state = "B4SE_TP_CO_P3"}
        // "TP_CO_P3" -> arknightsBaseCheckAvailableOps(text, 3) { availableTpOpNames.addAll(it); state = "B4SE_TP_MC"}
        //
        // "B4SE_TP_MC" -> arknightsBaseTradingPostMoraleCheck(text, { state = "B4SE_2" }) { blacklistedTpOpNames.addAll(it); state = "B4SE_TP_ANO_P1" }
        // "B4SE_TP_ANO_P1" -> arknightsBaseAddNewOps(text, 1, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP_ANO_P2" }
        // "B4SE_TP_ANO_P2" -> arknightsBaseAddNewOps(text, 2, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP_ANO_P3" }
        // "B4SE_TP_ANO_P3" -> arknightsBaseAddNewOps(text, 3, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP_CONF" }
        // "B4SE_TP_CONF" -> arknightsBaseConfirmOps(text) { state = "B4SE_TP1A" }
        // "B4SE_TP1A" -> arknightsBaseTradingPost(text, 3) { state = "B4SE_TP_MC"; alreadyFoundTpOpNames = mutableListOf() }
        //
        //
        // "B4SE_2" -> arknightsBaseHome(text) { state = "B4SE_COL_2" }
        // "B4SE_COL_2" -> arknightsBaseCollection(text, false) { state = "B4SE_TP2" }
        // "B4SE_COL_E_2" -> arknightsBaseCollection(text, true) { state = "B4SE_TP2" }
        // "B4SE_TP2" -> arknightsBaseTradingPost(text, 2) { state = "B4SE_TP2_MC"; alreadyFoundTpOpNames = mutableListOf() }
        // "B4SE_TP2_MC" -> arknightsBaseTradingPostMoraleCheck(text, { state = "B4SE_3" }) { blacklistedTpOpNames.addAll(it); state = "B4SE_TP2_ANO_P1" }
        // "B4SE_TP2_ANO_P1" -> arknightsBaseAddNewOps(text, 1, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP2_ANO_P2" }
        // "B4SE_TP2_ANO_P2" -> arknightsBaseAddNewOps(text, 2, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP2_ANO_P3" }
        // "B4SE_TP2_ANO_P3" -> arknightsBaseAddNewOps(text, 3, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP2_CONF" }
        // "B4SE_TP2_CONF" -> arknightsBaseConfirmOps(text) { state = "B4SE_TP1" }
        //
        //
        // "B4SE_3" -> arknightsBaseHome(text) { state = "B4SE_COL_3" }
        // "B4SE_COL_3" -> arknightsBaseCollection(text, false) { state = "B4SE_TP3" }
        // "B4SE_COL_E_3" -> arknightsBaseCollection(text, true) { state = "B4SE_TP3" }
        // "B4SE_TP3" -> arknightsBaseTradingPost(text, 3) { state = "B4SE_TP3_MC"; alreadyFoundTpOpNames = mutableListOf() }
        // "B4SE_TP3_MC" -> arknightsBaseTradingPostMoraleCheck(text, { state = "B4SE_4" }) { blacklistedTpOpNames.addAll(it); state = "B4SE_TP3_ANO_P1" }
        // "B4SE_TP3_ANO_P1" -> arknightsBaseAddNewOps(text, 1, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP3_ANO_P3" }
        // "B4SE_TP3_ANO_P3" -> arknightsBaseAddNewOps(text, 2, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP3_ANO_P3" }
        // "B4SE_TP3_ANO_P3" -> arknightsBaseAddNewOps(text, 3, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP3_CONF" }
        // "B4SE_TP3_CONF" -> arknightsBaseConfirmOps(text) { state = "B4SE_TP1" }
    }
}


var subState = "HOME"
suspend fun AutoService.arknightsAll(text: Text) {
    when (subState) {
        "HOME" -> arknightsHome(text) { subState = "RECR" }
        "RECR" -> arknightsRecruitment(text) { subState = "0SANITY" }
        "0SANITY" -> arknightsZeroSanity(text) { coma() }
    }
}


