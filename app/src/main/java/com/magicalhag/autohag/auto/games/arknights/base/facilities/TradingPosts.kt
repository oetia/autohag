package com.magicalhag.autohag.auto.games.arknights.base.facilities

import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.base.misc.arknightsBaseHome
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.dispatch.buildSwipe
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.logging.log
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.text.find
import kotlinx.coroutines.delay


suspend fun AutoService.arknightsBaseTradingPostHome(text: Text, number: Int, onCompletion: () -> Unit) {
    if (text.check("overview", "building mode")) {
        val point = when (number) {
            1 -> { Point(375, 470) }
            2 -> { Point(660, 470) }
            else -> { Point(230, 610) }
        }
        dispatch(point.buildClick())
        onCompletion()
    } else {
        arknightsBaseHome(text) {}
    }
}

suspend fun AutoService.arknightsBaseTradingPostOperatorManagement(text: Text, number: Int, onCompletion: () -> Unit) {
    if(text.check("facility info", "operator")) {
        dispatch(text.find("originium order").buildClick())
    } else if(text.check("facilities")) {
        dispatch(Point(480, 960).buildClick())
        onCompletion()
    } else {
        arknightsBaseTradingPostHome(text, number) {}
    }
}

suspend fun AutoService.arknightsBaseCheckAvailableOperators(
    text: Text, number: Int, availableOpNames: List<String>,
    onContinuation: (List<String>) -> Unit,
    onCompletion: () -> Unit
) {
    if(text.check("deselect all")) {
        if(availableOpNames.size < 40) {
            val ops = mutableListOf<Text.Line>()
            val row1Ops = text.find("\\S", Rect(635, 475, 2335, 530))
            val row2Ops = text.find("\\S", Rect(635, 895, 2335, 950))
            ops.addAll(row1Ops)
            ops.addAll(row2Ops)

            dispatch(buildSwipe(Point(2140, 535), Point(815, 535), duration = 1500L))
            dispatch((Point(815, 535).buildClick()))

            onContinuation(ops.map { it.text.lowercase() })
        } else {
            onCompletion()
        }
    } else {
        arknightsBaseTradingPostOperatorManagement(text, number) {}
    }
}


suspend fun AutoService.arknightsBaseTradingPostMoraleCheck(
    text: Text,
    onEmpty: () -> Unit,
    onLowMorale: (List<String>) -> Unit,
    onHighMorale: (List<String>) -> Unit
) {
    if(text.check("time remaining")) {
        val ops = mutableListOf<Text.Line>()
        val row1Ops = text.find("\\S", Rect(635, 475, 1050, 530))
        val row2Ops = text.find("\\S", Rect(635, 900, 830, 955))
        ops.addAll(row1Ops)
        ops.addAll(row2Ops)
        val opNames = ops.map { it.text.lowercase() }

        if(text.check("fatigued")) {
            onLowMorale(opNames)
        } else {
            val timeRemainingBlock = text.find("\\d", Rect(285, 215, 725, 385))
            val timeRemaining = timeRemainingBlock[0].text.replace(":", "").replace(".", "").replace("-", "").toInt()

            if(timeRemaining < 240000) {
                onLowMorale(opNames)
            } else {
                onHighMorale(opNames)
            }
        }
    } else {
        onEmpty()
    }
}


suspend fun AutoService.arknightsBaseTradingPostDeselectOperators(text: Text, number: Int, onCompletion: () -> Unit) {
    if (text.check("deselect all")) {
        dispatch(text.find("deselect all").buildClick())
        dispatch(text.find("state", Rect(1530, 0, 2000, 130)).buildClick())
        dispatch(text.find("state", Rect(1530, 0, 2000, 130)).buildClick())

        onCompletion()
    } else {
        arknightsBaseTradingPostOperatorManagement(text, number) {}
    }
}

val combos = listOf(
    listOf("lappland", "texas", "exusiai"),
    listOf("shamare", "kafka", "tequila"),
    listOf("gummy", "catapult", "midnight"),
    listOf("matoimaru", "ambriel", "mousse"),
    listOf("fang", "yato", "melantha"),
    listOf("quartz", "pozëmka", "tuye"),
    listOf("silverash", "jaye", "myrtle")
)

suspend fun AutoService.arknightsBaseTradingPostAddNewOps(
    text: Text,
    availableOpNames: List<String>,
    blacklistedOpNames: List<String>,
    alreadySelectedOpNames: MutableList<String>,
    onCompletion: (List<String>) -> Unit
) {
    var targetOpNames = mutableListOf<String>()
    for(combo in combos) {
        if(availableOpNames.containsAll(combo) && !blacklistedOpNames.containsAll(combo)) {
            targetOpNames.addAll(combo)
            break
        }
    }

    if(targetOpNames.isNotEmpty()) {
        for(opName in targetOpNames) {
            if(text.check(opName) && !alreadySelectedOpNames.contains(opName)) {
                dispatch(text.find(opName).buildClick())
                alreadySelectedOpNames.add(opName)
            }
        }

        if(alreadySelectedOpNames.containsAll(targetOpNames)) {
            dispatch(text.find("confirm").buildClick())
            delay(1000)
            onCompletion(targetOpNames)
        } else {
            dispatch(buildSwipe(Point(2140, 535), Point(815, 535), duration = 1500L))
            dispatch((Point(815, 535).buildClick()))
        }
    } else { // just take the first three
        dispatch(Point(730, 500).buildClick())
        dispatch(Point(730, 925).buildClick())
        dispatch(Point(1025, 500).buildClick())

        dispatch(text.find("confirm").buildClick())
        delay(1000)
        onCompletion(listOf()) // theoretically no conflict from not blacklisting these
    }
}