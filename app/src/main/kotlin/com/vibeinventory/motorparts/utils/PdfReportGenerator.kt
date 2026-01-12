package com.vibeinventory.motorparts.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.vibeinventory.motorparts.data.model.InventoryAnalytics
import com.vibeinventory.motorparts.data.model.InventoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for generating PDF reports
 */
class PdfReportGenerator(private val context: Context) {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    /**
     * Generate comprehensive inventory report
     */
    suspend fun generateInventoryReport(
        items: List<InventoryItem>,
        analytics: InventoryAnalytics
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "inventory_report_$timestamp.pdf"
            val file = File(context.cacheDir, fileName)
            
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            
            // Title
            document.add(
                Paragraph("INVENTORY REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
                    .setBold()
            )
            
            document.add(
                Paragraph("Generated: ${dateFormat.format(Date())}")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10f)
            )
            
            document.add(Paragraph("\n"))
            
            // Summary Section
            document.add(
                Paragraph("SUMMARY")
                    .setFontSize(16f)
                    .setBold()
            )
            
            val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
                .useAllAvailableWidth()
            
            summaryTable.addCell("Total Items:")
            summaryTable.addCell(analytics.totalItems.toString())
            
            summaryTable.addCell("Total Value:")
            summaryTable.addCell(currencyFormat.format(analytics.totalValue))
            
            summaryTable.addCell("Low Stock Items:")
            summaryTable.addCell(analytics.lowStockItems.toString())
            
            summaryTable.addCell("Out of Stock:")
            summaryTable.addCell(analytics.outOfStockItems.toString())
            
            document.add(summaryTable)
            document.add(Paragraph("\n"))
            
            // Category Breakdown
            document.add(
                Paragraph("CATEGORY BREAKDOWN")
                    .setFontSize(16f)
                    .setBold()
            )
            
            val categoryTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1f, 2f)))
                .useAllAvailableWidth()
            
            categoryTable.addHeaderCell("Category")
            categoryTable.addHeaderCell("Items")
            categoryTable.addHeaderCell("Value")
            
            analytics.categoryBreakdown.forEach { (category, count) ->
                val value = analytics.valueByCategory[category] ?: 0.0
                categoryTable.addCell(category)
                categoryTable.addCell(count.toString())
                categoryTable.addCell(currencyFormat.format(value))
            }
            
            document.add(categoryTable)
            document.add(Paragraph("\n"))
            
            // Low Stock Alerts
            if (analytics.lowStockAlerts.isNotEmpty()) {
                document.add(
                    Paragraph("LOW STOCK ALERTS")
                        .setFontSize(16f)
                        .setBold()
                )
                
                val alertTable = Table(UnitValue.createPercentArray(floatArrayOf(3f, 2f, 1f, 1f)))
                    .useAllAvailableWidth()
                
                alertTable.addHeaderCell("Name")
                alertTable.addHeaderCell("Part Number")
                alertTable.addHeaderCell("Qty")
                alertTable.addHeaderCell("Min")
                
                analytics.lowStockAlerts.take(10).forEach { item ->
                    alertTable.addCell(item.name)
                    alertTable.addCell(item.partNumber)
                    alertTable.addCell(item.quantity.toString())
                    alertTable.addCell(item.minQuantity.toString())
                }
                
                document.add(alertTable)
                document.add(Paragraph("\n"))
            }
            
            // Top Value Items
            document.add(
                Paragraph("TOP VALUE ITEMS")
                    .setFontSize(16f)
                    .setBold()
            )
            
            val topItemsTable = Table(UnitValue.createPercentArray(floatArrayOf(3f, 2f, 1f, 2f, 2f)))
                .useAllAvailableWidth()
            
            topItemsTable.addHeaderCell("Name")
            topItemsTable.addHeaderCell("Part Number")
            topItemsTable.addHeaderCell("Qty")
            topItemsTable.addHeaderCell("Price")
            topItemsTable.addHeaderCell("Total Value")
            
            analytics.topValueItems.forEach { item ->
                val totalValue = item.quantity * item.price
                topItemsTable.addCell(item.name)
                topItemsTable.addCell(item.partNumber)
                topItemsTable.addCell(item.quantity.toString())
                topItemsTable.addCell(currencyFormat.format(item.price))
                topItemsTable.addCell(currencyFormat.format(totalValue))
            }
            
            document.add(topItemsTable)
            
            // Close document
            document.close()
            
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
     * Share PDF report
     */
    fun sharePdfReport(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Inventory Report"))
    }
}
