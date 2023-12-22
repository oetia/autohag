package com.magicalhag.autohag.auto.games.e7

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.buildClick
import com.magicalhag.autohag.auto.core.dispatch.buildElementClick
import com.magicalhag.autohag.auto.core.dispatch.buildSwipe
import com.magicalhag.autohag.auto.core.dispatch.dispatch
import com.magicalhag.autohag.auto.core.logging.log
import com.magicalhag.autohag.auto.core.text.check
import com.magicalhag.autohag.auto.core.text.find
import com.magicalhag.autohag.auto.core.text.findElements
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// just keep it simple for now
// will use the same methods as everyone else
// if in the future i need to consider using different methods, then so be it

// if there's more than one possible decision on a screen, branch into separate
// feels so ugly tbh. goes against my abstraction ideas... but i guess it'll work.

// i need a better way
// checking for a specific screen
// calculate all in advance from image? a lot of unnecessary calculations

// @formatter:off

suspend fun AutoService.e7(text: Text) {

    suspend fun dt(t: String) = dispatch(text.find(t).last().buildClick())
    suspend fun dte(t: String) = dispatch(text.findElements(t).buildElementClick())
    suspend fun dp(p: Point) = dispatch(p.buildClick())
    suspend fun ds(p1: Point, p2: Point) = dispatch(buildSwipe(p1, p2))

    when (E7S.t) {
        E7S.T.Startup -> e7Startup(text, ::dt, ::dp) {}
        E7S.T.Home -> e7Home(text) { E7S.t = E7S.T.Hunt }
        E7S.T.Hunt -> e7Hunt(text, ::dt, ::dp) { E7S.t = E7S.T.Abyss }
        E7S.T.Abyss -> e7Abyss(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.SanctuaryHeart }
        E7S.T.SanctuaryHeart -> e7SanctuaryHeart(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.SanctuaryForestPenguin }
        E7S.T.SanctuaryForestPenguin -> e7SanctuaryForest(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.SanctuaryForestSpirit }
        E7S.T.SanctuaryForestSpirit -> e7SanctuaryForest(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.SanctuaryForestMola }
        E7S.T.SanctuaryForestMola -> e7SanctuaryForest(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.Arena }
        E7S.T.Arena -> e7Arena(text, ::dt, ::dp, ::dte, ::ds) { E7S.t = E7S.T.Reputation }
        E7S.T.Reputation -> e7Reputation(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.Home; coma() }
    }
    // when (E7S.t) {
    //     E7S.T.Startup -> e7Startup(text, ::dt, ::dp) {}
    //     E7S.T.Home -> e7Home(text) {}
    //     E7S.T.Hunt -> e7Hunt(text, ::dt, ::dp) { coma() }
    //     E7S.T.Abyss -> e7Abyss(text, ::dt, ::dp, ::dte) { coma() }
    //     E7S.T.SanctuaryHeart -> e7SanctuaryHeart(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.SanctuaryForestPenguin }
    //     E7S.T.SanctuaryForestPenguin -> e7SanctuaryForest(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.SanctuaryForestSpirit }
    //     E7S.T.SanctuaryForestSpirit -> e7SanctuaryForest(text, ::dt, ::dp, ::dte) { E7S.t = E7S.T.SanctuaryForestMola }
    //     E7S.T.SanctuaryForestMola -> e7SanctuaryForest(text, ::dt, ::dp, ::dte) { coma() }
    //     E7S.T.Arena -> e7Arena(text, ::dt, ::dp, ::dte, ::ds) { coma() }
    // }
}

suspend fun AutoService.e7Startup(
    text: Text,
    dt: suspend (t: String) -> Boolean,
    dp: suspend (p:Point) -> Boolean,
    onComplete: () -> Unit
) {}

