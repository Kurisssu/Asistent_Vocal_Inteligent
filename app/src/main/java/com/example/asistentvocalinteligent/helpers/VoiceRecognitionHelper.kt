package com.example.asistentvocalinteligent.helpers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class VoiceRecognitionHelper(context: Context, private val onResult: (String) -> Unit) {
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ro-RO")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }
    private var isContinuousListening = false

    fun startListening(continuous: Boolean = false) {
        isContinuousListening = continuous
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizer", "Gata de ascultare")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "Început de vorbire detectat")
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizer", "Sfârșitul vorbirii detectat")
            }

            override fun onError(error: Int) {
                Log.e("SpeechRecognizer", "Eroare: $error")
                if (isContinuousListening) {
                    startListening(true)
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { onResult(it) }
                if (isContinuousListening) {
                    startListening(true)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SpeechRecognizer", "Eveniment detectat: $eventType")
            }
        })
        speechRecognizer.startListening(recognizerIntent)
    }

    fun stopListening() {
        isContinuousListening = false
        speechRecognizer.stopListening()
        speechRecognizer.destroy()
    }
}