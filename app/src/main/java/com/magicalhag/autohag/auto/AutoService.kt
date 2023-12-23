package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService
import android.app.AlarmManager
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.magicalhag.autohag.auto.games.arknights.misc.arknightsLaunch
import com.magicalhag.autohag.auto.games.decoder
import com.magicalhag.autohag.auto.core.dispatch.buildClick
import com.magicalhag.autohag.auto.core.dispatch.buildSwipe
import com.magicalhag.autohag.auto.core.dispatch.dispatch
import com.magicalhag.autohag.auto.core.image.extractTextFromImage
import com.magicalhag.autohag.auto.core.image.getImageScreenshot
import com.magicalhag.autohag.auto.core.logging.log
import com.magicalhag.autohag.auto.core.logging.toast
import com.magicalhag.autohag.auto.games.State
import com.magicalhag.autohag.auto.games.ark.ArkS
import com.magicalhag.autohag.auto.games.ark.arkLaunch
import com.magicalhag.autohag.auto.games.e7.E7S
import com.magicalhag.autohag.auto.games.e7.e7Launch
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AutoService : AccessibilityService() {

    private var autoServiceUI: AutoServiceUI? = null

    val mainHandler = Handler(Looper.getMainLooper())

    val backgroundExecutor:ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "background") }
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().setExecutor(backgroundExecutor).build())
    private val dispatcher = backgroundExecutor.asCoroutineDispatcher()

    val coroutineScope = CoroutineScope(CoroutineName("AutoServiceCoroutineScope") + dispatcher)

    private var heartbeat: Job? = null
    private var badumps = 0
    private var sleeping = true

    var task: String = ""

    suspend fun badump(testing: Boolean = false) {

        if (sleeping && !testing) {
            log("zzzZZZz")
            return
        }

        val image = getImageScreenshot()
        val ocrout = extractTextFromImage(image)

        try {
            decoder(ocrout)
            // arknights(ocrout)
        } catch (e: Exception) {
            println(e.stackTraceToString())
        }
    }

    fun BEEPBEEPBEEP() {
        toast("BEEPBEEPBEEP")
        sleeping = false
    }

    fun coma() {
        toast("coma induced")
        sleeping = true
    }

    suspend fun nap(duration: Long = 3000L) {
        log("taking a nap")
        sleeping = true
        delay(duration)
        sleeping = false
        log("woke up")
    }

    fun updateState(newState: String) {
        if(newState.startsWith("ARK")) { State.g = State.G.Arknights; arkLaunch() }
        else if(newState.startsWith("E7")) { State.g = State.G.EpicSeven; e7Launch() }

        when (newState) {
            "ARK" -> { ArkS.t = ArkS.T.Home }
            "ARK-RECR" -> { ArkS.t = ArkS.T.Recruit }
            "ARK-CRED" -> { ArkS.t = ArkS.T.Credits }
            "ARK-0" -> { ArkS.t = ArkS.T.ZeroSanity }
            "ARK-MISS" -> { ArkS.t = ArkS.T.Missions }

            "E7" -> { E7S.t = E7S.T.Home }
            "E7-HNT" -> { E7S.t = E7S.T.Hunt }
            "E7-ABY" -> { E7S.t = E7S.T.Abyss }
            "E7-SANC" -> { E7S.t = E7S.T.SanctuaryHeart }
            "E7-ARNA" -> { E7S.t = E7S.T.Arena }
            "E7-REP" -> { E7S.t = E7S.T.Reputation }
        }
        log("STATE: ${if (State.g == State.G.Arknights) ArkS.t else E7S.t}")
        toast("STATE: ${if (State.g == State.G.Arknights) ArkS.t else E7S.t}")
    }


    // lifecycle
    override fun onServiceConnected() {
        super.onServiceConnected()
        autoServiceUI = AutoServiceUI(this)
        heartbeat = coroutineScope.launch {
            while (true) {
                badumps += 1
                // log("*~badump~* ($badumps)")
                badump()
                delay(1250L)
            }
        }
        log("Auto Service Connected")
    }


    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmListener: AlarmManager.OnAlarmListener
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        log("Auto Service Created")

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmListener = AlarmManager.OnAlarmListener { coroutineScope.launch { cunny() } }

        log("CAN SCHEDULE EXACT ALARMS?: $alarmManager?.canScheduleExactAlarms()")

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 4)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // calendar.set(Calendar.HOUR_OF_DAY, 23)
        // calendar.set(Calendar.MINUTE, 32)
        // calendar.set(Calendar.SECOND, 50)

        if(calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        log("${calendar.timeZone}")
        log("${calendar.timeInMillis}")
        log(SystemClock.currentNetworkTimeClock().millis())

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            // SystemClock.elapsedRealtime() + 3000L,
            "CUNNY",
            alarmListener,
            mainHandler
        )
    }

    suspend fun cunny() {

        log("CUNNY CUNNY uoooooooooooooooooooooooooogh")
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        delay(1000L)
        performGlobalAction(GLOBAL_ACTION_HOME)
        delay(1000L)

        dispatch(buildSwipe(Point(500, 1800), Point(500, 700), duration=300L))
        delay(1000L)

        dispatch(Point(300, 1150).buildClick())
        delay(100L)
        dispatch(Point(550, 1150).buildClick())
        delay(100L)
        dispatch(Point(800, 1150).buildClick())
        delay(100L)
        dispatch(Point(300, 1150).buildClick())
        delay(1000L)

        arkLaunch()

        delay(3000L)

        dispatch(Point(750, 75).buildClick()) // toggle begin

        // performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        // arknightsLaunch()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        dispatcher.close()
        alarmManager.cancel(alarmListener)
        log("Auto Service Destroyed")
    }

    override fun onInterrupt() {
        log("Auto Service Interrupted")
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        log("AccessibilityEvent: $e")
    }
}

