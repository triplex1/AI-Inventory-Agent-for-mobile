package com.vibeinventory.motorparts.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.vibeinventory.motorparts.data.model.InventoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for exporting inventory data to CSV format
 */
class CsvExporter(private val context: Context) {
    
    /**
     * Export inventory items to CSV file
     */
    suspend fun exportToCsv(items: List<InventoryItem>): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "inventory_export_$timestamp.csv"
            val file = File(context.cacheDir, fileName)
            
            FileWriter(file).use { writer ->
                // Write CSV header
                writer.append("ID,Name,Part Number,Description,Category,Quantity,Min Quantity,Location,Price,Supplier,Barcode,Created At,Updated At,Tags\n")
                
                // Write data rows
                items.forEach { item ->
                    writer.append(escapeCsv(item.id))
                    writer.append(",")
                    writer.append(escapeCsv(item.name))
                    writer.append(",")
                    writer.append(escapeCsv(item.partNumber))
                    writer.append(",")
                    writer.append(escapeCsv(item.description))
                    writer.append(",")
                    writer.append(escapeCsv(item.category))
                    writer.append(",")
                    writer.append(item.quantity.toString())
                    writer.append(",")
                    writer.append(item.minQuantity.toString())
                    writer.append(",")
                    writer.append(escapeCsv(item.location))
                    writer.append(",")
                    writer.append(item.price.toString())
                    writer.append(",")
                    writer.append(escapeCsv(item.supplier))
                    writer.append(",")
                    writer.append(escapeCsv(item.barcode))
                    writer.append(",")
                    writer.append(escapeCsv(item.createdAt))
                    writer.append(",")
                    writer.append(escapeCsv(item.updatedAt))
                    writer.append(",")
                    writer.append(escapeCsv(item.tags.joinToString(";")))
                    writer.append("\n")
                }
            }
            
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Share CSV file
     */
    fun shareCsvFile(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Inventory Export"))
    }
    
    /**
     * Escape CSV special characters
     */
    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
