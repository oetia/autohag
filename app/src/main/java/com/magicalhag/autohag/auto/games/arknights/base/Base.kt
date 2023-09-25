package com.magicalhag.autohag.auto.games.arknights.base

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.ArknightsState
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseFacilityAddNewOps
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseFacilityCheckAvailableOperators
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseFacilityDormAddNewOps
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseFacilityDormCheckFull
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseFacilityHome
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseFacilityMoraleCheck
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseFacilityOperatorClear
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseFacilityOperatorManagement
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
    when (state.facilityTask) {
        "HOME" -> arknightsBaseFacilityHome(text, facilityId) { state.facilityTask = "OPERATOR_MANAGEMENT" }
        "OPERATOR_MANAGEMENT" -> arknightsBaseFacilityOperatorManagement(text, facilityId) { state.facilityTask = "CHECK_AVAILABLE_OPERATORS" }
        "CHECK_AVAILABLE_OPERATORS" -> arknightsBaseFacilityCheckAvailableOperators(
            text, facilityId, state.facilityAvailableOpNames,
            { state.facilityAvailableOpNames.addAll(it) },
            { state.facilityTask = "RETURN_TO_OPERATOR_MANAGEMENT" })
        "RETURN_TO_OPERATOR_MANAGEMENT" -> arknightsBaseFacilityOperatorManagement(text, facilityId) { state.facilityTask = "CURRENT_OPERATOR_MORALE_CHECK" }
        "CURRENT_OPERATOR_MORALE_CHECK" -> arknightsBaseFacilityMoraleCheck(
            text, facilityId,
            { state.facilityTask = "ADD_NEW_OPS" },
            { state.facilityBlacklistedOpNames.addAll(it); state.facilityTask = "OPERATOR_CLEAR" },
            { state.facilityBlacklistedOpNames.addAll(it); state.facilityTask = "DONE" },
            { state.facilityBlacklistedOpNames.addAll(it); state.facilityTask = "DONE" })
        "OPERATOR_CLEAR" -> arknightsBaseFacilityOperatorClear(text, facilityId) { state.facilityTask = "ADD_NEW_OPS" }
        "ADD_NEW_OPS" -> arknightsBaseFacilityAddNewOps(
            text, facilityId,
            state.facilityAvailableOpNames,
            state.facilityBlacklistedOpNames,
            state.facilityAlreadySelectedOpNames
        ) { state.facilityBlacklistedOpNames.addAll(it); state.facilityTask = "RETURN_TO_OPERATOR_MANAGEMENT" }


        "DONE" -> {
            state.tpReset()
            onCompletion()
        }
    }
}


suspend fun AutoService.arknightsBaseFacilityDorm(
    text: Text, facilityId: String,
    state: ArknightsState,
    onCompletion: () -> Unit
) {
    when (state.facilityTask) {
        "HOME" -> arknightsBaseFacilityHome(text, facilityId) { state.facilityTask = "OPERATOR_MANAGEMENT" }
        "OPERATOR_MANAGEMENT" -> arknightsBaseFacilityOperatorManagement(text, facilityId) { state.facilityTask = "DORM_CHECK_FULL" }
        "DORM_CHECK_FULL" -> arknightsBaseFacilityDormCheckFull(text,) { state.facilityTask = "CURRENT_OPERATOR_MORALE_CHECK" }
        "CURRENT_OPERATOR_MORALE_CHECK" -> arknightsBaseFacilityMoraleCheck(
            text, facilityId,
            { state.facilityTask = "ADD_NEW_OPS" },
            { state.facilityBlacklistedOpNames.addAll(it); state.facilityTask = "DONE" },
            { state.facilityBlacklistedOpNames.addAll(it); state.facilityTask = "DONE" },
            { state.facilityTask = "OPERATOR_CLEAR" })
        "OPERATOR_CLEAR" -> arknightsBaseFacilityOperatorClear(text, facilityId) { state.facilityTask = "ADD_NEW_OPS" }
        "ADD_NEW_OPS" -> arknightsBaseFacilityDormAddNewOps(
            text,
            state.facilityBlacklistedOpNames,
            state.facilityAlreadySelectedOpNames
        ) { state.facilityTask = "DONE"; state.facilityBlacklistedOpNames = mutableListOf(); state.facilityAlreadySelectedOpNames = mutableListOf() }


        "DONE" -> {
            state.tpReset()
            onCompletion()
        }
    }
}
