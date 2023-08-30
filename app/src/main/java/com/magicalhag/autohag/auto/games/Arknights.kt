package com.magicalhag.autohag.auto.games

import android.content.ComponentName
import android.content.Intent
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.AutoService
import kotlinx.coroutines.launch
import com.magicalhag.autohag.auto.*
import kotlinx.coroutines.delay


suspend fun AutoService.arknights(ocrout: Text) {
    when (state) {
        "launch" -> arknightsLaunch()
        "login" -> arknightsLogin(ocrout)
        "0SANITY" -> arknightsZeroSanity(ocrout)
        "RECR" -> arknightsRecruit(ocrout)
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


suspend fun AutoService.arknightsLogin(ocrout: Text) {
    if (ocrout.check("clear cache", "start"))
        dispatch(ocrout.find("start").buildClick())
    else if (ocrout.check("start", "check preannounce", "account management", "customer service"))
        dispatch(ocrout.find("start").buildClick())
}

suspend fun AutoService.arknightsZeroSanity(ocrout: Text) {
    if (ocrout.check("friends", "archive", "sanity/")) {
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
        coroutineScope.launch { nap(15 * 1000) }
    } else if (ocrout.check("mission\\s+results")) {
        dispatch(ocrout.find("mission\\s+results").buildClick())
    } else if (ocrout.check("restore")) {
        coma()
    }
}

val arknightsRecruitCombos = arrayOf(
    // https://gamepress.gg/arknights/core-gameplay/arknights-operator-recruitment-guide

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
        }
    } else if (ocrout.check("skip")) {
        dispatch(ocrout.find("skip").buildClick())
    } else if (ocrout.check("certificate")) {
        dispatch(ocrout.find("certificate").buildClick())
    } else if (ocrout.check("job", "tags")) {
        if (ocrout.check("top operator")) {
            coma()
        } else {
            for (combo in arknightsRecruitCombos) {
                if (ocrout.check(*combo)) {
                    log("4+*")
                    log(combo)
                    for (tag in combo) {
                        dispatch(ocrout.find("tag").buildClick())
                        delay(300)
                    }

                    dispatch(Point(900, 450).buildClick())
                    delay(300)
                    dispatch(Point(1675, 875).buildClick())
                    delay(300)

                    return
                }
            }

            log("3*")

            // refresh if available
            if(ocrout.check("tap to refresh")) {
                dispatch(ocrout.find("tap to refresh").buildClick())
                delay(300)
                dispatch(Point(1600, 750).buildClick())
                delay(300)
            } else {
                dispatch(Point(900, 450).buildClick())
                delay(300)
                dispatch(Point(1675, 875).buildClick())
                delay(300)
            }

        }
    }
}

suspend fun AutoService.arknightsBase() {

}

/*
look for text
click on text

works everywhere but base

 */

