package com.vibeinventory.motorparts.data.repository

import com.vibeinventory.motorparts.data.model.InventoryAnalytics
import com.vibeinventory.motorparts.data.model.InventoryItem
import com.vibeinventory.motorparts.data.model.StockLevel
import com.vibeinventory.motorparts.data.model.CategoryStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for analytics and reporting operations
 */
class AnalyticsRepository(private val inventoryRepository: InventoryRepository) {
    
    /**
     * Calculate comprehensive inventory analytics
     */
    fun getInventoryAnalytics(): Flow<InventoryAnalytics> {
        return inventoryRepository.getAllItems().map { items ->
            calculateAnalytics(items)
        }
    }
    
    /**
     * Get stock level breakdown
     */
    fun getStockLevels(): Flow<StockLevel> {
        return inventoryRepository.getAllItems().map { items ->
            val healthy = items.count { it.quantity > (it.minQuantity * 2) }
            val low = items.count { it.quantity in 1..it.minQuantity }
            val outOfStock = items.count { it.quantity == 0 }
            
            StockLevel(healthy, low, outOfStock)
        }
    }
    
    /**
     * Get category statistics
     */
    fun getCategoryStats(): Flow<List<CategoryStats>> {
        return inventoryRepository.getAllItems().map { items ->
            items.groupBy { it.category }
                .map { (category, categoryItems) ->
                    CategoryStats(
                        category = category,
                        itemCount = categoryItems.size,
                        totalValue = categoryItems.sumOf { it.quantity * it.price },
                        averagePrice = categoryItems.map { it.price }.average(),
                        lowStockCount = categoryItems.count { it.quantity <= it.minQuantity }
                    )
                }
                .sortedByDescending { it.totalValue }
        }
    }
    
    /**
     * Get items with low stock alerts
     */
    fun getLowStockAlerts(): Flow<List<InventoryItem>> {
        return inventoryRepository.getAllItems().map { items ->
            items.filter { it.quantity <= it.minQuantity }
                .sortedBy { it.quantity }
        }
    }
    
    /**
     * Calculate all analytics from items list
     */
    private fun calculateAnalytics(items: List<InventoryItem>): InventoryAnalytics {
        val totalItems = items.size
        val totalValue = items.sumOf { it.quantity * it.price }
        val lowStockItems = items.count { it.quantity in 1..it.minQuantity }
        val outOfStockItems = items.count { it.quantity == 0 }
        
        val categoryBreakdown = items.groupBy { it.category }
            .mapValues { it.value.size }
        
        val valueByCategory = items.groupBy { it.category }
            .mapValues { (_, categoryItems) ->
                categoryItems.sumOf { it.quantity * it.price }
            }
        
        val topValueItems = items
            .sortedByDescending { it.quantity * it.price }
            .take(5)
        
        val recentlyAdded = items
            .sortedByDescending { it.createdAt }
            .take(5)
        
        val lowStockAlerts = items
            .filter { it.quantity <= it.minQuantity }
            .sortedBy { it.quantity }
        
        return InventoryAnalytics(
            totalItems = totalItems,
            totalValue = totalValue,
            lowStockItems = lowStockItems,
            outOfStockItems = outOfStockItems,
            categoryBreakdown = categoryBreakdown,
            valueByCategory = valueByCategory,
            topValueItems = topValueItems,
            recentlyAdded = recentlyAdded,
            lowStockAlerts = lowStockAlerts
        )
    }
}
