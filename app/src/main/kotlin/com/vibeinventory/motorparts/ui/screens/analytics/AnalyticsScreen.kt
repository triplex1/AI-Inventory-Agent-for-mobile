package com.vibeinventory.motorparts.ui.screens.analytics

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vibeinventory.motorparts.data.model.InventoryItem
import com.vibeinventory.motorparts.viewmodel.AnalyticsViewModel
import com.vibeinventory.motorparts.viewmodel.ExportState
import com.vibeinventory.motorparts.viewmodel.ImportState
import com.vibeinventory.motorparts.viewmodel.ExportType
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = viewModel()
) {
    val analytics by viewModel.analytics.collectAsState()
    val stockLevels by viewModel.stockLevels.collectAsState()
    val categoryStats by viewModel.categoryStats.collectAsState()
    val lowStockAlerts by viewModel.lowStockAlerts.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val importState by viewModel.importState.collectAsState()
    
    val currencyFormat = remember { NumberFormat.getCurrencyInstance() }
    
    // File picker for import
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importFromCsv(it) }
    }
    
    // Handle export state
    LaunchedEffect(exportState) {
        when (exportState) {
            is ExportState.Success -> {
                val state = exportState as ExportState.Success
                when (state.type) {
                    ExportType.CSV -> viewModel.shareCsvFile(state.uri)
                    ExportType.PDF -> viewModel.sharePdfReport(state.uri)
                }
                viewModel.resetExportState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics & Reports") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Cards
            item {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        title = "Total Items",
                        value = analytics.totalItems.toString(),
                        icon = Icons.Default.Inventory,
                        modifier = Modifier.weight(1f)
                    )
                    
                    SummaryCard(
                        title = "Total Value",
                        value = currencyFormat.format(analytics.totalValue),
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        title = "Low Stock",
                        value = analytics.lowStockItems.toString(),
                        icon = Icons.Default.Warning,
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    
                    SummaryCard(
                        title = "Out of Stock",
                        value = analytics.outOfStockItems.toString(),
                        icon = Icons.Default.Error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Stock Level Chart
            item {
                Text(
                    text = "Stock Levels",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                StockLevelChart(stockLevels)
            }
            
            // Category Breakdown
            item {
                Text(
                    text = "Category Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(categoryStats.take(5)) { stat ->
                CategoryStatCard(stat, currencyFormat)
            }
            
            // Low Stock Alerts
            if (lowStockAlerts.isNotEmpty()) {
                item {
                    Text(
                        text = "Low Stock Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                items(lowStockAlerts.take(5)) { item ->
                    LowStockAlertCard(item)
                }
            }
            
            // Export & Import Actions
            item {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                ActionButtons(
                    onExportCsv = { viewModel.exportToCsv() },
                    onGeneratePdf = { viewModel.generatePdfReport() },
                    onImportCsv = { filePickerLauncher.launch("text/csv") },
                    exportState = exportState,
                    importState = importState
                )
            }
            
            // Status messages
            when (exportState) {
                is ExportState.Loading -> {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                is ExportState.Error -> {
                    item {
                        Text(
                            text = (exportState as ExportState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> {}
            }
            
            when (importState) {
                is ImportState.Loading -> {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                is ImportState.Success -> {
                    item {
                        val state = importState as ImportState.Success
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "Imported ${state.itemsImported} items successfully" +
                                        if (state.errors > 0) " (${state.errors} errors)" else "",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                is ImportState.Error -> {
                    item {
                        Text(
                            text = (importState as ImportState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StockLevelChart(stockLevel: com.vibeinventory.motorparts.data.model.StockLevel) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            StockLevelBar("Healthy Stock", stockLevel.healthy, Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(8.dp))
            StockLevelBar("Low Stock", stockLevel.low, Color(0xFFFFA726))
            Spacer(modifier = Modifier.height(8.dp))
            StockLevelBar("Out of Stock", stockLevel.outOfStock, Color(0xFFF44336))
        }
    }
}

@Composable
fun StockLevelBar(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(120.dp)
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .background(color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
        
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun CategoryStatCard(stat: com.vibeinventory.motorparts.data.model.CategoryStats, currencyFormat: NumberFormat) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stat.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stat.itemCount} items",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currencyFormat.format(stat.totalValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (stat.lowStockCount > 0) {
                    Text(
                        text = "${stat.lowStockCount} low stock",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun LowStockAlertCard(item: InventoryItem) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.partNumber,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Qty: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Min: ${item.minQuantity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    onExportCsv: () -> Unit,
    onGeneratePdf: () -> Unit,
    onImportCsv: () -> Unit,
    exportState: ExportState,
    importState: ImportState
) {
    val isLoading = exportState is ExportState.Loading || importState is ImportState.Loading
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onExportCsv,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export to CSV")
        }
        
        Button(
            onClick = onGeneratePdf,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate PDF Report")
        }
        
        OutlinedButton(
            onClick = onImportCsv,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.FileUpload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Import from CSV")
        }
    }
}
