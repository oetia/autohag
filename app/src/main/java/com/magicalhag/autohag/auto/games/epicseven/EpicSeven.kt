package com.magicalhag.autohag.auto.games.epicseven

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.buildClick
import com.magicalhag.autohag.auto.core.dispatch.buildElementClick
import com.magicalhag.autohag.auto.core.dispatch.dispatch
import com.magicalhag.autohag.auto.core.logging.log
import com.magicalhag.autohag.auto.core.text.check
import com.magicalhag.autohag.auto.core.text.find
import com.magicalhag.autohag.auto.core.text.findElements
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import kotlinx.coroutines.launch

// just keep it simple for now
// will use the same methods as everyone else
// if in the future i need to consider using different methods, then so be it

// if there's more than one possible decision on a screen, branch into separate
// feels so ugly tbh. goes against my abstraction ideas... but i guess it'll work.

suspend fun AutoService.e7(text: Text) {
    when (EpicSevenState.task) {
        EpicSevenState.Task.Startup -> e7Startup(text) {}
        EpicSevenState.Task.Home -> e7Home(text) {}
        EpicSevenState.Task.Hunt -> e7Hunt(text) { coma() }
        EpicSevenState.Task.SanctuaryHeart -> e7Sanctuary(text) { EpicSevenState.task = EpicSevenState.Task.SanctuaryForestPenguin }
        EpicSevenState.Task.SanctuaryForestPenguin -> e7Sanctuary(text) { EpicSevenState.task = EpicSevenState.Task.SanctuaryForestSpirit }
        EpicSevenState.Task.SanctuaryForestSpirit -> e7Sanctuary(text) { EpicSevenState.task = EpicSevenState.Task.SanctuaryForestMola }
        EpicSevenState.Task.SanctuaryForestMola -> e7Sanctuary(text) { coma() }

        EpicSevenState.Task.Arena -> e7Arena(text) {}
    }
}

suspend fun AutoService.e7Startup(text: Text, onComplete: () -> Unit) {}
suspend fun AutoService.e7Home(text: Text, onComplete: () -> Unit) {
    if(text.check("shop", "hero", "summon", "reputation")) {
        onComplete()
    } else {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
}
suspend fun AutoService.e7Hunt(text: Text, onComplete: () -> Unit) {
    if (text.check("shop", "hero", "summon", "reputation")) {
        dispatch(text.find("battle").buildClick())
    } else if (text.check("labyrinth", "spirit altar", "hunt", "abyss")) {
        dispatch(text.find("hunt").buildClick())
    } else if (text.check("wyvern hunt", "golem hunt", "banshee hunt")) {
        dispatch(text.find("banshee hunt").buildClick())
    } else if (text.check("banshee hunt", "statistics", "select team")) {
        dispatch(text.find("select team").buildClick())
    } else if (text.check("hunt stage 13", "boss guide", "start")) {
        dispatch(text.find("start").buildClick())
    }

    else if (text.check("repeat (?:battle|battling)")) {
        dispatch(Point(1350, 300).buildClick())
    } else if (text.check("background battling", "cancel", "confirm")) {
        dispatch(text.find("confirm").buildClick())
        onComplete()
    } else if (text.check("insufficient energy")) {
        onComplete()
    }

    else {
        e7Home(text) {}
    }
}

suspend fun AutoService.e7Sanctuary(text: Text, onComplete: () -> Unit): Any {
    if (text.check("shop", "hero", "summon", "reputation")) {
        return dispatch(text.findElements("sanctuary").buildElementClick())
    }

    if (text.check("heart of orbis", "forest of souls")) {
        return when(EpicSevenState.task) {
            EpicSevenState.Task.SanctuaryHeart ->
                dispatch(text.find("heart of orbis").buildClick())

            in listOf(
                EpicSevenState.Task.SanctuaryForestPenguin,
                EpicSevenState.Task.SanctuaryForestSpirit,
                EpicSevenState.Task.SanctuaryForestMola
            ) ->
                dispatch(text.find("forest of souls").buildClick())

            else -> Unit
        }
    }

    if(EpicSevenState.task == EpicSevenState.Task.SanctuaryHeart) {
        if (text.check("receive reward(?!s)")) {
            return if(!text.check("time left until receiving")) {
                dispatch(text.find("receive reward(?!s)").buildClick())
            } else {
                onComplete()
            }
        } else if(text.check("received", "tap to close")) {
            return dispatch(text.find("tap to close").buildClick())
        }
    }

    if(EpicSevenState.task.name.contains("SanctuaryForest")) {
        if(text.check("penguin nest", "spirit well", "molagora farm")) {
            return when(EpicSevenState.task) {
                EpicSevenState.Task.SanctuaryForestPenguin ->
                    dispatch(text.find("penguin nest").buildClick())
                EpicSevenState.Task.SanctuaryForestSpirit ->
                    dispatch(text.find("spirit well").buildClick())
                EpicSevenState.Task.SanctuaryForestMola ->
                    dispatch(text.find("molagora farm").buildClick())
                else -> Unit
            }
        } else if(text.check("time left until harvest")) {
            return onComplete()
        }
    }

    return e7Home(text) {}
}
suspend fun AutoService.e7Arena(text: Text, onComplete: () -> Unit) {}

fun AutoService.e7Launch() {

    // adb shell 'dumpsys package | grep -Eo "^[[:space:]]+[0-9a-f]+[[:space:]]+com.stove.epic7.google/[^[:space:]]+" | grep -oE "[^[:space:]]+$"'
    // adb shell "am start -n com.stove.epic7.google/kr.supercreative.epic7.AppActivity"

    val launchIntent = Intent().setComponent(
        ComponentName(
            "com.stove.epic7.google", "kr.supercreative.epic7.AppActivity"
        )
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

    try {
        startActivity(launchIntent)
        log("E7 Launch Success")
    } catch (e: Exception) {
        throw Exception("E7 Launch Failure")
    }
}