package com.magicalhag.autohag.auto.games.arknights.base.misc

import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.dispatch.buildSwipe
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.text.find
import kotlinx.coroutines.delay

// i think that most of this code can be re-used for every single facility type

val facilityIdToPoint = hashMapOf<String, Point>(
    "CC" to Point(1455, 265),

    "TP1" to Point(375, 470),
    "TP2" to Point(660, 470),
    "PP1" to Point(955, 470),

    "TP3" to Point(230, 615) ,
    "FAC1" to Point(515, 615),
    "PP2" to Point(805, 615),

    "FAC2" to Point(375, 760),
    "FAC3" to Point(660, 760),
    "PP3" to Point(955, 760),

    "OFFICE" to Point(2135, 610),

    "DORM1" to Point(1390, 470),
    "DORM2" to Point(1540, 615),
    "DORM3" to Point(1390, 760),
    "DORM4" to Point(1540, 905)
)

val facilityCombos = hashMapOf<String, List<List<String>>>(
    "CC" to listOf(
        listOf("kal'tsit", "amiya", "tachanka", "blitz", "frost"),
    ),
    "TP" to listOf(
        listOf("lappland", "texas", "exusiai"),
        listOf("shamare", "kafka", "tequila"),
        listOf("gummy", "catapult", "midnight"),
        listOf("matoimaru", "ambriel", "mousse"),
        listOf("fang", "yato", "melantha"),
        listOf("quartz", "pozëmka", "tuye"),
        listOf("silverash", "jaye", "myrtle")
    ),
    "FAC" to listOf(
        listOf("purestream", "weedy", "eunectes"),
        listOf("gravel", "haze", "spot"),
        listOf("mizuki", "highmore", "ptilopsis"),
        listOf("ceobe", "vermeil", "beanstalk"),
        listOf("perfumer", "roberta", "jessica"),
        listOf("vulcan", "bena", "bubble"),
        listOf("vanilla", "steward", "asbestos")
    ),
    "PP" to listOf(),
    "OFFICE" to listOf(),
    "DORM" to listOf(),

)

suspend fun AutoService.arknightsBaseFacilityHome(text: Text, facilityId: String, onCompletion: () -> Unit) {
    if (text.check("overview", "building mode")) {
        val point = facilityIdToPoint[facilityId]
        dispatch(point!!.buildClick())
        onCompletion()
    } else {
        arknightsBaseHome(text) {}
    }
}

suspend fun AutoService.arknightsBaseFacilityOperatorManagement(text: Text, facilityId: String, onCompletion: () -> Unit) {
    if(text.check("facility info", "operator")) {
        if(text.check("assigned operators")) {
            onCompletion()
        } else {
            dispatch(text.find("operator").buildClick())
        }
    } else {
        arknightsBaseFacilityHome(text, facilityId) {}
    }
}

suspend fun AutoService.arknightsBaseFacilityCheckAvailableOperators(
    text: Text, facilityId: String, availableOpNames: List<String>,
    onContinuation: (List<String>) -> Unit,
    onCompletion: () -> Unit
) {
    // no need to check available ops if checked prior or no combos
    val facilityType = facilityId.replace(Regex("\\d+"), "")
    if(availableOpNames.size >= 40 || facilityType == "PP" || facilityType == "OFFICE" || facilityType == "DORM") {
        onCompletion(); return;
    }

    if(text.check("facility info", "operator", "assigned operators")) {
        dispatch(Point(1800, 235).buildClick())
    }
    else if(text.check("deselect all")) {
        val ops = mutableListOf<Text.Line>()
        val row1Ops = text.find("\\S", Rect(635, 475, 2335, 530))
        val row2Ops = text.find("\\S", Rect(635, 895, 2335, 950))
        ops.addAll(row1Ops)
        ops.addAll(row2Ops)

        dispatch(buildSwipe(Point(2140, 535), Point(815, 535), duration = 1500L))
        dispatch((Point(815, 535).buildClick()))

        onContinuation(ops.map { it.text.lowercase() })
    } else {
        arknightsBaseFacilityOperatorManagement(text, facilityId) {}
    }
}


