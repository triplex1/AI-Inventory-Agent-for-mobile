package com.vibeinventory.motorparts.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vibeinventory.motorparts.data.model.InventoryAnalytics
import com.vibeinventory.motorparts.data.model.InventoryItem
import com.vibeinventory.motorparts.data.model.StockLevel
import com.vibeinventory.motorparts.data.model.CategoryStats
import com.vibeinventory.motorparts.data.repository.AnalyticsRepository
import com.vibeinventory.motorparts.data.repository.InventoryRepository
import com.vibeinventory.motorparts.utils.CsvExporter
import com.vibeinventory.motorparts.utils.CsvImporter
import com.vibeinventory.motorparts.utils.PdfReportGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for analytics and reporting features
 */
class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val inventoryRepository = InventoryRepository()
    private val analyticsRepository = AnalyticsRepository(inventoryRepository)
    private val csvExporter = CsvExporter(application)
    private val csvImporter = CsvImporter(application)
    private val pdfGenerator = PdfReportGenerator(application)
    
    // Analytics data
    val analytics: StateFlow<InventoryAnalytics> = analyticsRepository
        .getInventoryAnalytics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InventoryAnalytics()
        )
    
    val stockLevels: StateFlow<StockLevel> = analyticsRepository
        .getStockLevels()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StockLevel()
        )
    
    val categoryStats: StateFlow<List<CategoryStats>> = analyticsRepository
        .getCategoryStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val lowStockAlerts: StateFlow<List<InventoryItem>> = analyticsRepository
        .getLowStockAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // UI State
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()
    
    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()
    
    /**
     * Export inventory to CSV
     */
    fun exportToCsv() {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            
            inventoryRepository.getAllItems().first().let { items ->
                csvExporter.exportToCsv(items).fold(
                    onSuccess = { uri ->
                        _exportState.value = ExportState.Success(uri, ExportType.CSV)
                    },
                    onFailure = { error ->
                        _exportState.value = ExportState.Error(error.message ?: "Export failed")
                    }
                )
            }
        }
    }
    
    /**
     * Generate PDF report
     */
    fun generatePdfReport() {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            
            val items = inventoryRepository.getAllItems().first()
            val analyticsData = analytics.value
            
            pdfGenerator.generateInventoryReport(items, analyticsData).fold(
                onSuccess = { uri ->
                    _exportState.value = ExportState.Success(uri, ExportType.PDF)
                },
                onFailure = { error ->
                    _exportState.value = ExportState.Error(error.message ?: "PDF generation failed")
                }
            )
        }
    }
    
    /**
     * Import inventory from CSV
     */
    fun importFromCsv(uri: Uri) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            
            // Validate format first
            csvImporter.validateCsvFormat(uri).fold(
                onSuccess = { isValid ->
                    if (isValid) {
                        proceedWithImport(uri)
                    } else {
                        _importState.value = ImportState.Error("Invalid CSV format")
                    }
                },
                onFailure = { error ->
                    _importState.value = ImportState.Error(error.message ?: "Validation failed")
                }
            )
        }
    }
    
    private suspend fun proceedWithImport(uri: Uri) {
        csvImporter.importFromCsv(uri).fold(
            onSuccess = { items ->
                // Add items to repository
                var successCount = 0
                var errorCount = 0
                
                items.forEach { item ->
                    inventoryRepository.addItem(item).fold(
                        onSuccess = { successCount++ },
                        onFailure = { errorCount++ }
                    )
                }
                
                _importState.value = ImportState.Success(successCount, errorCount)
            },
            onFailure = { error ->
                _importState.value = ImportState.Error(error.message ?: "Import failed")
            }
        )
    }
    
    /**
     * Share CSV export
     */
    fun shareCsvFile(uri: Uri) {
        csvExporter.shareCsvFile(uri)
    }
    
    /**
     * Share PDF report
     */
    fun sharePdfReport(uri: Uri) {
        pdfGenerator.sharePdfReport(uri)
    }
    
    /**
     * Reset export state
     */
    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }
    
    /**
     * Reset import state
     */
    fun resetImportState() {
        _importState.value = ImportState.Idle
    }
}

/**
 * Export state sealed class
 */
sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val uri: Uri, val type: ExportType) : ExportState()
    data class Error(val message: String) : ExportState()
}

/**
 * Export type enum
 */
enum class ExportType {
    CSV, PDF
}

/**
 * Import state sealed class
 */
sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class Success(val itemsImported: Int, val errors: Int) : ImportState()
    data class Error(val message: String) : ImportState()
}
