package com.magicalhag.autohag.auto.games.epicseven

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.buildClick
import com.magicalhag.autohag.auto.core.dispatch.dispatch
import com.magicalhag.autohag.auto.core.logging.log
import com.magicalhag.autohag.auto.core.text.check
import com.magicalhag.autohag.auto.core.text.find
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import kotlinx.coroutines.launch

// just keep it simple for now
// will use the same methods as everyone else
// if in the future i need to consider using different methods, then so be it

suspend fun AutoService.e7(text: Text) {
    when (EpicSevenState.task) {

        EpicSevenState.Task.Startup -> e7Startup(text) {}
        EpicSevenState.Task.Home -> e7Home(text) {}
        EpicSevenState.Task.Hunt -> e7Hunt(text) { coma() }
        EpicSevenState.Task.Sanctuary -> e7Sanctuary(text) {}
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
    } else if (text.check("insufficient energy")) {
        onComplete()
    } else if (text.check("repeat (?:battle|battling)")) {
        dispatch(Point(1350, 300).buildClick())
    } else if(text.check("background battling", "cancel", "confirm")) {
        dispatch(text.find("confirm").buildClick())
        onComplete()
    } else {
        e7Home(text) {}
    }
}

suspend fun AutoService.e7Sanctuary(text: Text, onComplete: () -> Unit) {}
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