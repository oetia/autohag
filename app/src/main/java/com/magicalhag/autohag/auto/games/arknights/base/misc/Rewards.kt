package com.magicalhag.autohag.auto.games.arknights.base.misc

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.text.find

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