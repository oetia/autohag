package com.magicalhag.autohag

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.view.View

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
    }

    fun startArknightsService(v: View) {
        val serviceIntent = Intent(this, ArknightsService::class.java)
        startService(serviceIntent)
    }

    fun stopArknightsService(v: View) {
        val serviceIntent = Intent(this, ArknightsService::class.java)
        stopService(serviceIntent)
    }
}