package com.magicalhag.autohag.auto.games

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log
import com.magicalhag.autohag.auto.games.epicseven.e7

object State {
    enum class Game {
        Arknights, EpicSeven,
    }

    val currentGame: Game = Game.EpicSeven
}

suspend fun AutoService.decoder(text: Text) {

    if(State.currentGame == State.Game.EpicSeven) {
        e7(text)
    } else {
        log("insanity check")
    }
}