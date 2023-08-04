package com.magicalhag.autohag

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Activity
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

// I think that i'm just going to opt for hard coding atm

class InputService : AccessibilityService() {

    private lateinit var mLayout: FrameLayout;

    private val screenshotExecutor = ScreenshotExecutor();
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val spinnerAcitivity = SpinnerActivity()

    // private var currentRoutine = "0SANITY"
    // private var currentRoutine = "BASE_COLLECT"
    // private var currentRoutine = "BASE_SWAP_OPS"
    private var currentRoutine = "EXIT_BASE"


    private val thread = Timer()
    private var started: Boolean = false
    private var threadPaused: Boolean = false
    private var iterationCounter = 0

    // BASE_COLLECT
    private var backlogChecked = false

    // BASE_REMOVE_DORM_OPS
    private var removeToggledOn = false
    private var dormsCleared = 0

    // ui

    private fun configureActionBar() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)

        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.action_bar, mLayout)
        wm.addView(mLayout, lp)

        configurePowerButton()
        configureShotButton()
        configureStartButton()
        configureStopButton()
        configureTasksSpinner()

    }

    private fun configurePowerButton() {
        val powerButton = mLayout.findViewById<ImageButton>(R.id.power)
        powerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                resetInternalState()

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

    private fun configureTasksSpinner() {
        val tasksSpinner = mLayout.findViewById<Spinner>(R.id.tasksSpinner)
        tasksSpinner.setBackgroundResource(android.R.drawable.spinner_dropdown_background)
        ArrayAdapter.createFromResource(this, R.array.tasks, android.R.layout.simple_spinner_item).also {
                adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tasksSpinner.adapter = adapter
            tasksSpinner.onItemSelectedListener = spinnerAcitivity
        }
    }

    inner class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if(parent != null) {
                val text = parent.getItemAtPosition(position)
                currentRoutine = text.toString()
                log("Current Routine Now: $currentRoutine")
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            return
        }
    }


    // heart of the service

    // so we want to have different routines that can be run
    // what's nice about the current 0sanity routine is that there's EXACTLY one option to take at every new window.
    // this means that you can very easily determine what to do next

    // from just the image you can determine your exact state
    // the next action is extremely obvious

    // you can't determine all the state that you need just from a single image.
    suspend fun iteration() {
        val screenshot = takeScreenshotSequential()
        val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
        val image = InputImage.fromBitmap(bitmap!!, 0)
        val visionText = recognizerProcessSequential(image)

        if (currentRoutine == "0SANITY") {
            for (block in visionText.textBlocks) {
                val blockText = block.text
                val blockCornerPoints = block.cornerPoints
                val blockCenter = getBoxCenter(blockCornerPoints as Array<Point>)

                if (Regex("start\\s+-\\d{1,2}").containsMatchIn(blockText.lowercase()) || Regex("mission\\s+start").containsMatchIn(
                        blockText.lowercase()
                    ) || Regex("mission\\s+results").containsMatchIn(blockText.lowercase())
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
                    }, 1000 * 15)
                } else if (Regex("restore").containsMatchIn(blockText.lowercase())) {
                    Log.d(getString(R.string.log_tag), "Sanity Gone: PAUSING")
                    threadPaused = true
                }
            }
        } else if (currentRoutine == "BASE_COLLECT") {

            var targetText: String =
                if (textMatchesAll(visionText.text, arrayOf("missions", "base", "depot"))) {
                    "base"
                } else if (!backlogChecked && textMatchesAll(
                        visionText.text, arrayOf("overview", "building mode")
                    )
                ) {
                    "not[i|t]?[f|e][i|t]?cat[i|t]?[o|d]n"
                } else if (textMatchesAll(visionText.text, arrayOf("backlog", "collectable"))) {
                    "collectable"
                } else if (textMatchesAll(visionText.text, arrayOf("backlog", "orders acquired"))) {
                    "orders acquired"
                } else if (textMatchesAll(visionText.text, arrayOf("backlog", "clues"))) {
                    backlogChecked = true
                    "backlog"
                } else if (backlogChecked && textMatchesAll(
                        visionText.text, arrayOf("overview", "building mode")
                    )
                ) {
                    log("Resources Collected, Changing Routine to Clearing Dorms")
                    currentRoutine = "BASE_REMOVE_DORM_OPS"
                    "overview"
                } else {
                    return
                }

            log(targetText)
            for (block in visionText.textBlocks) {
                val blockText = block.text
                val blockCornerPoints = block.cornerPoints
                val blockCenter = getBoxCenter(blockCornerPoints as Array<Point>)

                if (textMatchesAll(blockText, arrayOf(targetText))) {
                    dispatch(buildClick(blockCenter.x.toFloat(), blockCenter.y.toFloat(), 500))
                }
            }
        } else if (currentRoutine == "BASE_REMOVE_DORM_OPS") { // so, the current pattern that i'm seeing
            // 1) figure out external state from screenshot - couple with internal state from app variables
            // 2) build & execute an action

            // might also want a function to filter out textBlocks
            // look for the 5/5 right under dormitory
            if (dormsCleared == 4) {
                log("Dorms Cleared, Toggling Off Remove")

                val removeTextBlock =
                    findTextBlock(visionText.textBlocks, "remove") ?: return
                val removeTargetTextCenter =
                    getBoxCenter(removeTextBlock.cornerPoints as Array<Point>)

                dispatch(
                    buildClick(
                        removeTargetTextCenter.x.toFloat(),
                        removeTargetTextCenter.y.toFloat(),
                        100
                    )
                )
                removeToggledOn = false

                log("Changing Routine to Op Swap")
                currentRoutine = "BASE_SWAP_OPS"
                return
            }

            if (textMatchesAll(visionText.text, arrayOf("current assignment information"))) {
                if (!textMatchesAll(visionText.text, arrayOf("dormitory"))) {
                    dispatch(buildScroll("DOWN", 1500))
                } else {
                    if (!removeToggledOn) {
                        val removeTextBlock =
                            findTextBlock(visionText.textBlocks, "remove") ?: return
                        val removeTargetTextCenter =
                            getBoxCenter(removeTextBlock.cornerPoints as Array<Point>)
                        dispatch(
                            buildClick(
                                removeTargetTextCenter.x.toFloat(),
                                removeTargetTextCenter.y.toFloat(),
                                100
                            )
                        )
                        removeToggledOn = true
                    } else {
                        val dormitoryTextBlocks =
                            findAllTextBlocks(visionText.textBlocks, "dormitory")
                        if (dormitoryTextBlocks.size == 0) {
                            return
                        }

                        val dormitoryTextBlock = dormitoryTextBlocks[dormitoryTextBlocks.size - 1]
                        val dormitoryTextCenter =
                            getBoxCenter(dormitoryTextBlock.cornerPoints as Array<Point>)
                        val otherPoint = Point(
                            10000,
                            dormitoryTextCenter.y + 4 * getBoxHeight(dormitoryTextBlock.cornerPoints as Array<Point>)
                        )

                        val filteredTextBlocks = filterForTextBlocksInBox(
                            visionText.textBlocks, dormitoryTextCenter, otherPoint
                        )

                        if (findTextBlock(filteredTextBlocks, "\\+") == null) {
                            dispatch(buildClick(2220f, dormitoryTextCenter.y.toFloat(), 100L))
                            dormsCleared += 1
                        } else {
                            dispatch(buildScroll("DOWN", 1500))
                        }
                    }
                }
            }
        } else if (currentRoutine == "BASE_SWAP_OPS") {

            for (block in visionText.textBlocks) {
                val text = block.text
                val center = getBoxCenter(block.cornerPoints as Array<Point>)
                val area = getBoxArea(block.cornerPoints as Array<Point>)
                log("$text: $area $center")
            }

            if (textMatchesAll(
                    visionText.text, arrayOf("blueprint overview")
                )
            ) { // check that we're on overview page
                if (textMatchesAll(visionText.text, arrayOf("\\n\\s*\\+\\s*\\n"))) { // check for +
                    val plusTextBlock = findTextBlock(visionText.textBlocks, "\\+") ?: return
                    val plusTextCenter = getBoxCenter(plusTextBlock.cornerPoints as Array<Point>)
                    dispatch(
                        buildClick(
                            plusTextCenter.x.toFloat(), plusTextCenter.y.toFloat(), 500
                        )
                    )
                } else if (textMatchesAll(visionText.text, arrayOf("control center"))) {
                    log("No more ops to swap. Changing Routine to Exit Base")
                    currentRoutine = "EXIT_BASE"
                } else (dispatch(buildScroll("UP", 1500)))
            } else if (textMatchesAll(
                    visionText.text, arrayOf("trust")
                )
            ) { // at operator selection screen
                log("at operator selection")

                if (textMatchesAll(
                        visionText.text, arrayOf("tap on an operator")
                    )
                ) { // no operators in facility
                    log("no ops in current facility")

                    // filtering out non-operator names
                    // create a filter box based upon locations of different UI elements

                    val tapOpTextBlock =
                        findTextBlock(visionText.textBlocks, "tap on an operator") ?: return
                    val tapOpTextCenter = getBoxCenter(tapOpTextBlock.cornerPoints as Array<Point>)
                    val tapOpBoxWidth = getBoxWidth(tapOpTextBlock.cornerPoints as Array<Point>)
                    val tapOpBoxHeight = getBoxHeight(tapOpTextBlock.cornerPoints as Array<Point>)
                    // log("TAP OP: $tapOpBoxWidth $tapOpBoxHeight $tapOpTextCenter")

                    val confirmTextBlock = findTextBlock(visionText.textBlocks, "confirm") ?: return
                    val confirmTextBox = getBoxCenter(confirmTextBlock.cornerPoints as Array<Point>)
                    val ctbWidth = getBoxWidth(confirmTextBlock.cornerPoints as Array<Point>)
                    val ctbHeight = getBoxHeight(confirmTextBlock.cornerPoints as Array<Point>)
                    val ctbArea = getBoxArea(confirmTextBlock.cornerPoints as Array<Point>)

                    val stateTextBlock = findTextBlock(visionText.textBlocks, "state") ?: return
                    val stateTextBox = getBoxCenter(stateTextBlock.cornerPoints as Array<Point>)
                    val fbCorner1 = Point(
                        (tapOpTextCenter.x + 0.75 * tapOpBoxWidth).roundToInt(),
                        (tapOpTextCenter.y)
                    )
                    val fbCorner3 = Point(
                        confirmTextBox.x + ctbWidth,
                        (confirmTextBox.y - ctbHeight)
                    )

                    log("Op Name Filter Box: $fbCorner1, $fbCorner3")

                    val filteredTextBlocks = visionText.textBlocks.filter { textBlock ->
                        val center = getBoxCenter(textBlock.cornerPoints as Array<Point>)
                        val width = getBoxWidth(textBlock.cornerPoints as Array<Point>)
                        val height = getBoxHeight(textBlock.cornerPoints as Array<Point>)
                        val area = getBoxArea(textBlock.cornerPoints as Array<Point>)

                        val testCenter = center.x > fbCorner1.x && center.x < fbCorner3.x && center.y > fbCorner1.y && center.y < fbCorner3.y
                        val testHeight = height > ctbHeight / 1.8 // deals wtih RISC SKILL
                        val testText = !textBlock.text.contains("[0-9]{3,}|01|EXP|\\bon\\b|\\bshi[f|t]?t\\b".toRegex()) // deals with on shift + misc text

                        log(textBlock.text + " $testCenter, $testHeight, $testText")

                        testCenter && testHeight && testText
                    }

                    log("Filtered for Op Names")

                    // for (ftb in filteredTextBlocks.subList(0, 5)) {
                    //     val text = ftb.text
                    //     val center = getBoxCenter(ftb.cornerPoints as Array<Point>)
                    //     val area = getBoxArea(ftb.cornerPoints as Array<Point>)
                    //     log("$text: $area $center")
                    //
                    //     dispatch(buildClick(center.x.toFloat(), center.y.toFloat(), 100))
                    //     delay(300)
                    // }

                    val hardcodedPoints = arrayOf(
                        Point(750, 300),
                        Point(750, 700),
                        Point(950, 300),
                        Point(950, 700),
                        Point(1150, 300)
                    )
                    for(point in hardcodedPoints) {
                        log(point.toString())
                        dispatch(buildClick(point.x.toFloat(), point.y.toFloat(), 100))
                        delay(300)
                    }

                    log("Ops Selected")

                    val confirmTextCenter =
                        getBoxCenter(confirmTextBlock.cornerPoints as Array<Point>)
                    dispatch(
                        buildClick(
                            confirmTextCenter.x.toFloat(), confirmTextCenter.y.toFloat(), 100
                        )
                    )

                    log("Confirming...")

                } else if (textMatchesAll(visionText.text, arrayOf("current location"))) {
                    log("some ops in current facility")

                    val deselectTextBlock =
                        findTextBlock(visionText.textBlocks, "deselect all") ?: return
                    val deselectTextBox =
                        getBoxCenter(deselectTextBlock.cornerPoints as Array<Point>)
                    dispatch(
                        buildClick(
                            deselectTextBox.x.toFloat(), deselectTextBox.y.toFloat(), 100
                        )
                    )
                    delay(300)

                    log("deselected ops")

                    val stateTextBlocks = findAllTextBlocks(visionText.textBlocks, "state")
                    if (stateTextBlocks.size == 0) {
                        return
                    }
                    val stateTextBlock = stateTextBlocks[stateTextBlocks.size - 1]
                    val stateTextBox = getBoxCenter(stateTextBlock.cornerPoints as Array<Point>)
                    dispatch(buildClick(stateTextBox.x.toFloat(), stateTextBox.y.toFloat(), 100))
                    delay(300)
                    dispatch(buildClick(stateTextBox.x.toFloat(), stateTextBox.y.toFloat(), 100))
                    delay(300)

                    log("state double toggle")
                }
            } else if (textMatchesAll(visionText.text, arrayOf("confirm the shift"))) {
                val confirmTextBlock = findTextBlock(visionText.textBlocks, "^confirm$") ?: return
                val confirmTextCenter = getBoxCenter(confirmTextBlock.cornerPoints as Array<Point>)
                dispatch(
                    buildClick(
                        confirmTextCenter.x.toFloat(), confirmTextCenter.y.toFloat(), 500
                    )
                )
            }

        } else if(currentRoutine == "EXIT_BASE") {
            if(!textMatchesAll(visionText.text, arrayOf("missions", "base", "depot"))) {
                performGlobalAction(GLOBAL_ACTION_BACK)
            } else {
                log("Base Exited")
                currentRoutine = ""
            }
        }
    }


    // anti cbhell

    private suspend fun takeScreenshotSequential(): ScreenshotResult =
        suspendCoroutine { continuation ->
            takeScreenshot(Display.DEFAULT_DISPLAY,
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


    // gestures
    private fun dispatch(gesture: GestureDescription): Boolean {
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
        val topY = screenHeight * 0.2
        val bottomY = screenHeight * 0.80
        val leftX = screenWidth * 0.2
        val rightX = screenWidth * 0.80

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

        log(startPoint.toString())
        log(endPoint.toString())



        swipePath.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        swipePath.lineTo(endPoint.x.toFloat(), endPoint.y.toFloat())

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))

        return gestureBuilder.build()

        //        if(type == "SCROLL_DOWN") {
        //        }

    }

    // util

    private fun resetInternalState() {
        backlogChecked = false
        removeToggledOn = false

        dormsCleared = 0

        log("Internal State Reset")
    }

    private fun getBoxWidth(cornerPoints: Array<Point>): Int {
        return cornerPoints[1].x - cornerPoints[0].x
    }

    private fun getBoxHeight(cornerPoints: Array<Point>): Int {
        return cornerPoints[2].y - cornerPoints[1].y
    }

    private fun getBoxArea(cornerPoints: Array<Point>): Int {
        return getBoxHeight(cornerPoints) * getBoxWidth(cornerPoints)
    }

    private fun getBoxCenter(cornerPoints: Array<Point>): Point {
        val centerX = (cornerPoints[1].x + cornerPoints[0].x) / 2
        val centerY = (cornerPoints[2].y + cornerPoints[1].y) / 2
        return Point(centerX, centerY)
    }

    private fun textMatchesAll(
        text: String,
        wantedRegexes: Array<String> = arrayOf(),
        unwantedRegexes: Array<String> = arrayOf()
    ): Boolean {
        for (wantedRegex in wantedRegexes) {
            if (!Regex(wantedRegex.lowercase()).containsMatchIn(text.lowercase())) {
                return false
            }
        }

        for (unwantedRegex in unwantedRegexes) {
            if (Regex(unwantedRegex.lowercase()).containsMatchIn(text.lowercase())) {
                return false
            }
        }

        return true
    }

    private fun filterForTextBlocksInBox(
        textBlocks: List<Text.TextBlock>, corner1: Point, corner3: Point
    ): List<Text.TextBlock> {

        val filteredTextBox = ArrayList<Text.TextBlock>()

        for (textBlock in textBlocks) {
            val center = getBoxCenter(textBlock.cornerPoints as Array<Point>)
            if (center.x > corner1.x && center.x < corner3.x && center.y > corner1.y && center.y < corner3.y) {
                filteredTextBox.add(textBlock)
            }
        }

        return filteredTextBox.toList()
    }

    private fun findTextBlock(
        textBlocks: List<Text.TextBlock>, targetText: String
    ): Text.TextBlock? {


        for (textBlock in textBlocks) {
            if (Regex(targetText.lowercase()).containsMatchIn(textBlock.text.lowercase())) {
                return textBlock
            }
        }

        return null
    }

    private fun findAllTextBlocks(
        textBlocks: List<Text.TextBlock>, targetText: String
    ): ArrayList<Text.TextBlock> {

        val foundTextBlocks = ArrayList<Text.TextBlock>()

        for (textBlock in textBlocks) {
            if (Regex(targetText.lowercase()).containsMatchIn(textBlock.text.lowercase())) {
                foundTextBlocks.add(textBlock)
            }
        }

        return foundTextBlocks
    }

    // lifecycle
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

