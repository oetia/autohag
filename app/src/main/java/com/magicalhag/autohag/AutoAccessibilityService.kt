package com.magicalhag.autohag

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.gesture.Gesture
import android.graphics.Path
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.FrameLayout


class AutoAccessibilityService : AccessibilityService() {

    private lateinit var mLayout: FrameLayout;
    override fun onCreate() {
        super.onCreate()
        Log.d(getString(R.string.log_tag), "ASS Created")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(getString(R.string.log_tag), "ASS Destroyed")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(getString(R.string.log_tag), "ASS Connected")

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.action_bar, mLayout)
        wm.addView(mLayout, lp)

        configurePowerButton()
        configureSwipeButton()
    }

    private fun configurePowerButton() {
        val powerButton = mLayout.findViewById(R.id.power) as Button
        powerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
            }
        })
    }

    private fun configureSwipeButton() {
        val swipeButton = mLayout.findViewById(R.id.swipe) as Button
        swipeButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val swipePath = Path()
                swipePath.moveTo(1000f, 1000f)
                swipePath.lineTo(100f, 1000f)
                val gestureBuilder = GestureDescription.Builder()
                gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, 500))
                dispatchGesture(gestureBuilder.build(), null, null)
            }
        })
    }


    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        Log.d(getString(R.string.log_tag), "onAccessibilityEvent: $e")

    }

    override fun onInterrupt() {}



    private fun buildClick(x: Number, y: Number): GestureDescription {
        val clickPath = Path()
        clickPath.moveTo(x.toFloat(), y.toFloat())
        val clickStroke = StrokeDescription(clickPath, 0, 1)
        val clickBuilder = GestureDescription.Builder()
        clickBuilder.addStroke(clickStroke)
        return clickBuilder.build()
    }

    private fun buildSwipe(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        duration: Long
    ): GestureDescription {
        val swipePath = Path()
        swipePath.moveTo(startX, startY)
        swipePath.lineTo(endX, endY)
        val swipeBuilder = GestureDescription.Builder()
        swipeBuilder.addStroke(StrokeDescription(swipePath, 0, duration))
        return swipeBuilder.build()
    }


    fun dispatch(x: Number, y: Number): Boolean {
        val result = this.dispatchGesture(
            buildClick(x, y),
            object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    Log.d(getString(R.string.log_tag), "gestureCompleted")
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    Log.d(getString(R.string.log_tag), "gestureCancelled")
                }
            },
            null
        )
        return result ?: false
    }
}