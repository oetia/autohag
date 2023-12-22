package com.magicalhag.autohag.auto.core.logging

import android.util.Log
import android.widget.Toast
import com.magicalhag.autohag.auto.AutoService

fun log(message: Any) {
    val threadName = Thread.currentThread().name
    val wrapper = "=".repeat(50)
    Log.d("Kal's Panties", "$wrapper\n[$threadName] - $message\n$wrapper")
}

fun AutoService.toast(message: Any) {
    mainHandler.post {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }
}