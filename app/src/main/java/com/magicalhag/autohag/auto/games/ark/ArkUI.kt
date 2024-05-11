package com.magicalhag.autohag.auto.games.ark

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.core.logging.log
import com.magicalhag.autohag.auto.core.text.check

object ArkUI {
    private fun wrapper(name: String, text: Text, checks: Array<String>): Boolean {
        val result = text.check(*checks)
        log("ARKSC \n$name: $result")
        return result
    }


    fun startMenu(text: Text): Boolean =
        wrapper("Start Menu", text, arrayOf("start", "check preannounce", "account management", "customer service"))

    fun mainMenu(text: Text): Boolean =
        wrapper("Main Menu", text, arrayOf("friends", "archive", "recruit", "store", "missions", "base", "sanity"))

    fun terminal(text: Text): Boolean =
        wrapper("Terminal", text, arrayOf("to the most recent stage"))

    fun stageSelect(text: Text): Boolean =
        wrapper("Stage Select", text, arrayOf("auto deploy", "start"))

    fun autoCountSelect(text: Text): Boolean =
        wrapper("Auto Count Select", text, arrayOf("auto deploy", "start", "1", "2", "3", "4", "5", "6"))

    fun battlePrep(text: Text): Boolean =
        wrapper("Battle Prep", text, arrayOf("mission|operation", "start", "the roster for this operation cannot be changed"))

    fun battlePending(text: Text): Boolean =
        wrapper("Battle Pending", text, arrayOf("2x", "takeover"))

    fun battleFinished(text: Text): Boolean =
        wrapper("Battle Finished", text, arrayOf("mission", "results"))

    fun zeroSanity(text: Text): Boolean =
        wrapper("Zero Sanity", text, arrayOf("restore"))

    object recruit {
        fun menu(text: Text): Boolean =
            wrapper("Recruit Menu", text, arrayOf("recruit(?!ment)", "1", "2", "3", "4"))

        fun slotOpen(text: Text): Boolean =
            wrapper("Recruit Slot Open", text, arrayOf("recruit now"))
        fun slotHire(text: Text): Boolean =
            wrapper("Recruit Found", text, arrayOf("hire"))

        fun tagMenu(text: Text): Boolean =
            wrapper("Tag Menu", text, arrayOf("job", "tags"))

        fun tagMenuTopOperator(text: Text): Boolean =
            wrapper("Tag Menu Top Operator", text, arrayOf("top operator"))

        fun bagAnimation(text: Text): Boolean =
            wrapper("Skip Animation", text, arrayOf("skip"))

        fun operatorDetails(text: Text): Boolean =
            wrapper("Operator Details", text, arrayOf("certificate"))

        fun refreshAvailable(text: Text): Boolean =
            wrapper("Refresh Available", text, arrayOf("tap to refresh"))

        fun refreshConfirm(text: Text): Boolean =
            wrapper("Refresh Confirm", text, arrayOf("spend 1 refresh attempt?"))


    }



    fun storeBar(text: Text): Boolean =
        wrapper("Store Bar", text, arrayOf("credit store"))

    fun creditMenu(text: Text): Boolean =
        wrapper("Credit Menu", text, arrayOf("operator progress", "credit rules"))

    fun creditAvailable(text: Text): Boolean =
        wrapper("Credit Available", text, arrayOf("claim"))

    fun creditCollected(text: Text): Boolean =
        wrapper("Credit Collected", text, arrayOf("collected"))


    fun missionsMenu(text: Text): Boolean =
        wrapper("Mission Menu", text, arrayOf("weekly missions"))

    fun collectAll(text: Text): Boolean =
        wrapper("Collect All", text, arrayOf("collect all"))

}