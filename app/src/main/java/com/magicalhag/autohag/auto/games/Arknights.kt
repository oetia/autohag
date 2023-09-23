package com.magicalhag.autohag.auto.games

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.AutoService
import kotlinx.coroutines.launch
import com.magicalhag.autohag.auto.*
import kotlinx.coroutines.delay


// have some kind of intermediate management function that tracks completion

suspend fun AutoService.arknights(ocrout: Text) {
    when (state) {
        "launch" -> arknightsLaunch()
        "HOME" -> arknightsHomewardBound(ocrout)
        "0SANITY" -> arknightsZeroSanity(ocrout)
        "RECR" -> arknightsRecruit(ocrout)
        "BASE" -> arknightsBase(ocrout)
        "RESET" -> resetBaseTpsState()
    }
}

fun AutoService.arknightsLaunch() {
    val launchIntent = Intent()
        .setComponent(ComponentName("com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

    try {
        startActivity(launchIntent)
        log("Arknights Opened")
    } catch (e: Exception) {
        log("Arknights Failed to Open")
    }
}

suspend fun AutoService.arknightsHomewardBound(ocrout: Text) {

    // if (
    //     ocrout.check("oripathy") ||
    //     ocrout.check("orignium") ||
    //     ocrout.check("catastrophe") ||
    //     ocrout.check("nomadic city") ||
    //     ocrout.check("rhodes island") ||
    //     ocrout.check("lungmen") ||
    //     ocrout.check("victorian empire")
    // ) {
    //     dispatch(Point(500, 500).buildClick())
    // } else
    if (ocrout.check("start", "check preannounce", "account management", "customer service")) {
        dispatch(ocrout.find("start").buildClick())
    } else if (ocrout.check("terra")) {

    } else if(ocrout.check("resource today")) {
        dispatch(ocrout.find("resource today").buildClick())
    } else if (ocrout.check("daily supply")) {
        dispatch(Point(2075, 90).buildClick())
    } else if (ocrout.check("event", "system")) { // figure out how to get this right
        dispatch(Point(2075, 90).buildClick())
    } else {
        if(ocrout.check("friends", "archives", "squads", "operator")) {
            coma()
        } else {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        }
    } // also some state involving relogging
//     need to gather some more visual information for this to work truly properly
}

suspend fun AutoService.arknightsZeroSanity(ocrout: Text) {
    if (ocrout.check("friends", "archives", "sanity/")) {
        dispatch(ocrout.find("sanity/").buildClick())
    } else if (ocrout.check("to the most recent stage")) {
        dispatch(ocrout.find("to the most recent stage").buildClick())
    } else if (ocrout.check("auto deploy", "start")) {
        dispatch(ocrout.find("start").buildClick())
    } else if (ocrout.check(
            "the roster for this operation cannot be changed",
            "mission\\s+start"
        )
    ) {
        dispatch(ocrout.find("mission\\s+start").buildClick())
    } else if (ocrout.check("2x", "takeover", "unit limit")) {
        coroutineScope.launch { nap(15 * 1000) } // sleep & delay halt two different ways - delay provides a minimum
    } else if (ocrout.check("mission\\s+results")) {
        dispatch(ocrout.find("mission\\s+results").buildClick())
    } else if (ocrout.check("restore")) {
        coma()
    }
}

val arknightsRecruitCombos = arrayOf(
    // https://gamepress.gg/arknights/core-gameplay/arknights-operator-recruitment-guide
    // arrayOf("sniper"),

    // arrayOf("sniper", "medic", "supporter"),

    // 5*
    arrayOf("senior operator"),
    arrayOf("support(?!er)", "vanguard"),
    arrayOf("support(?!er)", "dp-recovery"),
    arrayOf("crowd-control"),
    arrayOf("survival", "defender"),
    arrayOf("survival", "defense"),
    arrayOf("defender", "dps"),
    arrayOf("defense", "dps"),
    arrayOf("defense", "(?<!van)guard"),
    arrayOf("shift", "defender"),
    arrayOf("shift", "defense"),
    arrayOf("shift", "slow"),
    arrayOf("specialist", "slow"),
    arrayOf("shift", "dps"),
    arrayOf("supporter", "dps"),
    arrayOf("debuff", "supporter"),
    arrayOf("debuff", "aoe"),
    arrayOf("debuff", "fast-redeploy"),
    arrayOf("debuff", "specialist"),
    arrayOf("debuff", "melee"),
    arrayOf("specialist", "survival"),
    arrayOf("specialist", "dps"),
    arrayOf("healing", "caster"),
    arrayOf("healing", "slow"),
    arrayOf("healing", "dps"),
    arrayOf("caster", "dps", "slow"),

    // 4*
    arrayOf("healing", "vanguard"),
    arrayOf("healing", "dp-recovery"),
    arrayOf("slow", "(?<!van)guard"),
    arrayOf("slow", "melee"),
    arrayOf("slow", "dps"),
    arrayOf("slow", "sniper"),
    arrayOf("slow", "ranged", "dps"),
    arrayOf("slow", "caster"),
    arrayOf("slow", "aoe"),
    arrayOf("survival", "sniper"),
    arrayOf("survival", "ranged"),
    arrayOf("specialist"),
    arrayOf("shift"),
    arrayOf("fast-redeploy"),
    arrayOf("debuff"),
    arrayOf("support(?!er)"),
    arrayOf("nuker")
)

suspend fun AutoService.arknightsRecruit(ocrout: Text) {
    if (ocrout.check("friends", "archive", "recruit")) {
        dispatch(ocrout.find("recruit").buildClick())
    } else if (ocrout.check("recruit(?!ment)", "1", "2", "3", "4")) {
        if (ocrout.check("recruit now")) {
            dispatch(ocrout.find("recruit now").buildClick())
        } else if (ocrout.check("hire")) {
            dispatch(ocrout.find("hire").buildClick())
        } else {
            coma()
        }
    } else if (ocrout.check("skip")) {
        dispatch(ocrout.find("skip").buildClick())
    } else if (ocrout.check("certificate")) {
        dispatch(ocrout.find("certificate").buildClick())
    } else if (ocrout.check("job", "tags")) {
        if (ocrout.check("top operator")) {
            coma()
            return
        } else if (ocrout.check("09")) {
            dispatch(Point(1675, 875).buildClick())
            return
        }

        var foundCombo = false
        for (combo in arknightsRecruitCombos) {
            if (ocrout.check(*combo)) {
                log(combo)
                for (tag in combo) {
                    dispatch(ocrout.find(tag).buildClick())
                }

                foundCombo = true
                break
            }
        }

        if(!foundCombo && ocrout.check("tap to refresh")) {
            dispatch(ocrout.find("tap to refresh").buildClick())
        } else {
            if(!foundCombo) { log("3*") }
            dispatch(Point(900, 450).buildClick())
        }
    } else if (ocrout.check("spend 1 refresh attempt?")) {
        dispatch(Point(1600, 750).buildClick())
    }
}

val baseFactoryOperatorCombos = arrayOf(
    arrayOf("purestream", "weedy", "eunectes"),
    arrayOf("gravel", "haze", "spot"),
    arrayOf("mizuki", "highmore", "ptilopsis"),
    arrayOf("ceobe", "vermeil", "beanstalk"),
)


// STATE

var baseCollectionDone = false
var baseCollectionHasEmergency = false

var baseTPsDone = false
var baseTPsNumDone = 0
var baseTPsOPsChecked = false
var baseTpsOPsCheckedRTF = false
var baseTPsNamesFound = mutableListOf<String>()
var baseTPsChosenCombos = mutableListOf<List<String>>()
val baseTPsCombos = listOf(
    listOf("lappland", "texas", "exusiai"),
    listOf("shamare", "kafka", "tequila"),
    listOf("gummy", "catapult", "midnight"),
    listOf("matoimaru", "ambriel", "mousse"),
    listOf("fang", "yato", "melantha"),
    listOf("quartz", "pozëmka", "tuye"),
    listOf("silverash", "jaye", "myrtle")
)
fun resetBaseTpsState() {
    baseTPsDone = false
    baseTPsNumDone = 0
    baseTPsOPsChecked = false
    baseTPsNamesFound = mutableListOf<String>()

}

//
suspend fun AutoService.arknightsBase(ocrout: Text) {

    baseCollectionDone = true
    if(ocrout.check("friends", "archives")) {
        dispatch(ocrout.find("base", Rect(1700, 860, 2095, 1045)).buildClick())
    }
    else if(!baseCollectionDone) {
        arknightsBaseCollection(ocrout)
    } else if(!baseTPsDone) {
        arknightsBaseTradingPosts(ocrout)
    }
}

suspend fun AutoService.arknightsBaseCollection(ocrout: Text) {
    if(ocrout.check("overview", "building mode")) {
        if(!baseCollectionHasEmergency) {
            dispatch(Point(2225, 125).buildClick())
        } else {
            dispatch(Point(2225, 225).buildClick())
        }
    } else if(ocrout.check("backlog")) {
        if(ocrout.check("collectable")) {
            dispatch(ocrout.find("collectable").buildClick())
        } else if(ocrout.check("orders acquired")) {
            dispatch(ocrout.find("orders acquired").buildClick())
        } else if(ocrout.check("trust")) {
            if(ocrout.check("max trust")) {
                dispatch(ocrout.find("backlog").buildClick())
                baseCollectionDone = true
            } else {
                dispatch(ocrout.find("trust").buildClick())
            }
        } else if(ocrout.check("clues")) {
            dispatch(ocrout.find("backlog").buildClick())
            baseCollectionDone = true
        } else {
            dispatch(ocrout.find("backlog").buildClick())
            baseCollectionHasEmergency = true
        }
    }
}

suspend fun AutoService.arknightsBaseTradingPosts(ocrout: Text) {
    if(ocrout.check("overview", "building mode")) {
        dispatch(ocrout.find("trading post").buildClick())
    } else if(ocrout.check("facility info", "operator")) {
        dispatch(Point(600, 900).buildClick())
    } else if(ocrout.check("facilities")) {
        dispatch(Point(125, 325 + baseTPsNumDone * 125).buildClick())
        dispatch(Point(425, 1000).buildClick())
    } else if(ocrout.check("deselect all")) {


        if(ocrout.check("time remaining")) { // operators are selected
            deselectIfNecessary(ocrout)
        }

    //     if(arknightsBaseNeedsSwap(ocrout)) {
    //
    //     } else {
    //
    //     }
    //
    //     if(!baseTPsOPsChecked) {
    //         log(baseTPsNamesFound.size)
    //         arknightsBaseCheckOps(ocrout)
    //     } else if(!baseTpsOPsCheckedRTF) {
    //         dispatch(buildSwipe(Point(2140, 535), Point(300, 535), 300L))
    //     } else if(baseTPsChosenCombos.size == 0) {
    //         for (baseTPsOPCombo in baseTPsCombos) {
    //             if (baseTPsNamesFound.containsAll(baseTPsOPCombo)) {
    //                 log(baseTPsOPCombo)
    //                 baseTPsChosenCombos.add(baseTPsOPCombo)
    //                 if(baseTPsChosenCombos.size == 3) { break }
    //             }
    //         }
    //     } else if (baseTPsChosenCombos.size == 3) {
    //         log("SANITY CHEC")
    //         val combo = baseTPsChosenCombos[baseTPsNumDone]
    //         log(combo)
    //     }
    }
}

suspend fun AutoService.arknightsBaseCheckOps(ocrout: Text) {
    val row1Names = ocrout.find("\\S", Rect(635, 475, 2335, 530))
    val row2Names = ocrout.find("\\S", Rect(635, 895, 2335, 950))
    val names = mutableListOf<Text.Line>()
    names.addAll(row1Names)
    names.addAll(row2Names)

    for (name in names) {
        baseTPsNamesFound.add(name.text.lowercase())
        log(name.text)
    }

    if("bagpipe" !in baseTPsNamesFound) {
        dispatch(buildSwipe(Point(2140, 535), Point(780, 535)))
        delay(10)
        dispatch(Point(780, 535).buildClick(duration = 500))
    } else {
        baseTPsOPsChecked = true
    }
}


suspend fun AutoService.deselectIfNecessary(ocrout: Text) {
    var shouldDeselect = false
    if(ocrout.check("fatigued")) {
        shouldDeselect = true
    } else if(ocrout.check("time remaining")) {
        val timeRemainingBlock = ocrout.find("\\d", Rect(285, 215, 725, 385))
        val timeRemaining = timeRemainingBlock[0].text.replace(":", "").toInt()
        log(timeRemaining)
        shouldDeselect = timeRemaining < 240000
    }

    if(shouldDeselect) {
        dispatch(ocrout.find("deselect all").buildClick())
        dispatch(ocrout.find("state", Rect(1530, 0, 2000, 130)).buildClick())
        dispatch(ocrout.find("state", Rect(1530, 0, 2000, 130)).buildClick())
    }
}

suspend fun AutoService.checkOps(ocrout: Text) {

}

suspend fun AutoService.arknightsBaseNeedsSwap(ocrout: Text): Boolean {
    if(ocrout.check("fatigued")) {
        return true
    } else if(ocrout.check("time remaining")) {
        val timeRemainingBlock = ocrout.find("\\d", Rect(285, 215, 725, 385))
        val timeRemaining = timeRemainingBlock[0].text.replace(":", "").toInt()
        log(timeRemaining)
        return timeRemaining < 240000
    }
    return true
}

suspend fun AutoService.arknightsBaseClearDorms(ocrout: Text) {

}
