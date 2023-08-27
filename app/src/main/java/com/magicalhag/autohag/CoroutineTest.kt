package com.magicalhag.autohag

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

val logger: Logger = LoggerFactory.getLogger("CoroutinesTest")

suspend fun c(tag: String, delayDuration: Long = 500L) {
    logger.info("$tag - s")
    delay(delayDuration) // yields
    logger.info("$tag - e") // main thread dead by the time we reach this point. needs to go onto another thread.
}

suspend fun sequential() {
    coroutineScope { // basically a function that bundles stuff in it - doesn't finish until everything inside it is done
        launch { c("loli") }
        launch { c("kon", delayDuration=1000L) }
    }
    logger.info("lolikon")
}

suspend fun cunt() {
    logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa save me")
    while(true) { }
    delay(100)
    logger.info("nice")
}
suspend fun main(args: Array<String>) {
    // sequential()
    val dispatcher = Dispatchers.Default.limitedParallelism(1)
    val custom = Executors.newFixedThreadPool(8).asCoroutineDispatcher()

    coroutineScope {
        launch(CoroutineName("CUNT") + custom) { cunt() }
        launch(CoroutineName("lolikon dayo") + custom) { c("asdf") }
    }
}