package com.example.asistentvocalinteligent.helpers

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TextToSpeechHelper(context: Context) {
    private var tts: TextToSpeech? = TextToSpeech(context) {
        if (it == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}