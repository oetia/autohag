package com.magicalhag.autohag.auto.core.text

import com.google.mlkit.vision.text.Text
import com.magicalhag.autohag.auto.AutoService

interface StateCheckUtils {
    val stateCheckDictionary: HashMap<String, Array<String>>
    fun sc(name: String): Boolean
    suspend fun sca(name: String, callback: suspend (String) -> Boolean): Boolean
}

// this seems very inefficient tbh. you recreate this object on every text instance.
// i think... you know what everything is in memory anyways, it should mainly be reference passing.
// using higher order functions to build out the state action history pair.
// state fills in a function, which fills in another function.
fun AutoService.generateStateCheckUtils(
    text: Text,
    stateCheckDictionary: HashMap<String, Array<String>>
): StateCheckUtils {
    return object : StateCheckUtils {
        override val stateCheckDictionary: HashMap<String, Array<String>>
            get() = stateCheckDictionary

        override fun sc(name: String): Boolean {
            val checks = stateCheckDictionary[name] as Array<String>
            val result = text.check(*checks);
            return result
        }

        override suspend fun sca(name: String, callback: suspend (String) -> Boolean): Boolean {
            return if(sc(name)) { callback(name) }
            else { false }
        }
    }
}
