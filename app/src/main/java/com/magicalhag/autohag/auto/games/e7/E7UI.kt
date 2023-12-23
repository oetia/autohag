package com.magicalhag.autohag.auto.games.e7

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.core.logging.log
import com.magicalhag.autohag.auto.core.text.check

object E7UI {

    private fun wrapper(name: String, text: Text, checks: Array<String>): Boolean {
        val result = text.check(*checks)
        log("E7SC \n$name: $result")
        return result
    }

    fun tapAgainToClose(text: Text): Boolean =
        wrapper("Tap Again to Close", text, arrayOf("tap again to close the game"))
    fun startScreen(text: Text): Boolean =
        wrapper("Start Screen", text, arrayOf("all rights reserved"))

    fun startScreenButtons(text: Text): Boolean =
        wrapper("Start Screen Buttons", text, arrayOf("log out", "global server"))

    fun mainMenuPartial(text: Text): Boolean =
        wrapper("Main Menu Partial", text, arrayOf("shop", "hero"))

    fun quitPopup(text: Text): Boolean =
        wrapper("Quit Popup", text, arrayOf("quit the game", "cancel", "confirm"))

    fun mainMenu(text: Text): Boolean =
        wrapper("Main Menu", text, arrayOf("shop", "hero", "summon", "reputation", "pet house", "guild", "sanctuary", "arena", "battle"))

    fun battleMenu(text: Text): Boolean =
        wrapper("Battle Menu", text, arrayOf("labyrinth", "spirit altar", "hunt", "abyss", "swipe to move pages"))

    fun huntMenu(text: Text): Boolean =
        wrapper("Hunt Menu", text, arrayOf("wyvern hunt", "golem hunt", "banshee hunt"))

    fun bansheeMenu(text: Text): Boolean =
        wrapper("Banshee Menu", text, arrayOf("banshee hunt", "statistics", "select team"))

    fun selectTeamMenu(text: Text): Boolean =
        wrapper("Select Team Menu", text, arrayOf("team cp", "start"))

    fun repeatBattlingModal(text: Text): Boolean =
        wrapper("Repeat Battle Modal", text, arrayOf("repeat (?:battle|battling)"))

    fun backgroundBattlingPopup(text: Text): Boolean =
        wrapper("Background Battling Popup", text, arrayOf("background battling", "cancel", "confirm"))

    fun insufficientEnergyPopup(text: Text): Boolean =
        wrapper("Insufficient Energy Popup", text, arrayOf("insufficient energy"))

    fun sanctuaryMenu(text: Text): Boolean =
        wrapper("Sanctuary Menu", text, arrayOf("heart of orbis", "forest of souls"))

    fun abyssMenu(text: Text): Boolean =
        wrapper("Abyss Menu", text, arrayOf("abyss", "purify"))

    fun purifyPopup(text: Text): Boolean =
        wrapper("Purify Popup", text, arrayOf("purify", "abyss guide", "cancel", "confirm"))

    fun purifyReward(text: Text): Boolean =
        wrapper("Purify Reward", text, arrayOf("receive reward", "purification complete", "tap to close"))

    fun insufficientAbyssEntryTickets(text: Text): Boolean =
        wrapper("Insufficient Abyss Entry Tickets", text, arrayOf("insufficient abyss entry tickets"))
    object heart {
        fun menu(text: Text): Boolean =
            wrapper("Heart Menu", text, arrayOf("improve building", "receive reward(?!s)"))

        fun rewardsPopup(text: Text) =
            wrapper("Heart Rewards Received Popup", text, arrayOf("received", "tap to close"))

        fun rewardsCDText(text: Text) =
            wrapper("Heart Rewards Cooldown Text", text, arrayOf("time left until receiving"))
    }

    object forest {
        fun menu(text: Text): Boolean =
            wrapper("Forest Menu", text, arrayOf("penguin nest", "spirit well", "molagora farm"))

        fun penguinCDText(text: Text) =
            wrapper("Penguin Nest Cooldown Text", text, arrayOf("time left until harvest", "the nest is a place"))

        fun spiritCDText(text: Text) =
            wrapper("Spirit Well Cooldown Text", text, arrayOf("time left until harvest", "the spirit well is a place"))

        fun molaCDText(text: Text) =
            wrapper("Molagora Farm Cooldown Text", text, arrayOf("time left until harvest", "on the molagora farm"))
    }

    object arena {
        fun selectMenu(text: Text): Boolean =
            wrapper("Arena Select Menu", text, arrayOf("arena", "world arena", "defeat your competitors"))

        fun menu(text: Text): Boolean =
            wrapper("Arena Menu", text, arrayOf("defense team", "arena info"))

        fun npcMenu(text: Text): Boolean =
            wrapper("Arena NPC Menu", text, arrayOf("npc challenge", "difficulty", "medium", "hard", "hell"))

        fun npcCorvus(text: Text): Boolean =
            wrapper("Arena NPC Corvus", text, arrayOf("corvus"))
        fun fightButton(text: Text): Boolean =
            wrapper("Arena Fight Button", text, arrayOf("fight"))

        fun fightPrep(text: Text): Boolean =
            wrapper("Arena Fight Prep", text, arrayOf("guardians and pets", "repeat reward", "start"))

        fun purchaseFlags(text: Text): Boolean =
            wrapper("Arena Purchase Flags", text, arrayOf("purchase flags", "cancel", "buy"))

        fun npcDialogue(text: Text): Boolean =
            wrapper("Arena NPC Dialogue", text, arrayOf("skip"))

        fun fightPause(text: Text): Boolean =
            wrapper("Arena Fight Pause", text, arrayOf("pause", "yield", "return to game"))
        fun fightStart(text: Text): Boolean =
            wrapper("Arena Fight Start", text, arrayOf("vivian"))

        fun fightEnd(text: Text): Boolean =
            wrapper("Arena Fight End", text, arrayOf("battle results", "confirm"))

    }

    object reputation {
        fun menu(text: Text): Boolean =
            wrapper("Reputation Menu", text, arrayOf("daily reputation", "weekly reputation"))

        fun rewardsPopup(text: Text): Boolean =
            wrapper("Reputation Rewards Popup", text, arrayOf("reputation point rewards", "tap to close"))

        fun rewardsReceived(text: Text): Boolean =
            wrapper("Reputation Rewards Received", text, arrayOf("you have already received all"))

    }

    fun backgroundBattlingModal(text: Text): Boolean =
        wrapper("Background Battling Modal", text, arrayOf("background battling", "hunt stage"))

    fun backgroundBattlingDone(text: Text): Boolean =
        wrapper("Background Battling Done", text, arrayOf("background battling ended", "repeat battling has ended"))

    fun stageClear(text: Text): Boolean =
        wrapper("Stage Clear", text, arrayOf("stage", "clear"))

    fun battleResults(text: Text): Boolean =
        wrapper("Battle Results", text, arrayOf("battle results", "confirm"))

    fun unitsIdling(text: Text): Boolean =
        wrapper("Units Idling", text, arrayOf("leave", "lobby", "try again"))
}