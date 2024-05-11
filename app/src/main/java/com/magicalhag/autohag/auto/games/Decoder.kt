package com.magicalhag.autohag.auto.games

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.ark.ark
import com.magicalhag.autohag.auto.games.arknights.arknights
import com.magicalhag.autohag.auto.games.e7.e7
import com.magicalhag.autohag.auto.games.e7.e7Launch

object State {
    enum class G {
        Arknights,
        // EpicSeven,
    }

    var g: G = G.Arknights
    // var currentGame: Game = Game.EpicSeven

}

suspend fun AutoService.decoder(text: Text) {

    when(State.g) {
        State.G.Arknights -> ark(text) { coma(); true }
        // State.G.Arknights -> ark(text) { State.g = State.G.EpicSeven; e7Launch() }
        // State.G.EpicSeven -> e7(text) { State.g = State.G.Arknights; coma(); performGlobalAction(GLOBAL_ACTION_HOME) }
    }
}