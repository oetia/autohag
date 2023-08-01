package com.magicalhag.autohag

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import android.view.View
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotManagerBuilder

class MainActivity : ComponentActivity() {

    private val screenshotManager by lazy {
        ScreenshotManagerBuilder(this)
            .withPermissionRequestCode(REQUEST_SCREENSHOT_PERMISSION)
            .build()
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val ass = AutoAccessibilityService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        screenshotManager.onActivityResult(requestCode, resultCode, data)
    }

    fun startArknightsService(v: View) {
        val serviceIntent = Intent(this, ArknightsService::class.java)
        startService(serviceIntent)
    }

    fun stopArknightsService(v: View) {
        val serviceIntent = Intent(this, ArknightsService::class.java)
        stopService(serviceIntent)
    }

    fun makeScreenshot(v: View) {
        val screenshotResult = screenshotManager.makeScreenshot()
        val subscription = screenshotResult.observe(
            onSuccess = { processScreenshot(it) },
            onError = {  }
        )
    }

    private fun processScreenshot(screenshot: Screenshot) {
        val bitmap = when (screenshot) {
            is ScreenshotBitmap -> screenshot.bitmap
        }
        val image = InputImage.fromBitmap(bitmap, 0)

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                Log.d(getString(R.string.log_tag), visionText.text)
            }

//        Log.d(getString(R.string.log_tag), "" + bitmap.byteCount)
    }

    companion object {
        private const val REQUEST_SCREENSHOT_PERMISSION = 1234
    }
}