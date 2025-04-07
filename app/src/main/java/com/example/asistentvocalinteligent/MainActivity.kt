package com.example.asistentvocalinteligent

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.asistentvocalinteligent.adapters.ChatAdapter
import com.example.asistentvocalinteligent.helpers.HuggingFaceAPI
import com.example.asistentvocalinteligent.helpers.TextToSpeechHelper
import com.example.asistentvocalinteligent.helpers.VoiceRecognitionHelper
import com.example.asistentvocalinteligent.models.Message

class MainActivity : AppCompatActivity() {
    private lateinit var voiceHelper: VoiceRecognitionHelper
    private lateinit var ttsHelper: TextToSpeechHelper
    private lateinit var hfApi: HuggingFaceAPI
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var voiceButton: ImageButton
    private lateinit var statusText: TextView
    private lateinit var micIndicator: View
    private var isListening = false
    private var isSpeaking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        voiceButton = findViewById(R.id.voiceButton)
        statusText = findViewById(R.id.statusText)
        micIndicator = findViewById(R.id.micIndicator)

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        chatAdapter = ChatAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = chatAdapter
        }

        // Initialize helpers
        hfApi = HuggingFaceAPI()
        ttsHelper = TextToSpeechHelper(this)

        // Setup voice recognition
        voiceHelper = VoiceRecognitionHelper(this) { userSpeech ->
            runOnUiThread {
                addMessage(userSpeech, true)
                hfApi.getResponse(userSpeech) { response ->
                    runOnUiThread {
                        addMessage(response, false)
                        speakResponse(response)
                    }
                }
            }
        }

        // Setup click listeners
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                addMessage(message, true)
                messageInput.text.clear()
                hfApi.getResponse(message) { response ->
                    runOnUiThread {
                        addMessage(response, false)
                        speakResponse(response)
                    }
                }
            }
        }

        voiceButton.setOnClickListener {
            if (isSpeaking) {
                // Dacă se citește un răspuns, oprim citirea
                ttsHelper.stop()
                isSpeaking = false
                updateStatus()
            } else if (isListening) {
                // Dacă se ascultă, oprim ascultarea
                stopListening()
            } else {
                // Dacă nu se face nimic, începem ascultarea
                startListening()
            }
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        // Verificăm starea reală a TTS când aplicația revine în prim plan
        isSpeaking = ttsHelper.isSpeaking()
        updateStatus()
    }

    private fun startListening() {
        voiceHelper.startListening(true)
        isListening = true
        updateStatus()
    }

    private fun stopListening() {
        voiceHelper.stopListening()
        isListening = false
        updateStatus()
    }

    private fun speakResponse(response: String) {
        isSpeaking = true
        updateStatus()
        ttsHelper.speak(response) {
            runOnUiThread {
                isSpeaking = false
                updateStatus()
            }
        }
    }

    private fun updateStatus() {
        // Actualizăm textul de stare
        statusText.text = when {
            isSpeaking -> "Se citește răspunsul..."
            isListening -> "Se ascultă..."
            else -> "Inactiv"
        }

        // Actualizăm culoarea butonului de microfon
        voiceButton.setColorFilter(
            when {
                isListening -> Color.RED
                isSpeaking -> Color.GRAY
                else -> Color.BLACK
            }
        )

        // Actualizăm indicatorul de microfon
        micIndicator.visibility = if (isListening) View.VISIBLE else View.GONE
    }

    private fun addMessage(text: String, isUser: Boolean) {
        chatAdapter.addMessage(Message(text, isUser))
        findViewById<RecyclerView>(R.id.chatRecyclerView).scrollToPosition(chatAdapter.itemCount - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsHelper.shutdown()
    }
}