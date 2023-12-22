package com.magicalhag.autohag.auto.games.arknights.misc

import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.buildClick
import com.magicalhag.autohag.auto.core.text.check
import com.magicalhag.autohag.auto.core.dispatch.dispatch
import com.magicalhag.autohag.auto.core.text.find
import com.magicalhag.autohag.auto.core.logging.log
import kotlinx.coroutines.delay


suspend fun AutoService.arknightsRecruitment(text: Text, onCompletion: () -> Unit) {

    fun findBestTagCombo(): Array<String>? {
        for (combo in arknightsRecruitCombos) {
            if (text.check(*combo)) {
                log(combo)
                return combo
            }
        }
        return null
    }
    suspend fun selectTags(combo: Array<String>) {
        for (tag in combo) {
            dispatch(text.find(tag).buildClick())
        }
    }
    suspend fun confirmRecruitment() {
        dispatch(Point(900, 450).buildClick())
        dispatch(Point(1675, 875).buildClick())
        delay(1000) // waiting on server - good practice
    }

    if (text.check("friends", "archive", "recruit")) {
        dispatch(text.find("recruit").buildClick())
    } else if (text.check("recruit(?!ment)", "1", "2", "3", "4")) {
        if (text.check("recruit now")) {
            dispatch(text.find("recruit now").buildClick())
        } else if (text.check("hire")) {
            dispatch(text.find("hire").buildClick())
            delay(1000)
        } else {
            onCompletion()
        }
    } else if (text.check("job", "tags")) {
        if (text.check("top operator")) {
            onCompletion()
            return
        }
        val bestTagCombo = findBestTagCombo()
        if(bestTagCombo != null) {
            selectTags(bestTagCombo)
            confirmRecruitment()
        } else {
            if(text.check("tap to refresh")) {
                dispatch(text.find("tap to refresh").buildClick())
            } else {
                confirmRecruitment()
            }
        }
    } else if (text.check("skip")) {
        dispatch(text.find("skip").buildClick())
    } else if (text.check("certificate")) {
        dispatch(text.find("certificate").buildClick())
    } else if (text.check("spend 1 refresh attempt?")) {
        dispatch(Point(1600, 750).buildClick())
        delay(1000)
    } else {
        arknightsHome(text) {}
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
