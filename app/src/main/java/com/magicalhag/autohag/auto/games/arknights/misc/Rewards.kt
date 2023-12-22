package com.magicalhag.autohag.auto.games.arknights.misc

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.dispatch.buildClick
import com.magicalhag.autohag.auto.core.dispatch.dispatch
import com.magicalhag.autohag.auto.core.text.check
import com.magicalhag.autohag.auto.core.text.find

suspend fun AutoService.arknightsRewards(text: Text, onComplete: () -> Unit) {
    if (text.check("friends", "archives", "missions")) {
        dispatch(text.find("missions").buildClick())
    } else if (text.check("weekly missions")) {
        if(text.check("collect a")) {
            dispatch(text.find("collect a").buildClick())
        }
        onComplete()
    } else {
        arknightsHome(text) {}
    }
}