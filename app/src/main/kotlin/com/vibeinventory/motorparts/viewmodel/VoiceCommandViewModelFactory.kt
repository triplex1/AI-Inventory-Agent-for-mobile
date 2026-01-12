package com.vibeinventory.motorparts.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vibeinventory.motorparts.ai.GeminiAIService
import com.vibeinventory.motorparts.data.repository.InventoryRepository

class VoiceCommandViewModelFactory(
    private val application: Application,
    private val aiService: GeminiAIService? = null,
    private val repository: InventoryRepository = InventoryRepository()
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoiceCommandViewModel::class.java)) {
            return VoiceCommandViewModel(application, aiService, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
