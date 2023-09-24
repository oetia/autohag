package com.magicalhag.autohag.auto.games.arknights

class ArknightsState {
    var task = "HOME"

    var baseTask = "REALIGN1"
    // var baseTask = "TP1"

    var realignTask = "HOME"
    fun realignReset() {
        realignTask = "HOME"
    }

    var tpTask = "HOME"
    // var tpTask = "CHECK_AVAILABLE_OPERATORS"
    var tpAvailableOpNames = mutableListOf<String>()
    var tpBlacklistedOpNames = mutableListOf<String>()
    var tpAlreadySelectedOpNames = mutableListOf<String>()
    fun tpReset() {
        tpTask = "HOME"
    }

    var facAvailableOpNames = mutableListOf<String>()
}