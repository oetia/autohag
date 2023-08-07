package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.Point
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.TextBlock
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.magicalhag.autohag.R
import com.magicalhag.autohag.ScreenshotExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.NullPointerException
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AutoService : AccessibilityService() {

    private lateinit var autoServiceUI: AutoServiceUI

    private val screenshotExecutor = ScreenshotExecutor();
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var currentRoutine = ""

    private val timerThread = Timer()
    private val timerThreadPeriod: Long = 3000
    private var timerThreadSpawned: Boolean = false
    private var threadPaused: Boolean = false
    private var iterationCount = 0

    // BASE_COLLECT
    private var backlogChecked = false

    // BASE_REMOVE_DORM_OPS
    private var removeToggledOn = false
    private var dormsCleared = 0

    private var baseCleared = false
    private var zeroSanity = false
    private var recruitsDone = false

    // heart of the service

    // so we want to have different routines that can be run
    // what's nice about the current 0sanity routine is that there's EXACTLY one option to take at every new window.
    // this means that you can very easily determine what to do next

    // from just the image you can determine your exact state
    // the next action is extremely obvious

    // you can't determine all the state that you need just from a single image.
    private val hardcodedPoints = arrayOf(
        Point(750, 300),
        Point(750, 700),
        Point(950, 300),
        Point(950, 700),
        Point(1150, 300)
    )

    suspend fun iterate() {
        val screenshot = takeScreenshotSequential()
        val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
        val image = InputImage.fromBitmap(bitmap!!, 0)
        val visionText = recognizerProcessSequential(image)
        val text = visionText.text
        val blocks = visionText.textBlocks

        if(currentRoutine == "HOME") {
            if(text.excludesAll("friends", "archive", "depot")) {
                performGlobalAction(GLOBAL_ACTION_BACK)
            } else {
                if(!baseCleared) {
                    setRoutine("BASE")
                } else if(!recruitsDone) {
                    setRoutine("RECR")
                } else if(!zeroSanity) {
                    setRoutine("0SANITY")
                } else {
                    setRoutine("N/A")
                    pauseTimerThread()
                }
            }
        } else if (currentRoutine == "0SANITY") {
            // @formatter:off
            if (text.containsAll("friends", "archive", "sanity/")) {
                dispatch(buildClick(blocks.find("sanity/")))
            } else if (text.containsAll("to the most recent stage")) {
                dispatch(buildClick(blocks.find("to the most recent stage")))
            } else if (text.containsAll("auto deploy", "start\\s+-\\d{1,2}")) {
                dispatch(buildClick(blocks.find("start\\s+-\\d{1,2}")))
            } else if (text.containsAll("the roster for this operation cannot be changed", "mission\\s+start")) {
                dispatch(buildClick(blocks.find("mission\\s+start")))
            } else if (text.containsAll("2x", "takeover", "unit limit")) {
                Log.d(getString(R.string.log_tag), "Still In Autodeploy: PAUSING")
                threadPaused = true
                val unpause = Timer()
                unpause.schedule(object : TimerTask() {
                    override fun run() {
                        Log.d(getString(R.string.log_tag), "UNPAUSING")
                        threadPaused = false
                    }
                }, 1000 * 15)
            } else if (text.containsAll("mission\\s+results")) {
                dispatch(buildClick(blocks.find("mission\\s+results")))
            } else if (text.containsAll("restore", "sanity")) {
                log("0SANITY: PAUSING")
                zeroSanity = true
                setRoutine("HOME")
            }
            // @formatter:on
        } else if(currentRoutine == "BASE") {
            if(text.containsAll("friends", "archive", "base")) {
                val baseBlocks = blocks.findAll("base")
                val baseBlock = baseBlocks[baseBlocks.size - 1]
                dispatch(buildClick(baseBlock))
            } else if(text.containsAll("overview", "building mode")) {
                // "not[i|t]?[f|e][i|t]?cat[i|t]?[o|d]n"
                setRoutine("BASE_COLLECT")
            }
        } else if (currentRoutine == "BASE_COLLECT") {
            // @formatter:off
            if (text.containsAll("overview", "building mode")) {
                // "not[i|t]?[f|e][i|t]?cat[i|t]?[o|d]n"
                log("@ BASE - MAIN")
                dispatch(buildClick(Point(2250, 135)))
            } else if (text.containsAll("backlog")) {
                log("@BASE - BACKLOG")
                if (text.containsAll("collectable")) {
                    dispatch(buildClick(blocks.find("collectable")))
                } else if (text.containsAll("orders acquired")) {
                    dispatch(buildClick(blocks.find("orders acquired")))
                } else if (text.containsAll("clues")) {
                    dispatch(buildClick(blocks.find("backlog")))
                    setRoutine("BASE_R_OPS")
                }
            }
            // @formatter:on

        } else if (currentRoutine == "BASE_R_OPS") {
            // @formatter:off
            if (text.containsAll("overview", "building mode")) {
                log("@ BASE - MAIN")
                dispatch(buildClick(blocks.find("overview")))
            } else if (text.containsAll("current assignment information", "remove")) {
                log("@ BASE - OVERVIEW")
                if (blocks.find("remove").getCenter().x > 2175) { // hardcoded value to determine if toggled
                    dispatch(buildClick(blocks.find("remove")))
                } else if(text.excludesAll("dormitory")) {
                    dispatch(buildScroll("DOWN"))
                } else { // there's a dormitory in sight
                    val dormitoryBlocks = blocks.findAll("dormitory")
                    val dormitoryBOI = dormitoryBlocks[dormitoryBlocks.size - 1]
                    val blocksFiltered = blocks.filterByLocation(
                        dormitoryBOI.getCenter(),
                        Point(Int.MAX_VALUE, dormitoryBOI.getCenter().y + dormitoryBOI.getHeight() * 4)
                    )

                    if(blocksFiltered.getText().excludesAll("\\+")) { // dorm is full
                        dispatch(buildClick(Point(2220, dormitoryBOI.getCenter().y))) // hardcoded value for remove button
                    } else { // dorm already cleared
                        if(text.containsAll("b4")) {
                            setRoutine("BASE_OP_SWAP")
                        } else {
                            dispatch(buildScroll("DOWN"))
                        }
                    }
                }
            }
            // @formatter:on

        } else if (currentRoutine == "BASE_OP_SWAP") {
            // @formatter:off
            if (text.containsAll("current assignment information", "remove")) {
                log("@ BASE - OVERVIEW")
                if (blocks.find("remove").getCenter().x < 2175) { // hardcoded value to determine if toggled
                    dispatch(buildClick(blocks.find("remove")))
                } else if (text.containsAll("\\+")) {
                    dispatch(buildClick(blocks.find("\\+")))
                } else if (text.excludesAll("control center")){
                    dispatch(buildScroll("UP"))
                } else {
                    log("BASE CLEARED")
                    baseCleared = true
                    setRoutine("HOME")
                }
            } else if (text.containsAll("state", "skill", "trust")) {
                log("@ BASE - OP SELECTION")
                if (text.containsAll("tap on an operator")) {
                    for (point in hardcodedPoints) {
                        dispatch(buildClick(point))
                        delay(300)
                    }
                    dispatch(buildClick(blocks.find("confirm")))
                } else { // assumes not in dorm
                    log("OPERATORS IN FACILITY: REMOVING")
                    if(text.containsAll("deselect all")) {
                        dispatch(buildClick(blocks.find("deselect all")))
                        delay(300)
                        val stateBlocks = blocks.findAll("state")
                        val stateBlock = stateBlocks[stateBlocks.size - 1]
                        dispatch(buildClick(stateBlock))
                        delay(300)
                        dispatch(buildClick(stateBlock))
                        delay(300)
                    } else {
                        dispatch(buildClick(hardcodedPoints[0]))
                    }
                }
            } else if(text.containsAll("confirm the shift")) {
                val confirmBlocks = blocks.findAll("confirm")
                val confirmBlock = confirmBlocks[confirmBlocks.size - 1]
                dispatch(buildClick(confirmBlock))
            }
            // @formatter:on
        } else if(currentRoutine == "RECR") {
            if(text.containsAll("friends", "archive", "recruit")) {
                dispatch(buildClick(blocks.find("recruit")))
            } else if(text.containsAll("contacting")) {
                if(text.containsAll("recruit now")) {
                    dispatch(buildClick(blocks.find("recruit now")))
                }
                else if(text.containsAll("hire")) {
                    dispatch(buildClick(blocks.find("hire")))
                } else if(text.containsAll("job", "tags")) {
                    if(text.containsAll("top operator")) {
                        log("TOP OP FOUND")
                        currentRoutine = "TOP OP FOUND"
                        pauseTimerThread()
                        return
                    }

                    val combinations = arrayOf(
                        // https://gamepress.gg/arknights/core-gameplay/arknights-operator-recruitment-guide

                        // 5*
                        arrayOf("senior operator"),
                        arrayOf("support(?!er)", "vanguard"),
                        arrayOf("support(?!er)", "dp-recovery"),
                        arrayOf("crowd-control"),
                        arrayOf("survival", "defender"),
                        arrayOf("survival", "defense"),
                        arrayOf("defender", "dps"),
                        arrayOf("defense", "dps"),
                        arrayOf("defense", "(?<!van)guard"),
                        arrayOf("shift", "defender"),
                        arrayOf("shift", "defense"),
                        arrayOf("shift", "slow"),
                        arrayOf("specialist", "slow"),
                        arrayOf("shift", "dps"),
                        arrayOf("supporter", "dps"),
                        arrayOf("debuff", "supporter"),
                        arrayOf("debuff", "aoe"),
                        arrayOf("debuff", "fast-redeploy"),
                        arrayOf("debuff", "specialist"),
                        arrayOf("debuff", "melee"),
                        arrayOf("specialist", "survival"),
                        arrayOf("specialist", "dps"),
                        arrayOf("healing", "caster"),
                        arrayOf("healing", "slow"),
                        arrayOf("healing", "dps"),
                        arrayOf("caster", "dps", "slow"),

                        // 4*
                        arrayOf("healing", "vanguard"),
                        arrayOf("healing", "dp-recovery"),
                        arrayOf("slow", "(?<!van)guard"),
                        arrayOf("slow", "melee"),
                        arrayOf("slow", "dps"),
                        arrayOf("slow", "sniper"),
                        arrayOf("slow", "ranged", "dps"),
                        arrayOf("slow", "caster"),
                        arrayOf("slow", "aoe"),
                        arrayOf("survival", "sniper"),
                        arrayOf("survival", "ranged"),
                        arrayOf("specialist"),
                        arrayOf("shift"),
                        arrayOf("fast-redeploy"),
                        arrayOf("debuff"),
                        arrayOf("support(?!er)"),
                        arrayOf("nuker")
                    )

                    for(combination in combinations) {
                        if(text.containsAll(*combination)) {
                            log(combination)
                            for(tag in combination) {
                                dispatch(buildClick(blocks.find(tag)))
                                delay(300)
                            }

                            dispatch(buildClick(Point(900, 450)))
                            delay(300)
                            dispatch(buildClick(Point(1675, 875)))
                            delay(300)

                            log("4+*")
                            return
                        }
                    }

                    log("3*")
                    dispatch(buildClick(Point(900, 450)))
                    delay(300)
                    dispatch(buildClick(Point(1675, 875)))
                    delay(300)
                } else {
                    log("RECRUITS DONE")
                    recruitsDone = true
                    setRoutine("HOME")
                }
            } else if(text.containsAll("skip")) {
                dispatch(buildClick(blocks.find("skip")))
            } else if(text.containsAll("certificate")) {
                dispatch(buildClick(blocks.find("certificate")))
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

    private fun buildClick(block: TextBlock, duration: Long = 100L): GestureDescription {

        val center = block.getCenter()

        val clickPath = Path()
        clickPath.moveTo(center.x.toFloat(), center.y.toFloat())

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

        log("BUILDING CLICK: $center - ${block.text}")

        return gestureBuilder.build()
    }

    private fun buildClick(point: Point, duration: Long = 100L): GestureDescription {
        val clickPath = Path()
        clickPath.moveTo(point.x.toFloat(), point.y.toFloat())

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

        return gestureBuilder.build()
    }

    private fun buildClick(x: Float, y: Float, duration: Long): GestureDescription {
        val clickPath = Path()
        clickPath.moveTo(x, y)

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(clickPath, 0, duration))

        return gestureBuilder.build()
    }

    private fun buildScroll(type: String, duration: Long = 1500L): GestureDescription {

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

    }

    // util


    // vararg treats as an array variable length arguments
    fun String.containsAll(vararg regExs: String): Boolean {
        for (regEx in regExs) {
            if (!this.lowercase().contains(regEx.toRegex())) {
                log("String.containsAll: DSNTCONT '$regEx'")
                return false
            } else {
                log("String.containsAll: CONTAINS '$regEx'")
            }
        }
        return true
    }

    fun String.excludesAll(vararg regExs: String): Boolean {
        for (regEx in regExs) {
            if (this.lowercase().contains(regEx.toRegex())) {
                log("String.excludesAll: CONTAINS '$regEx'")
                return false
            } else {
                log("String.excludesAll: DSNTCONT '$regEx'")
            }
        }
        return true
    }

    fun List<Text.TextBlock>.filterByLocation(
        corner1: Point,
        corner3: Point
    ): List<Text.TextBlock> {
        val filtered = ArrayList<Text.TextBlock>()

        for (block in this) {
            val center = block.getCenter()
            if (center.x > corner1.x && center.x < corner3.x && center.y > corner1.y && center.y < corner3.y) {
                filtered.add(block)
            }
        }

        return filtered.toList()
    }


    fun List<Text.TextBlock>.find(text: String): Text.TextBlock {
        val found = ArrayList<Text.TextBlock>()
        for (block in this) {
            if (block.text.lowercase().contains(text.toRegex())) {
                found.add(block)
            }
        }
        if(found.size > 0) {
            return found[found.size - 1]
        } else {
            throw Exception("Block Not Found")
        }
    }

    fun List<Text.TextBlock>.findAll(text: String): List<Text.TextBlock> {
        val found = ArrayList<Text.TextBlock>()
        for (block in this) {
            if (block.text.lowercase().contains(text.toRegex())) {
                found.add(block)
            }
        }

        if (found.size == 0) {
            throw Exception("Block not Found")
        } else {
            return found.toList()
        }
    }

    fun List<Text.TextBlock>.getText(): String {
        var text = ""
        for (block in this) {
            text += block.text
        }
        return text
    }

    fun List<Text.TextBlock>.log() {
        for (block in this) {
            this@AutoService.log("${block.getCenter()} - ${block.text}")
        }
    }

    fun Text.TextBlock.getCenter(): Point {
        val centerX = (this.cornerPoints!![1].x + this.cornerPoints!![0].x) / 2
        val centerY = (this.cornerPoints!![2].y + this.cornerPoints!![1].y) / 2
        return Point(centerX, centerY)
    }

    fun Text.TextBlock.getWidth(): Int {
        return this.cornerPoints!![1].x - this.cornerPoints!![0].x
    }

    fun Text.TextBlock.getHeight(): Int {
        return this.cornerPoints!![2].y - this.cornerPoints!![1].y
    }

    fun resetInternalState() {
        backlogChecked = false
        removeToggledOn = false
        dormsCleared = 0

        baseCleared = false
        recruitsDone = false
        zeroSanity = false


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

    // start activities
    fun launchArknights() {
        val launchIntent = Intent()
            .setComponent(ComponentName("com.YoStarEN.Arknights", "com.u8.sdk.U8UnityContext"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        try {
            startActivity(launchIntent)
            log("Arknights Opened", true)
        } catch (e: Exception) {
            log("Arknights Failed to Open", true)
        }
    }

    fun spawnTimerThread() {
        timerThread.scheduleAtFixedRate(object : TimerTask() {
            override fun run() = runBlocking {
                if (!threadPaused) {
                    iterate()
                    iterationCount += 1
                    log("Timer Thread Iteration Count $iterationCount")
                }
            }
        }, 0, timerThreadPeriod)

        timerThreadSpawned = true
        log("Timer Thread Spawned")
    }

    fun pauseTimerThread() {
        threadPaused = true
        log("Timer Thread Paused", true)
    }

    fun unpauseTimerThread() {
        threadPaused = false
        log("Timer Thread Unpaused", true)
    }

    fun getTimerThreadSpawned(): Boolean {
        return timerThreadSpawned
    }

    fun setRoutine(newRoutine: String) {
        currentRoutine = newRoutine
        log("Routine is Now: $newRoutine", true)
    }

    // lifecycle
    override fun onServiceConnected() {
        super.onServiceConnected()
        autoServiceUI = AutoServiceUI(this)
        log("Service Connected")
    }

    override fun onCreate() {
        super.onCreate()
        log("Service Created")
    }

    override fun onDestroy() {
        super.onDestroy()
        timerThread.cancel()
        log("Service Destroyed")
    }

    override fun onInterrupt() {
        log("Service Interrupted")
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        log("AccessibilityEvent: $e")
    }

    private fun log(message: Any, toast: Boolean = false) {
        Log.d(getString(R.string.log_tag), message.toString())
        if (toast) {
            try {
                // Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
            } catch (e: NullPointerException) {
                Log.e(getString(R.string.log_tag), e.stackTraceToString())
            }

        }
    }
}

