package com.magicalhag.autohag.auto.core.text

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService
import com.magicalhag.autohag.auto.core.logging.log

interface StateCheckUtils {
    val stateCheckDictionary: HashMap<String, Array<String>>
    fun stateCheck(name: String): Boolean
    suspend fun sca(name: String, callback: suspend () -> Boolean): Boolean
}

// this seems very inefficient tbh. you recreate this object on every text instance.
// i think... you know what everything is in memory anyways, it should mainly be reference passing.
fun AutoService.generateStateCheckUtils(
    text: Text,
    stateCheckDictionary: HashMap<String, Array<String>>
): StateCheckUtils {
    return object : StateCheckUtils {
        override val stateCheckDictionary: HashMap<String, Array<String>>
            get() = stateCheckDictionary

        override fun stateCheck(name: String): Boolean {
            log("uuuuuooooooooogh")

            val checks = stateCheckDictionary[name] as Array<String>
            val result = text.check(*checks);
            log("uuuuuooooooooogh")
            return result
        }

        override suspend fun sca(name: String, callback: suspend () -> Boolean): Boolean {
            log("uuuuuooooooooogh")
            val res1 = stateCheck(name)
            log(" * asdfasdfasdfaasdf")
            if(res1) {
                return callback()
            } else {
                return false
            }
            // return if(stateCheck(name)) { callback() }
            // else { false }
        }
    }
}
