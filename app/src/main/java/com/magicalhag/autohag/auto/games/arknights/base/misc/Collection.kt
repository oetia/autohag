package com.magicalhag.autohag.auto.games.arknights.base.misc

import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.games.arknights.ArknightsState
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.find


suspend fun AutoService.arknightsBaseCollection(
    text: Text,
    emergency: Boolean,
    onEmergency: () -> Unit,
    onCompletion: () -> Unit,
) {
    if (text.check("overview", "building mode")) {
        if (!emergency) {
            dispatch(Point(2225, 125).buildClick())
        } else {
            dispatch(Point(2225, 225).buildClick())
        }
    } else if (text.check("backlog")) {
        if (text.check("collectable")) {
            dispatch(text.find("collectable").buildClick())
        } else if (text.check("orders acquired")) {
            dispatch(text.find("orders acquired").buildClick())
        } else if (text.check("trust")) {
            if (text.check("max trust")) {
                dispatch(text.find("backlog").buildClick())
                onCompletion()
            } else {
                dispatch(text.find("trust").buildClick())
            }
        } else if (text.check("clues")) {
            dispatch(text.find("backlog").buildClick())
            onCompletion()
        } else {
            dispatch(text.find("backlog").buildClick())
            onEmergency()
        }
    } else {
        arknightsBaseHome(text) {}
    }
}