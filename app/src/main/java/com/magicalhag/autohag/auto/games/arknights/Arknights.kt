package com.magicalhag.autohag.auto.games.arknights

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBase
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBaseCollection
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseCheckAvailableOps
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseAddNewOps
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseConfirmOps
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseTradingPost
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseTradingPostMoraleCheck
import com.magicalhag.autohag.auto.games.arknights.battle.arknightsZeroSanity
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsRecruitment
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsStartup


val availableTpOpNames = mutableListOf<String>()
val blacklistedTpOpNames = mutableListOf<String>()
val alreadyFoundTpOpNames = mutableListOf<String>()

// so this currently is ugly as fuck.
// but it's extremely promising
// i need to find some way to pass in state for function parameters
// if there's some way to pass in parameters for the next call throug the cb's
suspend fun AutoService.arknights(text: Text) {
    when (state) {
        "STRTUP" -> arknightsStartup(text)
        "ALL" -> arknightsAll(text)
        "HOME" -> arknightsHome(text) { coma() }
        "RECR" -> arknightsRecruitment(text) { coma() }
        "0SANITY" -> arknightsZeroSanity(text) { coma() }

        "B4SE" -> arknightsBase(text) { state = "B4SE_COL" }
        "B4SE_COL" -> arknightsBaseCollection(text, false) { state = "B4SE_TP1" }
        "B4SE_COL_E" -> arknightsBaseCollection(text, true) { state = "B4SE_TP1" }
        "B4SE_TP1" -> arknightsBaseTradingPost(text, 1) { state = "B4SE_TP_MC" }
        "B4SE_TP_MC" -> arknightsBaseTradingPostMoraleCheck(text, { state = "B4SE_2" }) { blacklistedTpOpNames.addAll(it); state = "B4SE_TP_CO_P1" }
        "B4SE_TP_CO_P1" -> arknightsBaseCheckAvailableOps(text, 1) { availableTpOpNames.addAll(it); state = "B4SE_TP_CO_P2"}
        "B4SE_TP_CO_P2" -> arknightsBaseCheckAvailableOps(text, 2) { availableTpOpNames.addAll(it); state = "B4SE_TP_CO_P3"}
        "B4SE_TP_CO_P3" -> arknightsBaseCheckAvailableOps(text, 3) { availableTpOpNames.addAll(it); state = "B4SE_TP_ANO_P1"}
        "B4SE_TP_ANO_P1" -> arknightsBaseAddNewOps(text, 1, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP_ANO_P2" }
        "B4SE_TP_ANO_P2" -> arknightsBaseAddNewOps(text, 2, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP_ANO_P3" }
        "B4SE_TP_ANO_P3" -> arknightsBaseAddNewOps(text, 3, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP_CONF" }
        "B4SE_TP_CONF" -> arknightsBaseConfirmOps(text) { state = "B4SE_TP1" }


        "B4SE_2" -> arknightsBase(text) { state = "B4SE_COL_2" }
        "B4SE_COL_2" -> arknightsBaseCollection(text, false) { state = "B4SE_TP2" }
        "B4SE_COL_E_2" -> arknightsBaseCollection(text, true) { state = "B4SE_TP2" }
        "B4SE_TP2" -> arknightsBaseTradingPost(text, 2) { state = "B4SE_TP2_MC" }
        "B4SE_TP2_MC" -> arknightsBaseTradingPostMoraleCheck(text, { state = "B4SE_3" }) { blacklistedTpOpNames.addAll(it); state = "B4SE_TP2_CO_P1" }
        "B4SE_TP2_ANO_P1" -> arknightsBaseAddNewOps(text, 1, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP2_ANO_P2" }
        "B4SE_TP2_ANO_P2" -> arknightsBaseAddNewOps(text, 2, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP2_ANO_P3" }
        "B4SE_TP2_ANO_P3" -> arknightsBaseAddNewOps(text, 3, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP2_CONF" }
        "B4SE_TP2_CONF" -> arknightsBaseConfirmOps(text) { state = "B4SE_TP1" }


        "B4SE_3" -> arknightsBase(text) { state = "B4SE_COL_3" }
        "B4SE_COL_3" -> arknightsBaseCollection(text, false) { state = "B4SE_TP3" }
        "B4SE_COL_E_3" -> arknightsBaseCollection(text, true) { state = "B4SE_TP3" }
        "B4SE_TP3" -> arknightsBaseTradingPost(text, 3) { state = "B4SE_TP3_MC" }
        "B4SE_TP3_MC" -> arknightsBaseTradingPostMoraleCheck(text, { state = "B4SE_4" }) { blacklistedTpOpNames.addAll(it); state = "B4SE_TP3_CO_P1" }
        "B4SE_TP3_ANO_P1" -> arknightsBaseAddNewOps(text, 1, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP3_ANO_P3" }
        "B4SE_TP3_ANO_P3" -> arknightsBaseAddNewOps(text, 2, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP3_ANO_P3" }
        "B4SE_TP3_ANO_P3" -> arknightsBaseAddNewOps(text, 3, availableTpOpNames, blacklistedTpOpNames, alreadyFoundTpOpNames) { alreadyFoundTpOpNames.addAll(it); state = "B4SE_TP3_CONF" }
        "B4SE_TP3_CONF" -> arknightsBaseConfirmOps(text) { state = "B4SE_TP1" }
    }
}

suspend fun AutoService.B4SED() {

}


var subState = "HOME"
suspend fun AutoService.arknightsAll(text: Text) {
    when (subState) {
        "HOME" -> arknightsHome(text) { subState = "RECR" }
        "RECR" -> arknightsRecruitment(text) { subState = "0SANITY" }
        "0SANITY" -> arknightsZeroSanity(text) { coma() }
    }
}


