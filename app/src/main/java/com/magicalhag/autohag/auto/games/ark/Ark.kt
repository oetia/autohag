package com.magicalhag.autohag.auto.games.ark

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK
import android.content.ComponentName
import android.content.Intent
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.DispatchUtils
import com.magicalhag.autohag.auto.core.dispatch.generateDispatchUtils
import com.magicalhag.autohag.auto.games.ark.misc.findBestTagCombo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// @formatter:off
suspend fun AutoService.ark(text: Text, onComplete: () -> Unit) {
    val d = generateDispatchUtils(text)
    when (ArkS.t) {
        ArkS.T.Startup -> arkStartup(text, d) { ArkS.t = ArkS.T.Home }
        ArkS.T.Home -> arkHome(text) { ArkS.t = ArkS.T.Recruit }
        ArkS.T.Recruit -> arkRecruit(text, d) { ArkS.t = ArkS.T.Credits }
        ArkS.T.Credits -> arkCredits(text, d) { ArkS.t = ArkS.T.ZeroSanity }
        ArkS.T.ZeroSanity -> arkZeroSanity(text, d) { ArkS.t = ArkS.T.Missions }
        ArkS.T.Missions -> arkMissions(text, d) { ArkS.t = ArkS.T.Home; onComplete() }
    }
}

suspend fun AutoService.arkStartup(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
}

suspend fun AutoService.arkHome(
    text: Text,
    onComplete: () -> Unit
) {
    if (ArkUI.mainMenu(text)) { onComplete() }
    else { performGlobalAction(GLOBAL_ACTION_BACK) }
}

suspend fun AutoService.arkRecruit(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (ArkUI.mainMenu(text)) { d.t("recruit") }
    else if (ArkUI.recruit.menu(text)) {
        if (ArkUI.recruit.slotOpen(text)) { d.t("recruit now") }
        else if (ArkUI.recruit.slotHire(text)) { d.t("hire"); delay(1000) }
        else { onComplete() } }
    else if (ArkUI.recruit.tagMenu(text)) {
        if (ArkUI.recruit.tagMenuTopOperator(text)) { onComplete(); return }
        else {
            val bestCombo = findBestTagCombo(text)
            if (bestCombo != null) {
                for (tag in bestCombo) { d.t(tag) } // select tags
                d.p(Point(900, 450)) // 9 hours
                d.p(Point(1675, 875)) // confirm
                delay(1000) }
            else {
                if (ArkUI.recruit.refreshAvailable(text)) {
                    d.t("tap to refresh"); delay(500);
                    d.p(Point(1600, 750)) // confirm refresh
                    delay(1000)}
                else {
                    d.p(Point(900, 450)) // 9 hours
                    d.p(Point(1675, 875)) // confirm
                    delay(1000) } } } }
    else if (ArkUI.recruit.bagAnimation(text)) { d.t("skip") }
    else if (ArkUI.recruit.operatorDetails(text)) { d.t("certificate") }
    else { arkHome(text) {} }

    // } else if (text.check("job", "tags")) {
    //     val bestTagCombo = findBestTagCombo()
    //     if(bestTagCombo != null) {
    //         selectTags(bestTagCombo)
    //         confirmRecruitment()
    //     } else {
    //         if(text.check("tap to refresh")) {
    //             dispatch(text.find("tap to refresh").buildClick())
    //         } else {
    //             confirmRecruitment()
    //         }
    //     }
    // } else if (text.check("skip")) {
    //     dispatch(text.find("skip").buildClick())
    // } else if (text.check("certificate")) {
    //     dispatch(text.find("certificate").buildClick())
    // } else if (text.check("spend 1 refresh attempt?")) {
    //     dispatch(Point(1600, 750).buildClick())
    //     delay(1000)
    // } else {
    //     arknightsHome(text) {}
    // }
}

suspend fun AutoService.arkCredits(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (ArkUI.mainMenu(text)) { d.t("store") }
    else if (ArkUI.storeBar(text)) {
        if (!ArkUI.creditMenu(text)) { d.t("credit store") }
        else {
            if (ArkUI.creditAvailable(text)) { d.t("claim") }
            else { onComplete() } } }
    else { arkHome(text) {} }
}

suspend fun AutoService.arkZeroSanity(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (ArkUI.mainMenu(text)) { d.t("sanity/") }
    else if (ArkUI.terminal(text)) { d.t("to the most recent stage") }
    else if (ArkUI.stageSelect(text)) { d.t("start") }
    else if (ArkUI.battlePrep(text)) { d.t("start") }
    else if (ArkUI.battlePending(text)) { coroutineScope.launch { nap(15 * 1000) } }
    else if (ArkUI.battleFinished(text)) { d.t("results") }
    else if (ArkUI.zeroSanity(text)) { onComplete() }
    else { arkHome(text) {} }
}

suspend fun AutoService.arkMissions(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (ArkUI.mainMenu(text)) { d.t("missions") }
    else if (ArkUI.missionsMenu(text)) {
        d.p(Point(1900, 200)); delay(2000)
        onComplete() }
    else { arkHome(text) {} }
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
