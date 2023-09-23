package com.magicalhag.autohag.auto.games.arknights.misc

import android.content.ComponentName
import android.content.Intent
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.utils.logging.log

fun AutoService.arknightsLaunch() {
    val launchIntent = Intent()
        .setComponent(ComponentName("com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

    try {
        startActivity(launchIntent)
        log("Arknights Opened")
    } catch (e: Exception) {
        log("Arknights Failed to Open")
    }
}
