package com.magicalhag.autohag.auto.games.arknights.base.misc

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.text.find

suspend fun AutoService.arknightsCredits(text: Text, onComplete: () -> Unit) {
    if (text.check("friends", "archives", "store")) {
        dispatch(text.find("store").buildClick())
    } else if (text.check("credit store")) {
        if(text.check("operator progress", "credit rules")) {
            if (text.check("claim")) {
                dispatch(text.find("claim").buildClick())
            }
            else if(text.check("collected")) {
                onComplete()
            }
        } else {
            dispatch(text.find("credit store").buildClick())
        }
    } else {
        arknightsHome(text) {}
    }
}