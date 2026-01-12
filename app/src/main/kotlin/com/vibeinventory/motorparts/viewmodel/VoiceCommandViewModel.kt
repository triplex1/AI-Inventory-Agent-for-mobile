package com.vibeinventory.motorparts.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vibeinventory.motorparts.ai.AIResponse
import com.vibeinventory.motorparts.ai.GeminiAIService
import com.vibeinventory.motorparts.data.model.InventoryItem
import com.vibeinventory.motorparts.data.repository.InventoryRepository
import com.vibeinventory.motorparts.speech.SpeechRecognitionManager
import com.vibeinventory.motorparts.speech.SpeechRecognitionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VoiceCommandViewModel(
    application: Application,
    private val aiService: GeminiAIService? = null,
    private val repository: InventoryRepository = InventoryRepository()
) : AndroidViewModel(application) {
    
    private val speechRecognitionManager = SpeechRecognitionManager(application)
    private var recognitionJob: Job? = null
    
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    private val _transcription = MutableStateFlow("")
    val transcription: StateFlow<String> = _transcription.asStateFlow()
    
    private val _partialTranscription = MutableStateFlow("")
    val partialTranscription: StateFlow<String> = _partialTranscription.asStateFlow()
    
    private val _aiResponse = MutableStateFlow<AIResponse?>(null)
    val aiResponse: StateFlow<AIResponse?> = _aiResponse.asStateFlow()
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()
    
    private val _currentInventory = MutableStateFlow<List<InventoryItem>>(emptyList())
    
    init {
        // Load inventory on initialization
        loadInventory()
    }
    
    private fun loadInventory() {
        viewModelScope.launch {
            repository.getAllInventoryItems().collect { result ->
                result.onSuccess { items ->
                    _currentInventory.value = items
                }
            }
        }
    }
    
    /**
     * Check if speech recognition is available
     */
    fun isSpeechRecognitionAvailable(): Boolean {
        return speechRecognitionManager.isAvailable()
    }
    
    /**
     * Start voice recognition
     */
    fun startListening() {
        if (!isSpeechRecognitionAvailable()) {
            _error.value = "Speech recognition not available on this device"
            return
        }
        
        _isListening.value = true
        _transcription.value = ""
        _partialTranscription.value = ""
        _aiResponse.value = null
        _error.value = null
        _audioLevel.value = 0f
        
        recognitionJob = viewModelScope.launch {
            speechRecognitionManager.startListening().collect { result ->
                handleSpeechRecognitionResult(result)
            }
        }
    }
    
    /**
     * Stop voice recognition
     */
    fun stopListening() {
        _isListening.value = false
        speechRecognitionManager.stopListening()
        recognitionJob?.cancel()
        recognitionJob = null
        _partialTranscription.value = ""
        _audioLevel.value = 0f
    }
    
    /**
     * Handle speech recognition results
     */
    private fun handleSpeechRecognitionResult(result: SpeechRecognitionResult) {
        when (result) {
            is SpeechRecognitionResult.ReadyForSpeech -> {
                // Ready to receive speech
            }
            is SpeechRecognitionResult.BeginningOfSpeech -> {
                // User started speaking
                _partialTranscription.value = "Listening..."
            }
            is SpeechRecognitionResult.RmsChanged -> {
                // Update audio level for visualization
                _audioLevel.value = result.rmsdB
            }
            is SpeechRecognitionResult.PartialResult -> {
                // Show partial transcription as user speaks
                _partialTranscription.value = result.text
            }
            is SpeechRecognitionResult.EndOfSpeech -> {
                // User stopped speaking
                _partialTranscription.value = ""
            }
            is SpeechRecognitionResult.Success -> {
                // Final transcription received
                _isListening.value = false
                _transcription.value = result.text
                _partialTranscription.value = ""
                
                // Process the command with AI
                processCommand(result.text)
            }
            is SpeechRecognitionResult.Error -> {
                // Error occurred
                _isListening.value = false
                _partialTranscription.value = ""
                
                // Only show error if it's not "no match" (user didn't speak)
                if (!result.message.contains("No speech")) {
                    _error.value = result.message
                }
            }
        }
    }
    
    /**
     * Process voice command with AI
     */
    private fun processCommand(command: String) {
        if (aiService == null) {
            _error.value = "AI service not configured. Please add your Gemini API key."
            return
        }
        
        viewModelScope.launch {
            _isProcessing.value = true
            
            try {
                val result = aiService.processInventoryCommand(
                    command,
                    _currentInventory.value
                )
                
                result.onSuccess { response ->
                    _aiResponse.value = response
                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Failed to process command"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to process command"
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    /**
     * Manually update transcription (for testing without voice)
     */
    fun updateTranscription(text: String) {
        _transcription.value = text
        if (text.isNotEmpty()) {
            processCommand(text)
        }
    }
    
    /**
     * Clear current response and start over
     */
    fun clearResponse() {
        _transcription.value = ""
        _partialTranscription.value = ""
        _aiResponse.value = null
        _error.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
