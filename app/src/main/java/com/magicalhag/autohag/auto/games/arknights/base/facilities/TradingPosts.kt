package com.magicalhag.autohag.auto.games.arknights.base.facilities

import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.base.arknightsBase
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.dispatch.buildSwipe
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.logging.log
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.text.find


// NO SIDE EFFECTS
suspend fun AutoService.arknightsBaseTradingPost(text: Text, tpNum: Int, onCompletion: () -> Unit) {
    if (text.check("overview", "building mode")) {
        val point = when (tpNum) {
            1 -> {
                Point(375, 470)
            }
            2 -> {
                Point(660, 470)
            }
            else -> {
                Point(230, 610)
            }
        }
        dispatch(point.buildClick())
    } else if(text.check("facility info", "operator")) {
        dispatch(text.find("originium order").buildClick())
    } else if(text.check("facilities")) {
        dispatch(Point(480, 960).buildClick())
        onCompletion()
    } else {
        arknightsBase(text) {}
    }
}


suspend fun AutoService.arknightsBaseTradingPostMoraleCheck(text: Text, onShortcut: () -> Unit, onCompletion: (List<String>) -> Unit) {

    suspend fun deselect() {

        val row1Ops = text.find("\\S", Rect(635, 475, 1050, 530))
        val row2Ops = text.find("\\S", Rect(635, 900, 830, 955))
        val ops = mutableListOf<Text.Line>()
        ops.addAll(row1Ops)
        ops.addAll(row2Ops)

        dispatch(text.find("deselect all").buildClick())
        dispatch(text.find("state", Rect(1530, 0, 2000, 130)).buildClick())
        dispatch(text.find("state", Rect(1530, 0, 2000, 130)).buildClick())
        onCompletion(ops.map { it.text.lowercase() })
    }

    if(text.check("fatigued")) {
        deselect()
    } else if(text.check("time remaining")) {
        val timeRemainingBlock = text.find("\\d", Rect(285, 215, 725, 385))
        val timeRemaining = timeRemainingBlock[0].text.replace(":", "").replace(".", "").replace("-", "").toInt()
        log(timeRemaining)

        if(timeRemaining < 240000) {
            deselect()
        } else {
            arknightsBaseConfirmOps(text) {}
            onShortcut()
        }
    }
}

suspend fun AutoService.arknightsBaseCheckAvailableOps(text: Text, page: Int, onCompletion: (List<String>) -> Unit) {

    val ops = mutableListOf<Text.Line>()
    val row1Ops = text.find("\\S", Rect(635, 475, 2335, 530))
    val row2Ops = text.find("\\S", Rect(635, 895, 2335, 950))
    ops.addAll(row1Ops)
    ops.addAll(row2Ops)

    if(page < 3) {
        dispatch(buildSwipe(Point(2140, 535), Point(815, 535), duration = 1500L))
        dispatch((Point(815, 535).buildClick()))
    }

    onCompletion(ops.map { it.text.lowercase() })
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

suspend fun AutoService.arknightsBaseAddNewOps(text: Text, page: Int, availableOpNames: List<String>, blacklistedOpNames: List<String>, alreadyFoundOpNames: List<String>, onCompletion: (List<String>) -> Unit) {

    log(availableOpNames.joinToString(", "))
    log(blacklistedOpNames.joinToString(", "))

    lateinit var targetOpNames: List<String>
    for(combo in combos) {
        if(availableOpNames.containsAll(combo) && !blacklistedOpNames.containsAll(combo)) {
            targetOpNames = combo
            break
        }
    }

    log(targetOpNames.joinToString(", "))

    val targetOpNamesFound = mutableListOf<String>()
    for(opName in targetOpNames) {
        if(text.check(opName) && !alreadyFoundOpNames.contains(opName)) {
            dispatch(text.find(opName).buildClick())
            targetOpNamesFound.add(opName)
        }
    }

    if(targetOpNamesFound.size != targetOpNames.size) {
        dispatch(buildSwipe(Point(815, 535), Point(2140, 535), duration = 1500L))
        dispatch((Point(2140, 535).buildClick()))
    }

    onCompletion(targetOpNamesFound)
}

suspend fun AutoService.arknightsBaseConfirmOps(text: Text, onCompletion: () -> Unit) {
    if(text.check("confirm")) {
        dispatch(text.find("confirm").buildClick())
        onCompletion()
    }
}