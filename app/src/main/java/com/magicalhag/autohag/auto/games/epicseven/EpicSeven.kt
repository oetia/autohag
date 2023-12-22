package com.magicalhag.autohag.auto.games.epicseven

import android.content.ComponentName
import android.content.Intent
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log

// just keep it simple for now
// will use the same methods as everyone else
// if in the future i need to consider using different methods, then so be it

fun AutoService.e7(text: Text) {
    when(EpicSevenState.task) {

        EpicSevenState.Task.Startup -> e7Startup(text) {}
        EpicSevenState.Task.Home -> e7Home(text) {}
        EpicSevenState.Task.Hunt -> e7Hunt(text) {}
        EpicSevenState.Task.Sanctuary -> e7Sanctuary(text) {}
        EpicSevenState.Task.Arena -> e7Arena(text) {}
    }
}

fun AutoService.e7Startup(text: Text, onComplete: () -> Unit) {}
fun AutoService.e7Home(text: Text, onComplete: () -> Unit) {}
fun AutoService.e7Hunt(text: Text, onComplete: () -> Unit) {}
fun AutoService.e7Sanctuary(text: Text, onComplete: () -> Unit) {}
fun AutoService.e7Arena(text: Text, onComplete: () -> Unit) {}

fun AutoService.e7Launch() {

    // adb shell 'dumpsys package | grep -Eo "^[[:space:]]+[0-9a-f]+[[:space:]]+com.stove.epic7.google/[^[:space:]]+" | grep -oE "[^[:space:]]+$"'
    // adb shell "am start -n com.stove.epic7.google/kr.supercreative.epic7.AppActivity"

    val launchIntent = Intent()
        .setComponent(ComponentName("com.stove.epic7.google", "kr.supercreative.epic7.AppActivity"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

    try {
        startActivity(launchIntent)
        log("E7 Launch Success")
    } catch (e: Exception) {
        throw Exception("E7 Launch Failure")
    }
}