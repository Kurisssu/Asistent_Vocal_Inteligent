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

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        tts?.stop()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "message_${System.currentTimeMillis()}")
        
        // Verificăm periodic dacă redarea s-a terminat
        Thread {
            while (tts?.isSpeaking == true) {
                Thread.sleep(100)
            }
            onComplete?.invoke()
        }.start()
    }

    fun stop() {
        tts?.stop()
    }

    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}