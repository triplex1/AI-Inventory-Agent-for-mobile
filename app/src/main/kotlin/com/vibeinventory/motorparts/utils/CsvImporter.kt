package com.vibeinventory.motorparts.utils

import android.content.Context
import android.net.Uri
import com.vibeinventory.motorparts.data.model.InventoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Utility class for importing inventory data from CSV format
 */
class CsvImporter(private val context: Context) {
    
    /**
     * Import inventory items from CSV file
     */
    suspend fun importFromCsv(uri: Uri): Result<List<InventoryItem>> = withContext(Dispatchers.IO) {
        try {
            val items = mutableListOf<InventoryItem>()
            
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    // Skip header line
                    reader.readLine()
                    
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        line?.let {
                            val item = parseCsvLine(it)
                            if (item != null) {
                                items.add(item)
                            }
                        }
                    }
                }
            }
            
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Parse a single CSV line into InventoryItem
     */
    private fun parseCsvLine(line: String): InventoryItem? {
        return try {
            val values = parseCsvValues(line)
            
            if (values.size < 14) return null
            
            InventoryItem(
                id = values.getOrNull(0) ?: "",
                name = values.getOrNull(1) ?: "",
                partNumber = values.getOrNull(2) ?: "",
                description = values.getOrNull(3) ?: "",
                category = values.getOrNull(4) ?: "",
                quantity = values.getOrNull(5)?.toIntOrNull() ?: 0,
                minQuantity = values.getOrNull(6)?.toIntOrNull() ?: 5,
                location = values.getOrNull(7) ?: "",
                price = values.getOrNull(8)?.toDoubleOrNull() ?: 0.0,
                supplier = values.getOrNull(9) ?: "",
                barcode = values.getOrNull(10) ?: "",
                createdAt = values.getOrNull(11) ?: "",
                updatedAt = values.getOrNull(12) ?: "",
                tags = values.getOrNull(13)?.split(";")?.filter { it.isNotBlank() } ?: emptyList()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Parse CSV line handling quoted values
     */
    private fun parseCsvValues(line: String): List<String> {
        val values = mutableListOf<String>()
        var currentValue = StringBuilder()
        var insideQuotes = false
        var i = 0
        
        while (i < line.length) {
            val char = line[i]
            
            when {
                char == '"' && (i == 0 || line[i - 1] != '\\') -> {
                    insideQuotes = !insideQuotes
                }
                char == ',' && !insideQuotes -> {
                    values.add(currentValue.toString().trim())
                    currentValue = StringBuilder()
                }
                else -> {
                    currentValue.append(char)
                }
            }
            i++
        }
        
        // Add the last value
        values.add(currentValue.toString().trim())
        
        return values.map { it.replace("\"\"", "\"").removeSurrounding("\"") }
    }
    
    /**
     * Validate CSV format
     */
    suspend fun validateCsvFormat(uri: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val header = reader.readLine()
                    
                    // Check if header contains expected columns
                    val expectedColumns = listOf("ID", "Name", "Part Number", "Category", "Quantity")
                    val hasRequiredColumns = expectedColumns.all { 
                        header.contains(it, ignoreCase = true) 
                    }
                    
                    Result.success(hasRequiredColumns)
                }
            } ?: Result.failure(Exception("Cannot open file"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
