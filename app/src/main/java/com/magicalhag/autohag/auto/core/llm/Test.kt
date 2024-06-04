package com.magicalhag.autohag.auto.core.llm

import com.magicalhag.autohag.auto.core.logging.log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun testRequest() {

    // load api key from file
    val apiKey = File("api_key.txt").readText()

    val client = OkHttpClient()

    val mediaType = "application/json".toMediaTypeOrNull()
    val body =
        "{\n \"model\": \"gpt-3.5-turbo\",\n \"messages\": [{\"role\": \"user\", \"content\": \"Say this is a test!\"}],\n \"temperature\": 0.7\n}".toRequestBody(
            mediaType
        )
    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .post(body)
        .addHeader("cookie", "__cf_bm=RWpJvvR2KOyxqNIUct7nTGxebwFIKEofjncDVPGTQU0-1717485194-1.0.1.1-P134fpwGL5LxnXu58Cfu9cFIWM41zBWkYNXkK_prHMebQ4O36_gRRNefBrQHoPe686AtK66TylHIfmmX155Y7w; _cfuvid=ibD9gVMl.DpTeC8QIYpfDxg0o43hs1I.IdUlyUBFzRI-1717485194334-0.0.1.1-604800000")
        .addHeader("Content-Type", "application/json")
        .addHeader("User-Agent", "insomnia/8.6.0")
        .addHeader("Authorization", "Bearer $apiKey")
        .build()

    log("REQUEST SENT - SHOULD BLOCK BACKGROUND THREAD")
    val response = client.newCall(request).execute()
    log("RESPONSE RECEIVED")


    log(response.body?.string() ?: "no response")
}