package com.magicalhag.autohag.auto.games.arknights.misc

import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.find
import com.magicalhag.autohag.auto.utils.logging.log


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

