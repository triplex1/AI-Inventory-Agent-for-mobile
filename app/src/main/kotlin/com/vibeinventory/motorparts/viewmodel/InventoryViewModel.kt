package com.vibeinventory.motorparts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeinventory.motorparts.ai.GeminiAIService
import com.vibeinventory.motorparts.data.model.InventoryItem
import com.vibeinventory.motorparts.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: InventoryRepository = InventoryRepository(),
    private val aiService: GeminiAIService? = null
) : ViewModel() {
    
    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val filteredItems: StateFlow<List<InventoryItem>> = combine(
        _inventoryItems,
        _searchQuery
    ) { items, query ->
        if (query.isEmpty()) {
            items
        } else {
            items.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                item.partNumber.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    init {
        loadInventoryItems()
    }
    
    private fun loadInventoryItems() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllInventoryItems().collect { result ->
                result.onSuccess { items ->
                    _inventoryItems.value = items
                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load inventory"
                }
                _isLoading.value = false
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun addInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addInventoryItem(item).onFailure { exception ->
                _error.value = exception.message ?: "Failed to add item"
            }
            _isLoading.value = false
        }
    }
    
    fun updateInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateInventoryItem(item).onFailure { exception ->
                _error.value = exception.message ?: "Failed to update item"
            }
            _isLoading.value = false
        }
    }
    
    fun deleteInventoryItem(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteInventoryItem(id).onFailure { exception ->
                _error.value = exception.message ?: "Failed to delete item"
            }
            _isLoading.value = false
        }
    }
    
    fun updateQuantity(id: String, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(id, newQuantity).onFailure { exception ->
                _error.value = exception.message ?: "Failed to update quantity"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
