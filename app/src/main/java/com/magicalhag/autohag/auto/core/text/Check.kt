package com.magicalhag.autohag.auto.core.text

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.core.logging.log

fun Text.check(vararg regExs: String): Boolean {
    return this.text.check(*regExs)
}

fun String.check(vararg regExs: String): Boolean {
    val (contains, excludes) = regExs.partition { this.lowercase().contains(it.toRegex()) }

    val joinC = contains.joinToString("`, `", "`", "`")
    val joinE = excludes.joinToString("`, `", "`", "`")
    val result = contains.size == regExs.size
    log("STRINGCHECK - ($result)\nContains: $joinC\nExcludes: $joinE")

    return result
}