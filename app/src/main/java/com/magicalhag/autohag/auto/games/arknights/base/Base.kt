package com.magicalhag.autohag.auto.games.arknights.base

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.ArknightsState
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseFacilityAddNewOps
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseFacilityCheckAvailableOperators
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseFacilityHome
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseFacilityMoraleCheck
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseFacilityOperatorClear
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseFacilityOperatorManagement
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseCollection
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseHome

suspend fun AutoService.arknightsBaseRealign(
    text: Text,
    state: ArknightsState,
    onComplete: () -> Unit
) {
    when (state.realignTask) {
        "HOME" -> arknightsBaseHome(text) { state.realignTask = "COLLECT" }

        "COLLECT" -> arknightsBaseCollection(
            text, false,
            { state.realignTask = "COLLECT_E" },
            { state.realignTask = "DONE" })

        "COLLECT_E" -> arknightsBaseCollection(
            text, true,
            { state.realignTask = "COLLECT_E" },
            { state.realignTask = "DONE" })

        "DONE" -> {
            state.realignReset()
            onComplete()
        };
    }
}

suspend fun AutoService.arknightsBaseFacility(
    text: Text, facilityId: String,
    state: ArknightsState,
    onCompletion: () -> Unit
) {
    when (state.tpTask) {
        "HOME" -> arknightsBaseFacilityHome(text, facilityId) { state.tpTask = "OPERATOR_MANAGEMENT" }
        "OPERATOR_MANAGEMENT" -> arknightsBaseFacilityOperatorManagement(text, facilityId) { state.tpTask = "CHECK_AVAILABLE_OPERATORS" }
        "CHECK_AVAILABLE_OPERATORS" -> arknightsBaseFacilityCheckAvailableOperators(
            text, facilityId, state.tpAvailableOpNames,
            { state.tpAvailableOpNames.addAll(it) },
            { state.tpTask = "RETURN_TO_OPERATOR_MANAGEMENT" })
        "RETURN_TO_OPERATOR_MANAGEMENT" -> arknightsBaseFacilityOperatorManagement(text, facilityId) { state.tpTask = "CURRENT_OPERATOR_MORALE_CHECK" }
        "CURRENT_OPERATOR_MORALE_CHECK" -> arknightsBaseFacilityMoraleCheck(
            text, facilityId,
            { state.tpTask = "ADD_NEW_OPS" },
            { state.tpBlacklistedOpNames.addAll(it); state.tpTask = "OPERATOR_CLEAR" },
            { state.tpBlacklistedOpNames.addAll(it); state.tpTask = "DONE" })
        "OPERATOR_CLEAR" -> arknightsBaseFacilityOperatorClear(text, facilityId) { state.tpTask = "ADD_NEW_OPS" }
        "ADD_NEW_OPS" -> arknightsBaseFacilityAddNewOps(
            text, facilityId,
            state.tpAvailableOpNames,
            state.tpBlacklistedOpNames,
            state.tpAlreadySelectedOpNames
        ) { state.tpBlacklistedOpNames.addAll(it); state.tpTask = "RETURN_TO_OPERATOR_MANAGEMENT" }


        "DONE" -> {
            state.tpReset()
            onCompletion()
        }
        // "CHECK_OPS_P1" -> arknightsBaseCheckAvailableOps(text, )
        // "TP1" -> arknightsBaseTradingPostEnter(text, 1) { task = "TP1_CHECK_OPS" }
        // "DONE" -> onCompletion()
        //
        // "RESET" -> tradingPostState = "ENTER"
    }

}
