package com.magicalhag.autohag.auto.games.arknights.base

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.ArknightsState
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseCheckAvailableOperators
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseTradingPostAddNewOps
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseTradingPostDeselectOperators
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseTradingPostHome
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseTradingPostMoraleCheck
import com.magicalhag.autohag.auto.games.arknights.base.facilities.arknightsBaseTradingPostOperatorManagement
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

suspend fun AutoService.arknightsBaseTradingPost(
    text: Text, number: Int,
    state: ArknightsState,
    onCompletion: () -> Unit
) {
    when (state.tpTask) {
        "HOME" -> arknightsBaseTradingPostHome(text, number) { state.tpTask = "OPERATOR_MANAGEMENT" }
        "OPERATOR_MANAGEMENT" -> arknightsBaseTradingPostOperatorManagement(text, number) { state.tpTask = "CHECK_AVAILABLE_OPERATORS" }
        "CHECK_AVAILABLE_OPERATORS" -> arknightsBaseCheckAvailableOperators(
            text, number, state.tpAvailableOpNames,
            { state.tpAvailableOpNames.addAll(it) },
            { state.tpTask = "RETURN_TO_OPERATOR_MANAGEMENT" })
        // inefficiency involving a double return ok for now i guess...
        "RETURN_TO_OPERATOR_MANAGEMENT" -> arknightsBaseTradingPostOperatorManagement(text, number) { state.tpTask = "CURRENT_OPERATOR_MORALE_CHECK" }
        "CURRENT_OPERATOR_MORALE_CHECK" -> arknightsBaseTradingPostMoraleCheck(
            text,
            { state.tpTask = "ADD_NEW_OPS" },
            { state.tpBlacklistedOpNames.addAll(it); state.tpTask = "DESELECT_OPERATORS" },
            { state.tpBlacklistedOpNames.addAll(it); state.tpTask = "DONE" })
        "DESELECT_OPERATORS" -> arknightsBaseTradingPostDeselectOperators(text, number) { state.tpTask = "ADD_NEW_OPS" }
        "ADD_NEW_OPS" -> arknightsBaseTradingPostAddNewOps(
            text,
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
