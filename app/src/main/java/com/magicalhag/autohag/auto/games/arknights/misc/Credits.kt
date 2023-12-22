package com.magicalhag.autohag.auto.games.arknights.misc

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.buildClick
import com.magicalhag.autohag.auto.core.dispatch.dispatch
import com.magicalhag.autohag.auto.core.text.check
import com.magicalhag.autohag.auto.core.text.find

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