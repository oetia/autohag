package com.magicalhag.autohag

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Point
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Timer
import java.util.TimerTask


class InputService : AccessibilityService() {

//    companion object {
//        // Potential Memory Leak
//        var serviceInstance: InputService? = null
//        fun getInstance(): InputService? {
//            return serviceInstance
//        }
//    }

    private lateinit var mLayout: FrameLayout;

    val screenshotExecutor = ScreenshotExecutor();
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val thread = Timer()
    var started: Boolean = false
    var threadPaused: Boolean = false
    var iterationCounter = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(getString(R.string.log_tag), "Service Connected")

//        serviceInstance = this
        configureActionBar()
    }

    private fun configureActionBar() {
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

//        val spinner = mLayout.findViewById<Spinner>(R.id.tasks_spinner)
//        ArrayAdapter.createFromResource(this, R.array.tasks_array, android.R.layout.simple_spinner_item).also { adapter ->
//            // Specify the layout to use when the list of choices appears
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            // Apply the adapter to the spinner
//            spinner.adapter = adapter
//        }


        configurePowerButton()
//        configureOpenButton()
        configureShotButton()
        configureStartButton()
        configureStopButton()
//        configureSwipeButton()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(getString(R.string.log_tag), "START COMMAND RECEIVED")

        if (intent != null) {
            if (intent.action == "CLICK" && intent.extras != null) {
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
        val powerButton = mLayout.findViewById<ImageButton>(R.id.power)
        powerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                val launchIntent = Intent().setComponent(
                    ComponentName(
                        "com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"
                    )
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                try {
                    startActivity(launchIntent)
                    Log.d(getString(R.string.log_tag), "Arknights Opened")
                } catch (e: Exception) {
                    stopSelf()
                    Log.d(getString(R.string.log_tag), "Arknights Open Failed")
                }

            }
        })
    }

    private fun configureOpenButton() {
        val openButton = mLayout.findViewById(R.id.open) as Button
        openButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                Log.d(getString(R.string.log_tag), "AAAAAAAAAAAAAAAAAAAAAAA")
//                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
                val launchIntent = Intent().setComponent(
                    ComponentName(
                        "com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"
                    )
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                try {
                    startActivity(launchIntent)
                    Log.d(getString(R.string.log_tag), "Arknights Opened")
                } catch (e: Exception) {
                    stopSelf()
                    Log.d(getString(R.string.log_tag), "Arknights Open Failed")
                }
            }

        })
    }

    private fun configureStartButton() {
        val startButton = mLayout.findViewById(R.id.start) as Button
        startButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                threadPaused = false
                if (!started) {
                    Log.d(
                        getString(R.string.log_tag),
                        "Thread hasn't been started. Creating a new one. "
                    )
                    thread.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (!threadPaused) {
                                iteration()

                                iterationCounter += 1
                                Log.d(getString(R.string.log_tag), iterationCounter.toString())
                            }
                        }
                    }, 0, 3000)
                    started = true
                } else {
                    Log.d(
                        getString(R.string.log_tag),
                        "Thread already started. Not creating a new one. "
                    )
                }
            }
        })
    }

    private fun configureStopButton() {
        val stopButton = mLayout.findViewById(R.id.stop) as Button
        stopButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                threadPaused = true
            }
        })

    }

//    private
//    private fun TextBlocks() {}

    private fun iteration() {
        takeScreenshot(
            Display.DEFAULT_DISPLAY,
            screenshotExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshot.hardwareBuffer, screenshot.colorSpace
                    )

                    Log.d(getString(R.string.log_tag), screenshot.timestamp.toString())
                    Log.d(
                        getString(R.string.log_tag),
                        bitmap!!.height.toString() + " " + bitmap!!.width.toString()
                    )

                    val image = InputImage.fromBitmap(bitmap, 0)

                    val result = recognizer.process(image).addOnSuccessListener { visionText ->
                        for (block in visionText.textBlocks) {
                            val blockText = block.text
                            val blockCornerPoints = block.cornerPoints
                            val blockCenter = getBoxCenter(blockCornerPoints as Array<Point>)
                            if (Regex("start\\s+-\\d{1,2}").containsMatchIn(blockText.lowercase())) {
                                Log.d(
                                    getString(R.string.log_tag),
                                    blockText + "\n" + blockCenter.toString() + "\n" + blockCornerPoints.contentDeepToString()
                                )

                                dispatch(
                                    buildClick(
                                        blockCenter.x.toFloat(), blockCenter.y.toFloat(), 500
                                    )
                                )
                            } else if (Regex("mission\\s+start").containsMatchIn(blockText.lowercase())) {
                                Log.d(
                                    getString(R.string.log_tag),
                                    blockText + "\n" + blockCenter.toString() + "\n" + blockCornerPoints.contentDeepToString()
                                )

                                dispatch(
                                    buildClick(
                                        blockCenter.x.toFloat(), blockCenter.y.toFloat(), 500
                                    )
                                )

                                Thread()
                            } else if (Regex("takeover").containsMatchIn(blockText.lowercase())) {

                                Log.d(
                                    getString(R.string.log_tag),
                                    blockText + "\n" + blockCenter.toString() + "\n" + blockCornerPoints.contentDeepToString()
                                )

                                threadPaused = true
                                val unpause = Timer()
                                unpause.schedule(object : TimerTask() {
                                    override fun run() {
                                        Log.d(
                                            getString(R.string.log_tag), "Unpaused"
                                        )
                                        threadPaused = false
                                    }
                                }, 1000 * 10)
                            } else if (Regex("mission\\s+results").containsMatchIn(blockText.lowercase())) {
                                Log.d(
                                    getString(R.string.log_tag),
                                    blockText + "\n" + blockCenter.toString() + "\n" + blockCornerPoints.contentDeepToString()
                                )

                                dispatch(
                                    buildClick(
                                        blockCenter.x.toFloat(), blockCenter.y.toFloat(), 500
                                    )
                                )
                            } else if (Regex("restore").containsMatchIn(blockText.lowercase())) {
                                Log.d(
                                    getString(R.string.log_tag), "Sanity Gone: PAUSING"
                                )

                                threadPaused = true
                            }

                        }
                    }

                }

                override fun onFailure(errorCode: Int) {
                    Log.e(getString(R.string.log_tag), "Screenshot Failed")
                }
            })
    }

    private fun configureShotButton() {
        val shotButton = mLayout.findViewById(R.id.shot) as Button
        shotButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                iteration()
            }
        })
    }


    fun getBoxCenter(cornerPoints: Array<Point>): Point {
        val centerX = (cornerPoints[1].x + cornerPoints[0].x) / 2
        val centerY = (cornerPoints[2].y + cornerPoints[1].y) / 2
        return Point(centerX, centerY)
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
                Log.d(getString(R.string.log_tag), "Input Service: gestureCompleted")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d(getString(R.string.log_tag), "Input Service: gestureCancelled")
            }
        }, null)
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(getString(R.string.log_tag), "Input Service: Created")
    }

    override fun onDestroy() {
        super.onDestroy()
        thread.cancel()
//        serviceInstance = null
        Log.d(getString(R.string.log_tag), "Input Service: Destroyed + Thread: Canceled")
    }

    override fun onUnbind(intent: Intent?): Boolean {
//        serviceInstance = null
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        Log.d(getString(R.string.log_tag), "Input Service: AccessibilityEvent $e")
    }

    override fun onInterrupt() {
        Log.d(getString(R.string.log_tag), "Input Service: Interrupted")
    }
}

