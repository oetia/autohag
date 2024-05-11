package com.magicalhag.autohag.auto.games.ark

// i guess that this can all be kept in the same scope
// just have an arknights object that everything else falls under.
// folder

val arknightsStateCheckDictionary = hashMapOf<String, Array<String>>(
    "start menu" to arrayOf("start", "check preannounce", "account management", "customer service"),
    "main menu" to arrayOf("friends", "archive", "recruit", "store", "missions", "base", "sanity"),
    "terminal" to arrayOf("to the most recent stage"),
    "stage select" to arrayOf("auto deploy", "start"),
    "auto count select" to arrayOf("auto deploy", "start", "1", "2", "3", "4", "5", "6"),
    "battle prep" to arrayOf("mission|operation", "start", "the roster for this operation cannot be changed"),
    "battle pending" to arrayOf("2x", "takeover"),
    "battle finished" to arrayOf("mission", "results"),
    "zero sanity" to arrayOf("restore"),
    "recruit menu" to arrayOf("recruit(?!ment)", "1", "2", "3", "4"),
    "recruit slot open" to arrayOf("recruit now"),
    "recruit slot hire" to arrayOf("hire"),
    "tag menu" to arrayOf("job", "tags"),
    "tag menu top operator" to arrayOf("top operator"),
    "bag animation" to arrayOf("skip"),
    "operator details" to arrayOf("certificate"),
    "refresh available" to arrayOf("tap to refresh"),
    "refresh confirm" to arrayOf("spend 1 refresh attempt?"),
    "store bar" to arrayOf("credit store"),
    "credit menu" to arrayOf("operator progress", "credit rules"),
    "credit available" to arrayOf("claim"),
    "credit collected" to arrayOf("collected"),
    "missions menu" to arrayOf("weekly missions"),
    "collect all" to arrayOf("collect all")
)