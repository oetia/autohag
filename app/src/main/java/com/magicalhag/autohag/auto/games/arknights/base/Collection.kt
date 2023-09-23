package com.magicalhag.autohag.auto.games.arknights.base

import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.find


var baseCollectionDone = false
var baseCollectionHasEmergency = false

suspend fun AutoService.arknightsBaseCollection(ocrout: Text) {
    if(ocrout.check("overview", "building mode")) {
        if(!baseCollectionHasEmergency) {
            dispatch(Point(2225, 125).buildClick())
        } else {
            dispatch(Point(2225, 225).buildClick())
        }
    } else if(ocrout.check("backlog")) {
        if(ocrout.check("collectable")) {
            dispatch(ocrout.find("collectable").buildClick())
        } else if(ocrout.check("orders acquired")) {
            dispatch(ocrout.find("orders acquired").buildClick())
        } else if(ocrout.check("trust")) {
            if(ocrout.check("max trust")) {
                dispatch(ocrout.find("backlog").buildClick())
                baseCollectionDone = true
            } else {
                dispatch(ocrout.find("trust").buildClick())
            }
        } else if(ocrout.check("clues")) {
            dispatch(ocrout.find("backlog").buildClick())
            baseCollectionDone = true
        } else {
            dispatch(ocrout.find("backlog").buildClick())
            baseCollectionHasEmergency = true
        }
    }
}
