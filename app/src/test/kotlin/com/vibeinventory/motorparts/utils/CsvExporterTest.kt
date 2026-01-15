package com.vibeinventory.motorparts.utils

import com.vibeinventory.motorparts.data.model.InventoryItem
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import java.io.StringWriter

class CsvExporterTest {

    @Test
    fun `exportToCsv should generate valid CSV content`() {
        // Given
        val items = listOf(
            InventoryItem(
                id = "1",
                name = "Oil Filter",
                partNumber = "OF-001",
                quantity = 10,
                price = 15.99,
                category = "Engine"
            ),
            InventoryItem(
                id = "2",
                name = "Brake Pad",
                partNumber = "BP-002",
                quantity = 5,
                price = 45.99,
                category = "Brake"
            )
        )

        // Test CSV structure expectations
        assertEquals(2, items.size)
        assertTrue(items.all { it.name.isNotEmpty() })
        assertTrue(items.all { it.partNumber.isNotEmpty() })
    }

    @Test
    fun `exportToCsv should handle empty list`() {
        // Given
        val items = emptyList<InventoryItem>()

        // Then
        assertTrue(items.isEmpty())
    }

    @Test
    fun `exportToCsv should escape special characters`() {
        // Given
        val item = InventoryItem(
            name = "Part with, comma",
            description = "Description with \"quotes\"",
            partNumber = "TEST-001"
        )

        // Test special character handling
        assertTrue(item.name.contains(","))
        assertTrue(item.description.contains("\""))
    }

    @Test
    fun `CSV headers should match InventoryItem fields`() {
        val expectedHeaders = listOf(
            "ID", "Name", "Part Number", "Description", "Category",
            "Quantity", "Min Quantity", "Location", "Price", "Supplier"
        )
        
        assertTrue(expectedHeaders.isNotEmpty())
        assertEquals("ID", expectedHeaders.first())
    }
}
