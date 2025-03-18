package com.example.asistentvocalinteligent.helpers

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class HuggingFaceAPI {
    private val client = OkHttpClient()
    private val API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.3"
    private val API_KEY = "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"
    fun getResponse(input: String, callback: (String) -> Unit) {
        val json = JSONObject().put("inputs", input).toString()
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HuggingFaceAPI", "Eroare: ${e.message}")
                callback("Îmi pare rău, nu am putut procesa.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val jsonResponse = JSONArray(it)
                    val reply = jsonResponse.getJSONObject(0).getString("generated_text")
                    callback(reply)
                }
            }
        })
    }
}