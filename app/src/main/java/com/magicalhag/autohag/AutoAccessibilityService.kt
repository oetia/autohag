package com.magicalhag.autohag

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.PixelFormat
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.FrameLayout
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class AutoAccessibilityService : AccessibilityService() {

    private lateinit var mLayout: FrameLayout;

    val screenshotExecutor = ScreenshotExecutor();
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

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
        configureShotButton()
        configureSwipeButton()

        Log.d(getString(R.string.log_tag), "ASS Laid Out")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(getString(R.string.log_tag), "START COMMAND RECEIVED")

        if(intent != null) {
            if(intent.action == "CLICK" && intent.extras != null) {
                val x = intent.extras!!.getFloat("x")
                val y = intent.extras!!.getFloat("y")
                val duration = intent.extras!!.getInt("duration")

                Log.d(getString(R.string.log_tag), "Click: " + x + " " + y + " " + duration)

                dispatch(buildClick(x, y, duration.toLong()))
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun configurePowerButton() {
        val powerButton = mLayout.findViewById(R.id.power) as Button
        powerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
            }
        })
    }

    private fun configureShotButton() {
        val shotButton = mLayout.findViewById(R.id.shot) as Button
        shotButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                takeScreenshot(Display.DEFAULT_DISPLAY, screenshotExecutor, object : TakeScreenshotCallback {
                    override fun onSuccess(screenshot: ScreenshotResult) {
                        val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)

                        Log.d(getString(R.string.log_tag), screenshot.timestamp.toString())
                        Log.d(getString(R.string.log_tag), bitmap!!.height.toString() + " " + bitmap!!.width.toString())

                        val image = InputImage.fromBitmap(bitmap, 0)

                        val result = recognizer.process(image)
                            .addOnSuccessListener { visionText ->
                                Log.d(getString(R.string.log_tag), visionText.text)
                            }

                    }

                    override fun onFailure(errorCode: Int) {
                        Log.e(getString(R.string.log_tag), "Screenshot Failed")
                    }
                })
            }
        })
    }

    private fun configureSwipeButton() {
        val swipeButton = mLayout.findViewById(R.id.swipe) as Button
        swipeButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
//                dispatch(buildSwipe(1000f, 1000f, 100f, 1000f, 500))
                dispatch(buildClick(550f, 650f, 500))
            }
        })
    }

    fun buildClick(x: Float, y: Float, duration: Long): GestureDescription {
        val clickPath = Path()
        clickPath.moveTo(x, y)

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

        return gestureBuilder.build()
    }

    fun buildSwipe(
        startX: Float, startY: Float, endX: Float, endY: Float, duration: Long
    ): GestureDescription {
        val swipePath = Path()
        swipePath.moveTo(startX, startY)
        swipePath.lineTo(endX, endY)

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))

        return gestureBuilder.build()
    }


    fun dispatch(gesture: GestureDescription): Boolean {
        return dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d(getString(R.string.log_tag), "gestureCompleted")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d(getString(R.string.log_tag), "gestureCancelled")
            }
        }, null)


    }


    override fun onCreate() {
        super.onCreate()
        Log.d(getString(R.string.log_tag), "ASS Up")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(getString(R.string.log_tag), "ASS Destroyed")
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        Log.d(getString(R.string.log_tag), "onAccessibilityEvent: $e")
    }

    override fun onInterrupt() {
        Log.d(getString(R.string.log_tag), "ASS Interrupted")
    }

}

