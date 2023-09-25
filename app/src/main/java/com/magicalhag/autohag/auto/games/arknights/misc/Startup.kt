package com.magicalhag.autohag.auto.games.arknights.misc

import android.graphics.Point
import android.media.MediaPlayer.OnCompletionListener
import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.utils.dispatch.buildClick
import com.magicalhag.autohag.auto.utils.text.check
import com.magicalhag.autohag.auto.utils.dispatch.dispatch
import com.magicalhag.autohag.auto.utils.text.find

suspend fun AutoService.arknightsStartup(ocrout: Text, onCompletion: () -> Unit) {
    if (ocrout.check("start", "check preannounce", "account management", "customer service")) {
        dispatch(ocrout.find("start").buildClick())
    // } else if (ocrout.check("terra")) {

    } else if(ocrout.check("resource today")) {
        dispatch(ocrout.find("resource today").buildClick())
    } else if (ocrout.check("daily supply")) {
        dispatch(Point(2075, 90).buildClick())
    } else if (ocrout.check("event", "system")) { // figure out how to get this right
        dispatch(Point(2075, 90).buildClick())
    } else if (ocrout.check("friends", "archives", "squads", "operator")) {
        onCompletion()
    } else {
        dispatch(Point(500, 500).buildClick())
    }
}
