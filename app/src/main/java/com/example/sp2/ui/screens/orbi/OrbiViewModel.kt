package com.example.sp2.ui.screens.orbi

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.ai.GeminiNanoTaskExtractor
import com.example.sp2.ai.TaskInstructionParser
import com.example.sp2.model.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

sealed class OrbiState {
    object Idle : OrbiState()
    object Listening : OrbiState()
    object Thinking : OrbiState()
    data class Ready(
        val title: String,
        val description: String,
        val date: LocalDate?,
        val time: LocalTime?,
        val priority: Priority,
        val usedAi: Boolean
    ) : OrbiState()
    data class Error(val message: String) : OrbiState()
}

class OrbiViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<OrbiState>(OrbiState.Idle)
    val state: StateFlow<OrbiState> = _state.asStateFlow()

    private val aiExtractor = GeminiNanoTaskExtractor()
    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening() {

        if (!SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            _state.value = OrbiState.Error("Tu dispositivo no tiene reconocimiento de voz disponible.")
            return
        }

        _state.value = OrbiState.Listening

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es")
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onError(error: Int) {
                _state.value = OrbiState.Error("No entendí, intenta de nuevo.")
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()

                if (text.isNullOrBlank()) {
                    _state.value = OrbiState.Error("No entendí, intenta de nuevo.")
                } else {
                    processInstruction(text)
                }
            }
        })

        speechRecognizer?.startListening(intent)
    }

    private fun processInstruction(text: String) {

        _state.value = OrbiState.Thinking

        viewModelScope.launch {

            val parsed = TaskInstructionParser.parse(text)

            if (parsed.isConfident) {

                _state.value = OrbiState.Ready(
                    title = parsed.title,
                    description = parsed.description,
                    date = parsed.date,
                    time = parsed.time,
                    priority = parsed.priority,
                    usedAi = false
                )

            } else {

                val aiResult = if (aiExtractor.isAvailable()) {
                    aiExtractor.extract(text)
                } else {
                    null
                }

                if (aiResult != null) {

                    val aiDate = aiResult.date.takeIf { it.isNotBlank() }
                        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

                    val aiTime = aiResult.time.takeIf { it.isNotBlank() }
                        ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }

                    val aiPriority = runCatching {
                        Priority.valueOf(aiResult.priority.uppercase())
                    }.getOrDefault(parsed.priority)

                    _state.value = OrbiState.Ready(
                        title = aiResult.title.ifBlank { parsed.title },
                        description = aiResult.description.ifBlank { parsed.description },
                        date = aiDate ?: parsed.date,
                        time = aiTime ?: parsed.time,
                        priority = aiPriority,
                        usedAi = true
                    )

                } else {
                    _state.value = OrbiState.Ready(
                        title = parsed.title,
                        description = parsed.description,
                        date = parsed.date,
                        time = parsed.time,
                        priority = parsed.priority,
                        usedAi = false
                    )
                }
            }
        }
    }

    fun reset() {
        _state.value = OrbiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
}