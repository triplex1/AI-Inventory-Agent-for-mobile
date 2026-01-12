package com.vibeinventory.motorparts.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

/**
 * Manager for Android Speech Recognition
 * Provides a Flow-based API for speech recognition events
 */
class SpeechRecognitionManager(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    
    /**
     * Check if speech recognition is available on this device
     */
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }
    
    /**
     * Start listening for speech input
     * Returns a Flow that emits recognition events
     */
    fun startListening(): Flow<SpeechRecognitionResult> = callbackFlow {
        if (!isAvailable()) {
            trySend(SpeechRecognitionResult.Error("Speech recognition not available"))
            close()
            return@callbackFlow
        }
        
        // Create speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    trySend(SpeechRecognitionResult.ReadyForSpeech)
                }
                
                override fun onBeginningOfSpeech() {
                    trySend(SpeechRecognitionResult.BeginningOfSpeech)
                }
                
                override fun onRmsChanged(rmsdB: Float) {
                    // Audio level changed - can be used for visualizations
                    trySend(SpeechRecognitionResult.RmsChanged(rmsdB))
                }
                
                override fun onBufferReceived(buffer: ByteArray?) {
                    // Audio buffer received (not commonly used)
                }
                
                override fun onEndOfSpeech() {
                    trySend(SpeechRecognitionResult.EndOfSpeech)
                }
                
                override fun onError(error: Int) {
                    val errorMessage = getErrorMessage(error)
                    trySend(SpeechRecognitionResult.Error(errorMessage))
                    close()
                }
                
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        trySend(SpeechRecognitionResult.Success(matches[0], matches))
                    } else {
                        trySend(SpeechRecognitionResult.Error("No speech recognized"))
                    }
                    close()
                }
                
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )
                    if (!matches.isNullOrEmpty()) {
                        trySend(SpeechRecognitionResult.PartialResult(matches[0]))
                    }
                }
                
                override fun onEvent(eventType: Int, params: Bundle?) {
                    // Custom events (not commonly used)
                }
            })
        }
        
        // Create recognition intent
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak your inventory command..."
            )
        }
        
        // Start listening
        speechRecognizer?.startListening(intent)
        
        awaitClose {
            stopListening()
        }
    }
    
    /**
     * Stop listening and release resources
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
    
    /**
     * Convert error code to user-friendly message
     */
    private fun getErrorMessage(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Recognition error"
        }
    }
}

/**
 * Speech recognition result types
 */
sealed class SpeechRecognitionResult {
    object ReadyForSpeech : SpeechRecognitionResult()
    object BeginningOfSpeech : SpeechRecognitionResult()
    object EndOfSpeech : SpeechRecognitionResult()
    data class RmsChanged(val rmsdB: Float) : SpeechRecognitionResult()
    data class PartialResult(val text: String) : SpeechRecognitionResult()
    data class Success(val text: String, val allResults: List<String>) : SpeechRecognitionResult()
    data class Error(val message: String) : SpeechRecognitionResult()
}