suspend fun AutoService.e7Home(text: Text, onComplete: () -> Unit) {
    if(E7UI.mainMenu(text)) {
        onComplete()
    } else {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
}

suspend fun AutoService.e7Hunt(
    text: Text,
    dt: suspend (t: String) -> Boolean,
    dp: suspend (p:Point) -> Boolean,
    onComplete: () -> Unit
) {
    if (E7UI.quitPopup(text)) { dt("cancel") }
    else if (E7UI.mainMenu(text)) { dt("battle") }
    else if (E7UI.battleMenu(text)) { dt("hunt") }
    else if (E7UI.huntMenu(text)) { dt("banshee hunt") }
    else if (E7UI.bansheeMenu(text)) { dt("select team") }
    else if (E7UI.selectTeamMenu(text)) { dt("start"); delay(500) }
    else if (E7UI.repeatBattlingModal(text)) { dp(Point(1350, 300)) }
    else if (E7UI.backgroundBattlingPopup(text)) { dt("confirm"); onComplete() }
    else if (E7UI.insufficientEnergyPopup(text)) { onComplete() }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7Abyss(
    text: Text,
    dt: suspend (t: String) -> Boolean,
    dp: suspend (p:Point) -> Boolean,
    dte: suspend (t: String) -> Boolean,
    onComplete: () -> Unit
) {
    if (E7UI.quitPopup(text)) { dt("cancel") }
    else if (E7UI.mainMenu(text)) { dt("battle") }
    else if (E7UI.battleMenu(text)) { dt("abyss"); delay(500) }
    else if (E7UI.purifyPopup(text)) { dt("confirm") }
    else if (E7UI.purifyReward(text)) { dt("tap to close") }
    else if (E7UI.insufficientAbyssEntryTickets(text)) { onComplete() }
    else if (E7UI.abyssMenu(text)) { dt("purify") }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7SanctuaryHeart(
    text: Text,
    dt: suspend (t: String) -> Boolean,
    dp: suspend (p:Point) -> Boolean,
    dte: suspend (t: String) -> Boolean,
    onComplete: () -> Unit
) {
    if (E7UI.quitPopup(text)) { dt("cancel") }
    else if (E7UI.mainMenu(text)) { dte("sanctuary"); delay(500) }
    else if (E7UI.sanctuaryMenu(text)) { dt("heart of orbis") }
    else if (E7UI.heart.menu(text)) {
        if (!E7UI.heart.rewardsCDText(text)) { dt("receive reward(?!s)") }
        else { onComplete() } }
    else if (E7UI.heart.rewardsPopup(text)) { dt("tap to close") }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7SanctuaryForest(
    text: Text,
    dt: suspend (t: String) -> Boolean,
    dp: suspend (p:Point) -> Boolean,
    dte: suspend (t: String) -> Boolean,
    onComplete: () -> Unit
) {
    if (E7UI.quitPopup(text)) { dt("cancel") }
    else if (E7UI.mainMenu(text)) { dte("sanctuary"); delay(500) }
    else if (E7UI.sanctuaryMenu(text)) { dt("forest of souls"); delay(500) }
    else if (E7UI.forest.menu(text)) {
        if (E7S.t == E7S.T.SanctuaryForestPenguin) { dt("penguin nest") }
        else if (E7S.t == E7S.T.SanctuaryForestSpirit) { dt("spirit well") }
        else if (E7S.t == E7S.T.SanctuaryForestMola) { dt("molagora farm") } }
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
    dt: suspend (t: String) -> Boolean,
    dp: suspend (p:Point) -> Boolean,
    dte: suspend (t: String) -> Boolean,
    ds: suspend (p1: Point, p2: Point) -> Boolean,
    onComplete: () -> Unit
) {

    if (E7UI.mainMenu(text)) { dt("arena") }
    else if (E7UI.arena.selectMenu(text)) { dt("defeat your competitors"); delay(500) }
    else if (E7UI.arena.menu(text)) {
        if (!E7UI.arena.npcMenu(text)) { dt("npc challenge") }
        else if (E7UI.arena.fightButton(text)) { dt("fight") }
        else {
            if (!E7UI.arena.npcCorvus(text)) { ds(Point(1600, 1000), Point(1600, 50)) }
            else { onComplete() } } }
    else if (E7UI.arena.purchaseFlags(text)) { onComplete() }
    else if (E7UI.arena.fightPrep(text)) { dt("start") }
    else if (E7UI.arena.npcDialogue(text)) { dt("skip") }
    else if (E7UI.arena.fightPause(text)) { dt("return to game") }
    else if (E7UI.arena.fightStart(text)) { dp(Point(2110, 50)); coroutineScope.launch { nap(60 * 1000) } }
    else if (E7UI.arena.fightEnd(text)) { dt("confirm") }
    else { e7Home(text) {} }
}

suspend fun AutoService.e7Reputation(
    text: Text,
    dt: suspend (t: String) -> Boolean,
    dp: suspend (p:Point) -> Boolean,
    dte: suspend (t: String) -> Boolean,
    onComplete: () -> Unit
) {
    if (E7UI.mainMenu(text)) { dte("reputation") }
    else if (E7UI.reputation.menu(text)) {
        if (!E7UI.reputation.rewardsReceived(text)) { dp(Point(1795, 275)) }
        else { onComplete() } }
    else if (E7UI.reputation.rewardsPopup(text)) { onComplete() }
    else { e7Home(text) {} }
}

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