suspend fun AutoService.arknightsBaseFacilityMoraleCheck(
    text: Text, facilityId: String,
    onEmpty: () -> Unit,
    onLowMorale: (List<String>) -> Unit,
    onHighMorale: (List<String>) -> Unit,
    onFullMorale: (List<String>) -> Unit,
) {

    // ensure that it's either entirely full or entirely empty

    if(text.check("facility info", "operator", "assigned operators")) {
        if(text.check("morale(?!\\s+restored)")) {
            val moraleBlock = text.find("\\d", Rect(2085, 210, 2210, 260))
            val moraleText = moraleBlock[0].text
            val moraleRemaining: Int = moraleText.split("/")[0].replace("O", "0").toInt()

            val opNameLines = mutableListOf<Text.Line>()
            val opNameLine1 = text.find("\\S", Rect(1800, 165, 2210, 215))
            val opNameLine2 = text.find("\\S", Rect(1800, 375, 2210, 425))
            val opNameLine3 = text.find("\\S", Rect(1800, 585, 2210, 635))
            opNameLines.addAll(opNameLine1); opNameLines.addAll(opNameLine2); opNameLines.addAll(opNameLine3);
            val opNames = opNameLines.map { it.text.lowercase() }

            if(moraleRemaining == 24) {
                onFullMorale(opNames)
            } else if(moraleRemaining > 12) {
                onHighMorale(opNames)
            } else {
                onLowMorale(opNames)
            }
        } else {
            onEmpty()
        }
    } else {
        arknightsBaseFacilityOperatorManagement(text, facilityId) {}
    }
}


suspend fun AutoService.arknightsBaseFacilityOperatorClear(text: Text, facilityId: String, onCompletion: () -> Unit) {
    if (text.check("facility info", "assigned operators", "clear")) {
        val operatorLine = text.find("\\d", Rect(2040, 990, 2335, 1060))
        val operatorLineSplit = operatorLine[0].text.split(" ")
        val operatorRatio = operatorLineSplit[operatorLineSplit.size - 1]
        val operatorCount = operatorRatio.split("/")[0].replace("O", "0").toInt()

        if(operatorCount == 0) {
            onCompletion()
        } else {
            dispatch(text.find("clear").buildClick())
        }
    } else if (text.check("selected operators will be removed")) {
        dispatch(Point(1685, 740).buildClick())
        onCompletion()
    } else {
        arknightsBaseFacilityOperatorManagement(text, facilityId) {}
    }
}

