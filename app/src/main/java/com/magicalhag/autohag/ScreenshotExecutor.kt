package com.magicalhag.autohag

import java.util.concurrent.Executor

class ScreenshotExecutor : Executor {
    override fun execute(command: Runnable) {
       command.run()
    }
}