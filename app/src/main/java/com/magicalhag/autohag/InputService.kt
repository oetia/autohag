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
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// Get display information for how to send out and receive swipes and clicks.
// Can relly on OCR carry for pretty much everything else
// There isn't exactly a convenient swipe method available for usage, so I pretty much just have to make my own
// Might also have to calculate where to click based upon what one is seeing
//
// In order to tell if state has changed, can search for verification text in other textblocks
// I'm wondering what kind of datastructure i should use for quick searching a bunch of information
// I think that having some kind of a previous state and calculating a diff... since loading will often stall and not pop off quite properly.
// I need a better system for state management.
// Despite that it works, code is jank and bodged together.
//
// Side note, I need to get more and more familiar with using vim
// For vertical movement, just using shift J and shift K works well enough here
// My screen is usually pretty zoomed in, so using control U control D is not that necessary

class InputService : AccessibilityService() {

    private lateinit var mLayout: FrameLayout;

    private val screenshotExecutor = ScreenshotExecutor();
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val thread = Timer()
    var started: Boolean = false
    var threadPaused: Boolean = false
    var iterationCounter = 0

    // ui

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

        configurePowerButton()
        configureScrollButton()
        configureShotButton()
        configureStartButton()
        configureStopButton()
    }

    private fun configurePowerButton() {
        val powerButton = mLayout.findViewById<ImageButton>(R.id.power)
        powerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {


                performGlobalAction(GLOBAL_ACTION_DPAD_DOWN)
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

    private fun configureShotButton() {
        val shotButton = mLayout.findViewById(R.id.shot) as Button
        shotButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) = runBlocking {
                iteration()
            }
        })
    }

    private fun configureStartButton() {
        val startButton = mLayout.findViewById(R.id.start) as Button
        startButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                threadPaused = false
                if (!started) {
                    Log.d(
                        getString(R.string.log_tag),
                        "Thread hasn't been started. Creating a new one."
                    )
                    thread.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() = runBlocking {
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

    private fun configureScrollButton() {
        val scrollButton = mLayout.findViewById(R.id.scroll) as Button
        scrollButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                dispatch(buildScroll("DOWN", 1000))
            }
        })

    }

    // heart of the service

    suspend fun iteration() {
        val screenshot = takeScreenshotSequential()
        val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
        val image = InputImage.fromBitmap(bitmap!!, 0)
        val visionText = recognizerProcessSequential(image)

        for (block in visionText.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockCenter = getBoxCenter(blockCornerPoints as Array<Point>)

            if (
                Regex("start\\s+-\\d{1,2}").containsMatchIn(blockText.lowercase()) ||
                Regex("mission\\s+start").containsMatchIn(blockText.lowercase()) ||
                Regex("mission\\s+results").containsMatchIn(blockText.lowercase())
            ) {
                dispatch(
                    buildClick(
                        blockCenter.x.toFloat(), blockCenter.y.toFloat(), 500
                    )
                )
            } else if (Regex("takeover").containsMatchIn(blockText.lowercase())) {
                Log.d(getString(R.string.log_tag), "Still In Autodeploy: PAUSING")
                threadPaused = true
                val unpause = Timer()
                unpause.schedule(object : TimerTask() {
                    override fun run() {
                        Log.d(getString(R.string.log_tag), "UNPAUSING")
                        threadPaused = false
                    }
                }, 1000 * 10)
            } else if (Regex("restore").containsMatchIn(blockText.lowercase())) {
                Log.d(getString(R.string.log_tag), "Sanity Gone: PAUSING")
                threadPaused = true
            }
        }
    }

    // anti cbhell

    private suspend fun takeScreenshotSequential(): ScreenshotResult =
        suspendCoroutine { continuation ->
            takeScreenshot(
                Display.DEFAULT_DISPLAY,
                screenshotExecutor,
                object : TakeScreenshotCallback {
                    override fun onFailure(errorCode: Int) {
                        continuation.resumeWithException(Exception("Screenshot Failed"))
                    }

                    override fun onSuccess(screenshot: ScreenshotResult) {
                        continuation.resume(screenshot)
                    }
                })
        }

    private suspend fun recognizerProcessSequential(image: InputImage): Text =
        suspendCoroutine { continuation ->
            val task = recognizer.process(image)
            while (!task.isSuccessful and !task.isComplete) {
            } // this is so fucking jank.
            continuation.resume(task.result)
        }


    // util
    private fun getBoxCenter(cornerPoints: Array<Point>): Point {
        val centerX = (cornerPoints[1].x + cornerPoints[0].x) / 2
        val centerY = (cornerPoints[2].y + cornerPoints[1].y) / 2
        return Point(centerX, centerY)
    }

    // gestures
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

    private fun buildClick(x: Float, y: Float, duration: Long): GestureDescription {
        val clickPath = Path()
        clickPath.moveTo(x, y)

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

        return gestureBuilder.build()
    }


    private fun buildScroll(type: String, duration: Long): GestureDescription {

        Log.d(getString(R.string.log_tag), "scrolling")

        val screenHeight = resources.displayMetrics.heightPixels
        val screenWidth = resources.displayMetrics.widthPixels

        // indicates corners of a square
        val topY = screenHeight * 0.25
        val bottomY = screenHeight * 0.75
        val leftX = screenWidth * 0.25
        val rightX = screenWidth * 0.75

        val swipePath = Path()
        lateinit var startPoint: Point
        lateinit var endPoint: Point

        when (type) {
            "UP" -> {
                startPoint = Point(rightX.toInt(), topY.toInt())
                endPoint = Point(rightX.toInt(), bottomY.toInt())
            }

            "DOWN" -> {
                startPoint = Point(rightX.toInt(), bottomY.toInt())
                endPoint = Point(rightX.toInt(), topY.toInt())
            }

            else -> { // default on a scroll up
                startPoint = Point(rightX.toInt(), topY.toInt())
                endPoint = Point(rightX.toInt(), bottomY.toInt())
            }
        }


        swipePath.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        swipePath.lineTo(endPoint.x.toFloat(), endPoint.y.toFloat())

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))

        return gestureBuilder.build()

        //        if(type == "SCROLL_DOWN") {
        //        }

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        configureActionBar()

        log("Service Set-up Complete")
    }

    override fun onCreate() {
        super.onCreate()
        log("Service Created")
    }

    override fun onDestroy() {
        super.onDestroy()
        thread.cancel()

        log("Service Destroyed")
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        log("AccessibilityEvent: $e")
    }

    override fun onInterrupt() {
        log("Service Interrupted")
    }

    private fun log(message: String) {
        Log.d(getString(R.string.log_tag), message)
    }
}

