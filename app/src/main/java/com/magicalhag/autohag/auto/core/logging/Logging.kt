package com.magicalhag.autohag.auto.core.logging

import android.util.Log
import android.widget.Toast
import com.magicalhag.autohag.auto.AutoService

fun log(message: Any, sandwich: Boolean = false) {
    val threadName = Thread.currentThread().name
    val wrapper = "=".repeat(50)
    val value = if (sandwich) "$wrapper\n[$threadName] - $message\n$wrapper" else "[$threadName] - $message"
    Log.d("Kal's Panties", value)
}

fun AutoService.toast(message: Any) {
    mainHandler.post {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }
}