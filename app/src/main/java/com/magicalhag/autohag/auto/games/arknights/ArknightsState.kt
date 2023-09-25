package com.magicalhag.autohag.auto.games.arknights

class ArknightsState {
    var allState = "HOME"

    var task = "HOME"

    var baseTask = "REALIGN0"
    // var baseTask = "REALIGN11"
    // var baseTask = "TP1"
    // var baseTask = "REALIGN11"


    var realignTask = "HOME"
    fun realignReset() {
        realignTask = "HOME"
    }

    var tpTask = "HOME"
    // var tpTask = "CHECK_AVAILABLE_OPERATORS"
    // var tpTask = "CURRENT_OPERATOR_MORALE_CHECK"
    // var tpTask = "OPERATOR_CLEAR"
    // var tpTask = "ADD_NEW_OPS"

    var tpAvailableOpNames = mutableListOf<String>()
    var tpBlacklistedOpNames = mutableListOf<String>()
    var tpAlreadySelectedOpNames = mutableListOf<String>()
    fun tpReset() {
        tpTask = "HOME"
    }

    var facAvailableOpNames = mutableListOf<String>()
}