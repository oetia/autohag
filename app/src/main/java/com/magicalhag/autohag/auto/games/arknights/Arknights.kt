package com.magicalhag.autohag.auto.games.arknights

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.battle.arknightsZeroSanity
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsRecruit
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsStartup


// have some kind of intermediate management function that tracks completion

suspend fun AutoService.arknights(ocrout: Text) {
    when (state) {
        // "ALL"
        "STRTUP" -> arknightsStartup(ocrout)
        "HOME" -> arknightsHome(ocrout)
        "RECR" -> arknightsRecruit(ocrout)
        "0SANITY" -> arknightsZeroSanity(ocrout)
        // "B4SE"
    }
}




