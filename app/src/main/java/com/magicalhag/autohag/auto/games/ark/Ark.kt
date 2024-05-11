package com.magicalhag.autohag.auto.games.ark

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK
import android.content.ComponentName
import android.content.Intent
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.DispatchUtils
import com.magicalhag.autohag.auto.core.dispatch.generateDispatchUtils
import com.magicalhag.autohag.auto.core.text.StateCheckUtils
import com.magicalhag.autohag.auto.core.text.generateStateCheckUtils
import com.magicalhag.autohag.auto.games.ark.misc.findBestTagCombo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// @formatter:off
suspend fun AutoService.ark(text: Text, onComplete: () -> Boolean) {
    val s = generateStateCheckUtils(text, arknightsStateCheckDictionary)
    val d = generateDispatchUtils(text)

    when (ArkS.t) {
        ArkS.T.Startup -> arkStartup(text, d) { ArkS.t = ArkS.T.Home; true }
        ArkS.T.Home -> arkHome(s, d) { true }
        ArkS.T.Home -> arkHome(s, d) { ArkS.t = ArkS.T.Recruit; true }
        ArkS.T.Recruit -> arkRecruit(text, s, d) { ArkS.t = ArkS.T.Credits; true }
        ArkS.T.Credits -> arkCredits(text, s, d) { ArkS.t = ArkS.T.ZeroSanity; true }
        ArkS.T.ZeroSanity -> arkZeroSanity(text, s, d) { ArkS.t = ArkS.T.Missions; true }
        ArkS.T.Missions -> arkMissions(text, s, d) { ArkS.t = ArkS.T.Home; onComplete() }
    }
}

suspend fun AutoService.arkStartup(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
}

suspend fun AutoService.arkHome(
    s: StateCheckUtils,
    d: DispatchUtils,
    onComplete: () -> Boolean
) {
    if (s.sca("main menu") { onComplete() } ) {}
    else { performGlobalAction(GLOBAL_ACTION_BACK) }
}

suspend fun AutoService.arkRecruit(
    text: Text,
    s: StateCheckUtils,
    d: DispatchUtils,
    onComplete: () -> Boolean
) {
    if (s.sca("main menu") { d.tm("recruit", it) } ) {}
    else if (s.sc("recruit menu")) {
        if (s.sca("recruit slot open") { d.tm("recruit now", it) } ) {}
        else if (s.sca("recruit slot hire") { d.tm("hire", it); delay(1000); true }) {}
        else { onComplete() } }
    else if (s.sca("bag animation") { d.tm("skip", it) } ) {}
    else if (s.sca("operator details") { d.tm("certificate", it) } ) {}
    else if (s.sc("tag menu")) {
        if (s.sca("tag menu top operator") { onComplete() } ) { return }
        else {
            val bestCombo = findBestTagCombo(text)
            if (bestCombo != null) {
                for (tag in bestCombo) { d.t(tag) } // select tags
                d.p(Point(900, 450)) // 9 hours
                d.p(Point(1675, 875)) // confirm
                delay(1000) }
            else {
                if (s.sc("refresh available")) {
                    d.t("tap to refresh"); delay(500);
                    d.p(Point(1600, 750)) // confirm refresh
                    delay(1000)}
                else {
                    d.p(Point(900, 450)) // 9 hours
                    d.p(Point(1675, 875)) // confirm
                    delay(1000) } } } }
    else { arkHome(s, d) { true } }
}

suspend fun AutoService.arkCredits(
    text: Text,
    s: StateCheckUtils,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (s.sca("main menu") { d.tm("store", it) } ) {}
    else if (s.sc("store bar")) {
        if (!s.sc("credit menu")) { d.tm("credit store", "credit menu") }
        else {
            if (s.sca("credit available") { d.tm("claim", it) } ) {}
            else { onComplete() } } }
    else { arkHome(s, d) {true} }
}

// location checks
// action based upon location

suspend fun AutoService.arkZeroSanity(
    text: Text,
    s: StateCheckUtils,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (s.sca("main menu") { d.tm("sanity", it) } ) {}
    else if (s.sca("terminal") { d.tm("to the most recent stage", it) } ) {}
    else if (s.sca("stage select") { d.tm("start", it) } ) {}
    else if (s.sca("battle prep") { d.tm("start", it) } ) {}
    else if (s.sc("battle pending")) { coroutineScope.launch { nap(15 * 1000) } }
    else if (s.sca("battle finished") { d.tm("results", it) } ) {}
    else if (s.sc("zero sanity")) { onComplete() }
    else { arkHome(s, d) { true } }
}

suspend fun AutoService.arkMissions(
    text: Text,
    s: StateCheckUtils,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (s.sca("main menu") { d.tm("missions", it) } ) {}
    else if (s.sc("missions menu")) {
        d.pm(Point(1900, 200), "missions menu"); delay(2000)
        onComplete() }
    else { arkHome(s, d) { true } }
}

fun AutoService.arkLaunch() {
    // adb shell 'dumpsys package | grep -Eo "^[[:space:]]+[0-9a-f]+[[:space:]]+com.YoStarEN.Arknights/[^[:space:]]+" | grep -oE "[^[:space:]]+$"'
    // adb shell "am start -n com.YoStarEN.Arknights/com.u8.sdk.U8UnityContext"

    val launchIntent = Intent()
        .setComponent(ComponentName("com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

    try { startActivity(launchIntent) }
    catch (e: Exception) { throw Exception(e) }
}
