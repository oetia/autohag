package com.magicalhag.autohag.auto.games.arknights.base.misc

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsHome
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.text.find

suspend fun AutoService.arknightsBaseHome(text: Text, onCompletion: () -> Unit) {
    if (text.check("overview", "building mode")) {
        onCompletion()
    } else if (text.check("friends", "archives", "base")) {
        dispatch(text.find("base").buildClick())
    } else {
        arknightsHome(text) {}
    }
}