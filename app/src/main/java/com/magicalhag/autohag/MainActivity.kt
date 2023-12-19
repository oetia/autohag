package com.magicalhag.autohag

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import android.view.View
import android.view.WindowManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

    }
    fun startArknightsService(v: View) {
        val serviceIntent = Intent(this, ArknightsService::class.java)
        startService(serviceIntent)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val asdf = alarmManager?.canScheduleExactAlarms()
        Log.d("FAGGOT", "$asdf")
    }

    fun stopArknightsService(v: View) {
        val serviceIntent = Intent(this, ArknightsService::class.java)
        stopService(serviceIntent)
    }
}