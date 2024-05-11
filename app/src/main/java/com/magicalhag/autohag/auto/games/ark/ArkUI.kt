package com.magicalhag.autohag.auto.games.ark

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.core.logging.log
import com.magicalhag.autohag.auto.core.text.check
import java.util.Dictionary

object ArkUI {
    // what were you checking for along with the result. used to build out a dispatch history.
    // i think that i can store this information globally. there's no reason to keep this passed around
    // i'm trying to think if i would ever have to do something else.

    // determining state action pairs. yeah i think this is a good way to describe it. state and action pairs.
    // state action pairs

    private val stateCheckDictionary = hashMapOf<String, Array<String>>(
        "Start Menu" to arrayOf("start", "check preannounce", "account management", "customer service"),
    )

    private fun stateCheck(text: Text, name: String): Boolean {
        val checks = stateCheckDictionary[name] as Array<String>
        return text.check(*checks);
    }

    suspend fun stateCheckAction(text: Text, name: String, callback: suspend () -> Boolean): Boolean {
        return if(stateCheck(text, name)) {
            callback()
        } else {
            false
        }
    }

    data class StateCheckResult(val name: String, val found: Boolean)

    private fun wrapper(name: String, text: Text, checks: Array<String>): StateCheckResult {
        val found = text.check(*checks)
        log("String Check: \n$name: $found")
        return StateCheckResult(name, found)
    }

    fun startMenu(text: Text): StateCheckResult =
        wrapper("Start Menu", text, arrayOf("start", "check preannounce", "account management", "customer service"))

    fun mainMenu(text: Text): StateCheckResult =
        wrapper("Main Menu", text, arrayOf("friends", "archive", "recruit", "store", "missions", "base", "sanity"))

    fun terminal(text: Text): StateCheckResult =
        wrapper("Terminal", text, arrayOf("to the most recent stage"))

    fun stageSelect(text: Text): StateCheckResult =
        wrapper("Stage Select", text, arrayOf("auto deploy", "start"))

    fun autoCountSelect(text: Text): StateCheckResult =
        wrapper("Auto Count Select", text, arrayOf("auto deploy", "start", "1", "2", "3", "4", "5", "6"))

    fun battlePrep(text: Text): StateCheckResult =
        wrapper("Battle Prep", text, arrayOf("mission|operation", "start", "the roster for this operation cannot be changed"))

    fun battlePending(text: Text): StateCheckResult =
        wrapper("Battle Pending", text, arrayOf("2x", "takeover"))

    fun battleFinished(text: Text): StateCheckResult =
        wrapper("Battle Finished", text, arrayOf("mission", "results"))

    fun zeroSanity(text: Text): StateCheckResult =
        wrapper("Zero Sanity", text, arrayOf("restore"))

    object recruit {
        fun menu(text: Text): StateCheckResult =
            wrapper("Recruit Menu", text, arrayOf("recruit(?!ment)", "1", "2", "3", "4"))

        fun slotOpen(text: Text): StateCheckResult =
            wrapper("Recruit Slot Open", text, arrayOf("recruit now"))
        fun slotHire(text: Text): StateCheckResult =
            wrapper("Recruit Found", text, arrayOf("hire"))

        fun tagMenu(text: Text): StateCheckResult =
            wrapper("Tag Menu", text, arrayOf("job", "tags"))

        fun tagMenuTopOperator(text: Text): StateCheckResult =
            wrapper("Tag Menu Top Operator", text, arrayOf("top operator"))

        fun bagAnimation(text: Text): StateCheckResult =
            wrapper("Skip Animation", text, arrayOf("skip"))

        fun operatorDetails(text: Text): StateCheckResult =
            wrapper("Operator Details", text, arrayOf("certificate"))

        fun refreshAvailable(text: Text): StateCheckResult =
            wrapper("Refresh Available", text, arrayOf("tap to refresh"))

        fun refreshConfirm(text: Text): StateCheckResult =
            wrapper("Refresh Confirm", text, arrayOf("spend 1 refresh attempt?"))
    }

    fun storeBar(text: Text): StateCheckResult =
        wrapper("Store Bar", text, arrayOf("credit store"))

    fun creditMenu(text: Text): StateCheckResult =
        wrapper("Credit Menu", text, arrayOf("operator progress", "credit rules"))

    fun creditAvailable(text: Text): StateCheckResult =
        wrapper("Credit Available", text, arrayOf("claim"))

    fun creditCollected(text: Text): StateCheckResult =
        wrapper("Credit Collected", text, arrayOf("collected"))


    fun missionsMenu(text: Text): StateCheckResult =
        wrapper("Mission Menu", text, arrayOf("weekly missions"))

    fun collectAll(text: Text): StateCheckResult =
        wrapper("Collect All", text, arrayOf("collect all"))

}