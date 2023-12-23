package com.magicalhag.autohag.auto.games.e7

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.DispatchUtils
import com.magicalhag.autohag.auto.core.dispatch.generateDispatchUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// can have conditional branches and pass in two oncompletes
// check for the tap again to close text

// @formatter:off

suspend fun AutoService.e7(text: Text, onComplete: () -> Unit) {
    val d = generateDispatchUtils(text)
    when (E7S.t) {
        E7S.T.Startup -> e7Startup(text, d) { E7S.t = E7S.T.Home }
        E7S.T.Home -> e7Home(text) { if (it) E7S.t = E7S.T.Startup else E7S.t = E7S.T.Hunt }
        E7S.T.Hunt -> e7Hunt(text, d) { E7S.t = E7S.T.Abyss }
        E7S.T.Abyss -> e7Abyss(text, d) { E7S.t = E7S.T.SanctuaryHeart }
        E7S.T.SanctuaryHeart -> e7SanctuaryHeart(text, d) { E7S.t = E7S.T.SanctuaryForestPenguin }
        E7S.T.SanctuaryForestPenguin -> e7SanctuaryForest(text, d) { E7S.t = E7S.T.SanctuaryForestSpirit }
        E7S.T.SanctuaryForestSpirit -> e7SanctuaryForest(text, d) { E7S.t = E7S.T.SanctuaryForestMola }
        E7S.T.SanctuaryForestMola -> e7SanctuaryForest(text, d) { E7S.t = E7S.T.Arena }
        E7S.T.Arena -> e7Arena(text, d) { E7S.t = E7S.T.Reputation }
        E7S.T.Reputation -> e7Reputation(text, d) { E7S.t = E7S.T.Home; onComplete() }
    }
}

suspend fun AutoService.e7Startup(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (E7UI.mainMenuPartial(text)) { onComplete() }
    else if (E7UI.startScreen(text)) { d.p(Point(1250, 1050)) }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7Home(
    text: Text,
    onComplete: (restartNeeded: Boolean) -> Unit
) {
    if (E7UI.mainMenu(text)) { onComplete(false) }
    else if (E7UI.tapAgainToClose(text)) { onComplete(true) }
    else { performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK) }
}

suspend fun AutoService.e7Hunt(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (E7UI.quitPopup(text)) { d.t("cancel") }
    else if (E7UI.mainMenu(text)) { d.t("battle") }
    else if (E7UI.battleMenu(text)) { d.t("hunt") }
    else if (E7UI.huntMenu(text)) { d.t("banshee hunt") }
    else if (E7UI.bansheeMenu(text)) { d.t("select team") }
    else if (E7UI.selectTeamMenu(text)) { d.t("start"); delay(500) }
    else if (E7UI.repeatBattlingModal(text)) { d.p(Point(1350, 300)) }
    else if (E7UI.backgroundBattlingPopup(text)) { d.t("confirm"); onComplete() }
    else if (E7UI.insufficientEnergyPopup(text)) { onComplete() }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7Abyss(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (E7UI.quitPopup(text)) { d.t("cancel") }
    else if (E7UI.mainMenu(text)) { d.t("battle") }
    else if (E7UI.battleMenu(text)) { d.t("abyss"); delay(500) }
    else if (E7UI.purifyPopup(text)) { d.t("confirm") }
    else if (E7UI.purifyReward(text)) { d.t("tap to close") }
    else if (E7UI.insufficientAbyssEntryTickets(text)) { onComplete() }
    else if (E7UI.abyssMenu(text)) { d.t("purify") }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7SanctuaryHeart(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (E7UI.quitPopup(text)) { d.t("cancel") }
    else if (E7UI.mainMenu(text)) { d.te("sanctuary"); delay(500) }
    else if (E7UI.sanctuaryMenu(text)) { d.t("heart of orbis") }
    else if (E7UI.heart.menu(text)) {
        if (!E7UI.heart.rewardsCDText(text)) { d.t("receive reward(?!s)") }
        else { onComplete() } }
    else if (E7UI.heart.rewardsPopup(text)) { d.t("tap to close") }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7SanctuaryForest(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (E7UI.quitPopup(text)) { d.t("cancel") }
    else if (E7UI.mainMenu(text)) { d.te("sanctuary"); delay(500) }
    else if (E7UI.sanctuaryMenu(text)) { d.t("forest of souls"); delay(500) }
    else if (E7UI.forest.menu(text)) {
        if (E7S.t == E7S.T.SanctuaryForestPenguin) { d.t("penguin nest") }
        else if (E7S.t == E7S.T.SanctuaryForestSpirit) { d.t("spirit well") }
        else if (E7S.t == E7S.T.SanctuaryForestMola) { d.t("molagora farm") } }
    else if (E7UI.forest.penguinCDText(text)) {
        if (E7S.t == E7S.T.SanctuaryForestPenguin) { onComplete() }
        else { e7Home(text) {} } }
    else if (E7UI.forest.spiritCDText(text)) {
        if (E7S.t == E7S.T.SanctuaryForestSpirit) { onComplete() }
        else { e7Home(text) {} } }
    else if (E7UI.forest.molaCDText(text)) {
        if(E7S.t == E7S.T.SanctuaryForestMola) { onComplete() }
        else { e7Home(text) {} } }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7Arena(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (E7UI.mainMenu(text)) { d.t("arena") }
    else if (E7UI.arena.selectMenu(text)) { d.t("defeat your competitors"); delay(500) }
    else if (E7UI.arena.menu(text)) {
        if (!E7UI.arena.npcMenu(text)) { d.t("npc challenge") }
        else if (E7UI.arena.fightButton(text)) { d.t("fight") }
        else {
            if (!E7UI.arena.npcCorvus(text)) { d.s(Point(1600, 1000), Point(1600, 50)) }
            else { onComplete() } } }
    else if (E7UI.arena.purchaseFlags(text)) { onComplete() }
    else if (E7UI.arena.fightPrep(text)) { d.t("start") }
    else if (E7UI.arena.npcDialogue(text)) { d.t("skip") }
    else if (E7UI.arena.fightPause(text)) { d.t("return to game") }
    else if (E7UI.arena.fightStart(text)) { d.p(Point(2110, 50)); coroutineScope.launch { nap(60 * 1000) } }
    else if (E7UI.arena.fightEnd(text)) { d.t("confirm") }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7Reputation(
    text: Text,
    d: DispatchUtils,
    onComplete: () -> Unit
) {
    if (E7UI.mainMenu(text)) { d.te("reputation") }
    else if (E7UI.reputation.menu(text)) {
        if (!E7UI.reputation.rewardsReceived(text)) { d.p(Point(1795, 275)) }
        else { onComplete() } }
    else if (E7UI.reputation.rewardsPopup(text)) { onComplete() }
    else { e7Home(text) {} }
}

fun AutoService.e7Launch() {
    // adb shell 'dumpsys package | grep -Eo "^[[:space:]]+[0-9a-f]+[[:space:]]+com.stove.epic7.google/[^[:space:]]+" | grep -oE "[^[:space:]]+$"'
    // adb shell "am start -n com.stove.epic7.google/kr.supercreative.epic7.AppActivity"

    val launchIntent = Intent()
        .setComponent(ComponentName("com.stove.epic7.google", "kr.supercreative.epic7.AppActivity"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

    try { startActivity(launchIntent) }
    catch (e: Exception) { throw Exception("E7 Launch Failure") }
}