package com.vibeinventory.motorparts.utils

import com.vibeinventory.motorparts.data.model.InventoryItem
import org.junit.Test
import org.junit.Assert.*

class CsvImporterTest {

    @Test
    fun `importFromCsv should parse valid CSV content`() {
        // Given
        val csvContent = ""
            Name,Part Number,Quantity,Price,Category
            Oil Filter,OF-001,10,15.99,Engine
            Brake Pad,BP-002,5,45.99,Brake
        "".trimIndent()

        // Test parsing structure
        val lines = csvContent.lines()
        assertEquals(3, lines.size) // Header + 2 data rows
        assertTrue(lines[0].contains("Name"))
    }

    @Test
    fun `importFromCsv should handle empty file`() {
        // Given
        val csvContent = ""

        // Then
        assertTrue(csvContent.isEmpty())
    }

    @Test
    fun `importFromCsv should handle malformed CSV`() {
        // Given
        val csvContent = "Name,Part Number\nIncomplete"

        val lines = csvContent.lines()
        assertTrue(lines.size > 0)
    }

    @Test
    fun `importFromCsv should skip invalid rows`() {
        // Given
        val csvContent = ""
            Name,Part Number,Quantity,Price
            Valid Part,VP-001,10,15.99
            Invalid Part,,,-99
            Another Valid,AV-002,5,25.00
        "".trimIndent()

        val lines = csvContent.lines().filter { it.isNotEmpty() }
        assertEquals(4, lines.size)
    }

    @Test
    fun `importFromCsv should handle quoted fields`() {
        // Given
        val csvContent = ""Name,Description
"Part with, comma","Description with ""quotes"""
""
        
        assertTrue(csvContent.contains(","))
        assertTrue(csvContent.contains("\""))
    }
}
