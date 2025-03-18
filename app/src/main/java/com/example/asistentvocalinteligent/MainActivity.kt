package com.example.asistentvocalinteligent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.asistentvocalinteligent.helpers.HuggingFaceAPI
import com.example.asistentvocalinteligent.helpers.TextToSpeechHelper
import com.example.asistentvocalinteligent.helpers.VoiceRecognitionHelper

class MainActivity : AppCompatActivity() {
    private lateinit var voiceHelper: VoiceRecognitionHelper
    private lateinit var ttsHelper: TextToSpeechHelper
    private lateinit var hfApi: HuggingFaceAPI
    private lateinit var textView: TextView
    private lateinit var button: Button
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        button = findViewById(R.id.button)

        hfApi = HuggingFaceAPI()
        ttsHelper = TextToSpeechHelper(this)

        voiceHelper = VoiceRecognitionHelper(this) { userSpeech ->
            runOnUiThread {
                textView.text = "Tu: $userSpeech"
            }
            hfApi.getResponse(userSpeech) { response ->
                runOnUiThread {
                    textView.append("\nBot: $response")
                    ttsHelper.speak(response)
                }
            }
        }

        button.setOnClickListener {
            if (!isListening) {
                voiceHelper.startListening()
                button.text = "Oprește ascultarea"
            } else {
                voiceHelper.stopListening()
                button.text = "Începe ascultarea"
            }
            isListening = !isListening
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsHelper.shutdown()
    }
}