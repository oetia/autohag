package com.magicalhag.autohag.auto.games.arknights.base

// suspend fun AutoService.arknightsBaseTradingPosts(ocrout: Text) {
//     if(ocrout.check("overview", "building mode")) {
//         dispatch(ocrout.find("trading post").buildClick())
//     } else if(ocrout.check("facility info", "operator")) {
//         dispatch(Point(600, 900).buildClick())
//     } else if(ocrout.check("facilities")) {
//         for(i in 0..2) {
//             delay(1000)
//             log("FUCK ME IN THE ASSSSS $i")
//             dispatch(Point(125, 325 + i * 125).buildClick())
//             dispatch(Point(425, 1000).buildClick())
//             delay(1000)
//
//             manageFacility()
//         }
//     }
//
//     log("----- \n\n\n\n\n\n\nEND\n\n\n\n\n\n\n------")
// }
//
//
// suspend fun AutoService.manageFacility() {
//     var ocrtemp = getScreenText()
//     deselectIfNecessary(ocrtemp)
//     delay(1000) // so many fucking bugs due to misaligned ocrout text
//     // screenshot failed - 3 - 破案
//     ocrtemp = getScreenText()
//     if(ocrtemp.check("time remaining")) { // operators are selected
//         log("SANITY CHEC")
//         dispatch(ocrtemp.find("confirm").buildClick())
//     } else {
//         delay(1000)
//         // better long term solution would be to have a system on the screenshot end to halt until 1 second since last screenshot has passed
//         // ok. so i got out. then got sent right back in :| reselected ops since they didn't get sent to dorms
//         val availableOps = getAvailableOps()
//         val combo = findBestOpCombo(availableOps)
//         log(combo.joinToString(", "))
//         selectBestOpCombo(combo)
//     }
// }
//
//
// suspend fun AutoService.deselectIfNecessary(ocrout: Text) {
//     var shouldDeselect = false
//     if(ocrout.check("fatigued")) {
//         shouldDeselect = true
//     } else if(ocrout.check("time remaining")) {
//         val timeRemainingBlock = ocrout.find("\\d", Rect(285, 215, 725, 385))
//         val timeRemaining = timeRemainingBlock[0].text.replace(":", "").replace(".", "").replace("-", "").toInt()
//         log(timeRemaining)
//         shouldDeselect = timeRemaining < 240000
//     }
//
//     if(shouldDeselect) {
//         dispatch(ocrout.find("deselect all").buildClick())
//         dispatch(ocrout.find("state", Rect(1530, 0, 2000, 130)).buildClick())
//         dispatch(ocrout.find("state", Rect(1530, 0, 2000, 130)).buildClick())
//     }
// }
// // when you're twiddling your thumbs waiting for something to happen, the heartbeat system is pretty useful
// // when you have a sequence of chained events with zero delay in between, then sequential system is useful
//
// suspend fun AutoService.getAvailableOps(): List<String> {
//     val names = mutableListOf<Text.Line>()
//     for(i in 1..3) {
//         val ocrtemp = getScreenText()
//
//         val row1Names = ocrtemp.find("\\S", Rect(635, 475, 2335, 530))
//         val row2Names = ocrtemp.find("\\S", Rect(635, 895, 2335, 950))
//         names.addAll(row1Names)
//         names.addAll(row2Names)
//
//         dispatch(buildSwipe(Point(2140, 535), Point(815, 535)))
//         dispatch((Point(815, 535).buildClick()))
//         delay(1000)
//     }
//
//     return names.map { it -> it.text.lowercase() }
// }
//
// suspend fun AutoService.findBestOpCombo(availableOps: List<String>): List<String> {
//     for (combo in baseTPsCombos) {
//         if (availableOps.containsAll(combo)) {
//             return combo
//         }
//     }
//
//     throw Exception("No valid combos")
// }
//
// suspend fun AutoService.selectBestOpCombo(bestOpCombo: List<String>) {
//     val mutableBestOpCombo = bestOpCombo.toMutableSet()
//     val foundNames = mutableSetOf<String>()
//     for(i in 1..4) {
//
//         val ocrtemp = getScreenText()
//         for(name in mutableBestOpCombo) {
//             if(name !in foundNames && ocrtemp.check(name)) {
//                 dispatch(ocrtemp.find(name).buildClick())
//                 foundNames.add(name)
//             }
//         }
//
//         if(foundNames.size == bestOpCombo.size) {
//             dispatch(ocrtemp.find("confirm").buildClick())
//             break
//         }
//
//         dispatch(buildSwipe(Point(815, 535), Point(2140, 535)))
//         dispatch(Point(2140, 535).buildClick())
//         delay(1000)
//     }
// }
