package com.magicalhag.autohag.auto.core.text

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService

interface StateCheckUtils {
    val stateCheckDictionary: HashMap<String, Array<String>>

    fun stateCheck(name: String): Boolean

    suspend fun stateCheckAction(name: String, callback: suspend () -> Boolean): Boolean

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
            val checks = stateCheckDictionary[name] as Array<String>
            return text.check(*checks);
        }

        override suspend fun stateCheckAction(name: String, callback: suspend () -> Boolean): Boolean {
            return if(stateCheck(name)) { callback() }
            else { false }
        }
    }
}
