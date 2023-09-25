package com.magicalhag.autohag.auto.games.arknights

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBaseRealign
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBaseFacility
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBaseFacilityDorm
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
        "REALIGN0" -> arknightsBaseRealign(text, state) { state.baseTask = "CC" }
        "CC" -> arknightsBaseFacility(text, "CC", state) { state.baseTask = "REALIGN1" }

        "REALIGN1" -> arknightsBaseRealign(text, state) { state.baseTask = "TP1" }
        "TP1" -> arknightsBaseFacility(text, "TP1", state) { state.baseTask = "REALIGN2" }
        "REALIGN2" -> arknightsBaseRealign(text, state) { state.baseTask = "TP2" }
        "TP2" -> arknightsBaseFacility(text, "TP2", state) { state.baseTask = "REALIGN3" }
        "REALIGN3" -> arknightsBaseRealign(text, state) { state.baseTask = "TP3" }
        "TP3" -> arknightsBaseFacility(text, "TP3", state) { state.baseTask = "REALIGN4"; state.tpAvailableOpNames = mutableListOf(); state.tpBlacklistedOpNames = mutableListOf(); state.tpAlreadySelectedOpNames = mutableListOf() }

        "REALIGN4" -> arknightsBaseRealign(text, state) { state.baseTask = "FAC1" }
        "FAC1" -> arknightsBaseFacility(text, "FAC1", state) { state.baseTask = "REALIGN5" }
        "REALIGN5" -> arknightsBaseRealign(text, state) { state.baseTask = "FAC2" }
        "FAC2" -> arknightsBaseFacility(text, "FAC2", state) { state.baseTask = "REALIGN6" }
        "REALIGN6" -> arknightsBaseRealign(text, state) { state.baseTask = "FAC3" }
        "FAC3" -> arknightsBaseFacility(text, "FAC3", state) { state.baseTask = "REALIGN7"; state.tpAvailableOpNames = mutableListOf(); state.tpBlacklistedOpNames = mutableListOf(); state.tpAlreadySelectedOpNames = mutableListOf() }

        "REALIGN7" -> arknightsBaseRealign(text, state) { state.baseTask = "PP1" }
        "PP1" -> arknightsBaseFacility(text, "PP1", state) { state.baseTask = "REALIGN8" }
        "REALIGN8" -> arknightsBaseRealign(text, state) { state.baseTask = "PP2" }
        "PP2" -> arknightsBaseFacility(text, "PP2", state) { state.baseTask = "REALIGN9" }
        "REALIGN9" -> arknightsBaseRealign(text, state) { state.baseTask = "PP3" }
        "PP3" -> arknightsBaseFacility(text, "PP3", state) { state.baseTask = "REALIGN10" }

        "REALIGN10" -> arknightsBaseRealign(text, state) { state.baseTask = "OFFICE" }
        "OFFICE" -> arknightsBaseFacility(text, "OFFICE", state) { state.baseTask = "REALIGN11" }

        "REALIGN11" -> arknightsBaseRealign(text, state) { state.baseTask = "DORM1" }
        "DORM1" -> arknightsBaseFacilityDorm(text, "DORM1", state) { state.baseTask = "REALIGN12" }
        "REALIGN12" -> arknightsBaseRealign(text, state) { state.baseTask = "DORM2" }
        "DORM2" -> arknightsBaseFacilityDorm(text, "DORM2", state) { state.baseTask = "REALIGN13" }
        "REALIGN13" -> arknightsBaseRealign(text, state) { state.baseTask = "DORM3" }
        "DORM3" -> arknightsBaseFacilityDorm(text, "DORM3", state) { state.baseTask = "REALIGN14" }
        "REALIGN14" -> arknightsBaseRealign(text, state) { state.baseTask = "DORM4" }
        "DORM4" -> arknightsBaseFacilityDorm(text, "DORM4", state) { onCompletion() }
    }
}


suspend fun AutoService.arknightsAll(text: Text) {
    when (state.allState) {
        "HOME" -> arknightsHome(text) { state.allState = "BASE" }
        "BASE" -> arknightsBase(text, state) { state.allState = "RECR" }
        "RECR" -> arknightsRecruitment(text) { state.allState = "CREDITS" }
        "CREDITS" -> arknightsCredits(text) { state.allState = "0SANITY" }
        "0SANITY" -> arknightsZeroSanity(text) { state.allState = "REWARDS" }
        "REWARDS" -> arknightsRewards(text) { coma() }
    }
}