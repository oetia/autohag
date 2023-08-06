package com.magicalhag.autohag.utils

interface StringUtils {
    fun String.containsAll(regExs: Array<String>, log: ((message: Any) -> Unit)? = null): Boolean {
        for (regEx in regExs) {
            if (!this.lowercase().contains(regEx.toRegex())) {
                if (log != null) {
                    log("textContainsAll: DSNTCONT '$regEx'")
                }
                return false
            } else {
                if (log != null) {
                    log("textContainsAll: CONTAINS '$regEx'")
                }
            }
        }
        return true
    }
}