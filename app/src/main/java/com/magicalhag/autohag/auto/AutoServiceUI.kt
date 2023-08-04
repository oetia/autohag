package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import com.magicalhag.autohag.R
import kotlinx.coroutines.runBlocking

class AutoServiceUI(autoService: AutoService) {

    private var autoService: AutoService
    private var mLayout: FrameLayout
    private var spinnerActivity: SpinnerActivity

    init {
        this.autoService = autoService
        this.mLayout = FrameLayout(autoService)
        this.spinnerActivity = SpinnerActivity()

        val wm = autoService.getSystemService(AccessibilityService.WINDOW_SERVICE) as WindowManager
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP

        val inflater = LayoutInflater.from(autoService)
        inflater.inflate(R.layout.action_bar, mLayout)
        wm.addView(mLayout, lp)

        configurePowerButton()
        configureIterateButton()
        configureStartButton()
        configureStopButton()
        configureTasksSpinner()
    }

    private fun configurePowerButton() {
        val powerButton = mLayout.findViewById<ImageButton>(R.id.power)
        powerButton.setOnClickListener {
            autoService.resetInternalState()
            autoService.launchArknights()
        }
    }

    private fun configureIterateButton() {
        val shotButton = mLayout.findViewById(R.id.iterate) as Button
        shotButton.setOnClickListener {
            runBlocking {
                autoService.iterate()
            }
        }
    }

    private fun configureStartButton() {
        val startButton = mLayout.findViewById(R.id.start) as Button
        startButton.setOnClickListener {
            if (autoService.getTimerThreadSpawned()) {
                autoService.unpauseTimerThread()
            } else {
                autoService.spawnTimerThread()
            }
        }
    }

    private fun configureStopButton() {
        val stopButton = mLayout.findViewById(R.id.stop) as Button
        stopButton.setOnClickListener { autoService.pauseTimerThread() }
    }

    // https://developer.android.com/develop/ui/views/components/spinner
    inner class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (parent != null) {
                val text = parent.getItemAtPosition(position)
                autoService.setRoutine(text.toString())
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun configureTasksSpinner() {
        val tasksSpinner = mLayout.findViewById<Spinner>(R.id.tasksSpinner)
        tasksSpinner.setBackgroundResource(android.R.drawable.spinner_dropdown_background)
        ArrayAdapter.createFromResource(autoService, R.array.tasks, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                tasksSpinner.adapter = adapter
                tasksSpinner.onItemSelectedListener = spinnerActivity
            }
    }

}
