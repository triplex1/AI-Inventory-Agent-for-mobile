package com.vibeinventory.motorparts.data.model

/**
 * Data class representing inventory analytics and statistics
 */
data class InventoryAnalytics(
    val totalItems: Int = 0,
    val totalValue: Double = 0.0,
    val lowStockItems: Int = 0,
    val outOfStockItems: Int = 0,
    val categoryBreakdown: Map<String, Int> = emptyMap(),
    val valueByCategory: Map<String, Double> = emptyMap(),
    val topValueItems: List<InventoryItem> = emptyList(),
    val recentlyAdded: List<InventoryItem> = emptyList(),
    val lowStockAlerts: List<InventoryItem> = emptyList()
)

/**
 * Data class for stock level statistics
 */
data class StockLevel(
    val healthy: Int = 0,      // quantity > minQuantity * 2
    val low: Int = 0,          // quantity <= minQuantity but > 0
    val outOfStock: Int = 0    // quantity == 0
)

/**
 * Data class for category statistics
 */
data class CategoryStats(
    val category: String,
    val itemCount: Int,
    val totalValue: Double,
    val averagePrice: Double,
    val lowStockCount: Int
)

/**
 * Data class for time-based analytics (for future expansion)
 */
data class TimeSeriesData(
    val date: String,
    val totalItems: Int,
    val totalValue: Double,
    val transactions: Int
)