suspend fun AutoService.arknightsBaseFacilityAddNewOps(
    text: Text, facilityId: String,
    availableOpNames: List<String>,
    blacklistedOpNames: List<String>,
    alreadySelectedOpNames: MutableList<String>,
    onCompletion: (List<String>) -> Unit
) {

    if(text.check("facility info", "assigned operators")) {
        dispatch(Point(1990, 235).buildClick())
    } else {
        val facilityType = facilityId.replace(Regex("\\d+"), "")
        val targetOpNames = mutableListOf<String>()
        for(combo in facilityCombos[facilityType]!!) {
            // if(availableOpNames.containsAll(combo) && !blacklistedOpNames.containsAll(combo)) {
            if(availableOpNames.containsAll(combo) && !combo.any { blacklistedOpNames.contains(it) }) {
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
            if(facilityType == "PP" || facilityType == "OFFICE") {
                dispatch(Point(730, 500).buildClick())
            } else if(facilityType == "TP" || facilityType == "FAC") {
                dispatch(Point(730, 500).buildClick())
                dispatch(Point(730, 925).buildClick())
                dispatch(Point(950, 500).buildClick())
            } else if(facilityType == "DORM" || facilityType == "CC") {
                dispatch(Point(730, 500).buildClick())
                dispatch(Point(730, 925).buildClick())
                dispatch(Point(950, 500).buildClick())
                dispatch(Point(950, 925).buildClick())
                dispatch(Point(1160, 500).buildClick())
            }

            dispatch(text.find("confirm").buildClick())
            delay(1000)
            onCompletion(listOf()) // theoretically no conflict from not blacklisting these
        }
    }
}

suspend fun AutoService.arknightsBaseFacilityDormCheckFull(text: Text, onCompletion: () -> Unit) {
    if (text.check("facility info", "assigned operators", "clear")) {
        val operatorLine = text.find("\\d", Rect(2040, 990, 2335, 1060))
        val operatorLineSplit = operatorLine[0].text.split(" ")
        val operatorRatio = operatorLineSplit[operatorLineSplit.size - 1]
        val operatorCount = operatorRatio.split("/")[0].replace("O", "0").toInt()

        if(operatorCount != 5) {
            dispatch(text.find("clear").buildClick())
            delay(1000)
        }

        onCompletion()
    }
}

suspend fun AutoService.arknightsBaseFacilityDormAddNewOps(
    text: Text,
    blacklistedOpNames: MutableList<String>,
    alreadySelectedOpNames: MutableList<String>,
    onCompletion: () -> Unit
) {
    if(text.check("facility info", "operator", "assigned operators")) {
        dispatch(Point(1990, 235).buildClick())
    } else if(text.check("deselect all")) {
        val ops = mutableListOf<Text.Line>()
        val row1Ops = text.find("\\S", Rect(635, 475, 2335, 530))
        val row2Ops = text.find("\\S", Rect(635, 895, 2335, 950))
        for(i in 0 until kotlin.math.min(row1Ops.size, row2Ops.size)) {
            val row1OpBB = Rect(row1Ops[i].boundingBox!!.right - 200, row1Ops[i].boundingBox!!.bottom - 300, row1Ops[i].boundingBox!!.right, row1Ops[i].boundingBox!!.bottom - 100)
            val row2OpBB = Rect(row2Ops[i].boundingBox!!.right - 200, row2Ops[i].boundingBox!!.bottom - 300, row2Ops[i].boundingBox!!.right, row2Ops[i].boundingBox!!.bottom - 100)

            if(text.find("[o0]n", row1OpBB).isEmpty() && text.find("shift", row1OpBB).isEmpty()) {
                ops.add(row1Ops[i])
            }
            if(text.find("[o0]n", row2OpBB).isEmpty() && text.find("shift", row2OpBB).isEmpty()) {
                ops.add(row2Ops[i])
            }
        }

        for(op in ops.take(5)) {
            dispatch(op.buildClick())
        }
        dispatch(text.find("confirm").buildClick())
        delay(1000)
        onCompletion()
    }
}

// suspend fun AutoService.arknightsBaseFacilityDormAddNewOps(
//     text: Text,
//     blacklistedOpNames: MutableList<String>,
//     alreadySelectedOpNames: MutableList<String>,
//     onCompletion: () -> Unit
// ) {
//     if(alreadySelectedOpNames.size >= 5) {
//         dispatch(text.find("confirm").buildClick())
//         onCompletion()
//     } else if(text.check("facility info", "assigned operators")) {
//         dispatch(Point(1990, 235).buildClick())
//     } else if(text.check("deselect all")){
//         if(text.check("tap on an operator") || text.check("time remaining", "[il]dle")) {
//             val ops = mutableListOf<Text.Line>()
//             val row1Ops = text.find("\\S", Rect(635, 475, 2335, 530))
//             val row2Ops = text.find("\\S", Rect(635, 895, 2335, 950))
//             for(i in 0 until kotlin.math.min(row1Ops.size, row2Ops.size)) {
//                 ops.add(row1Ops[i])
//                 ops.add(row2Ops[i])
//             }
//             val opNames = ops.map { it.text.lowercase() }
//
//             for(opName in opNames) {
//                 if(opName !in alreadySelectedOpNames && opName !in blacklistedOpNames) {
//                     dispatch(text.find(opName).buildClick())
//                     alreadySelectedOpNames.add(opName)
//                     return
//                 }
//             }
//             onCompletion()
//         } else if(text.check("time remaining", "working")) {
//             val lastSelectedOpName = alreadySelectedOpNames[alreadySelectedOpNames.lastIndex]
//             alreadySelectedOpNames.remove(lastSelectedOpName)
//             blacklistedOpNames.add(lastSelectedOpName)
//             dispatch(text.find(lastSelectedOpName).buildClick())
//         }
//     }
// }