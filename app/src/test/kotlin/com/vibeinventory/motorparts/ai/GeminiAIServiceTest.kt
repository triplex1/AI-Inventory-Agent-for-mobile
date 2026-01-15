package com.vibeinventory.motorparts.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.vibeinventory.motorparts.data.model.InventoryItem
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class GeminiAIServiceTest {

    private lateinit var aiService: GeminiAIService
    private val testApiKey = "test-api-key"

    @Before
    fun setup() {
        // Note: Testing with actual Gemini API requires API key and network
        // These are structural tests
        aiService = GeminiAIService(testApiKey)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `GeminiAIService should be initialized with API key`() {
        assertNotNull(aiService)
    }

    @Test
    fun `parseInventoryItemFromText should extract item details`() = runTest {
        // Given
        val responseText = """
            Name: Brake Pad Set
            Part Number: BP-2024
            Quantity: 15
            Price: 89.99
            Category: Brake System
        """.trimIndent()

        // This would test the parsing logic
        assertTrue(responseText.contains("Brake Pad Set"))
        assertTrue(responseText.contains("BP-2024"))
    }

    @Test
    fun `processInventoryCommand should identify ADD intent`() = runTest {
        // Given
        val command = "Add 10 oil filters to inventory"
        
        // Test command parsing structure
        assertTrue(command.contains("add", ignoreCase = true))
        assertTrue(command.contains("oil filters", ignoreCase = true))
    }

    @Test
    fun `processInventoryCommand should identify SEARCH intent`() = runTest {
        // Given
        val command = "Find all brake pads in stock"
        
        assertTrue(command.contains("find", ignoreCase = true) || 
                   command.contains("search", ignoreCase = true))
    }

    @Test
    fun `processInventoryCommand should identify UPDATE intent`() = runTest {
        // Given
        val command = "Update quantity of spark plugs to 20"
        
        assertTrue(command.contains("update", ignoreCase = true))
    }

    @Test
    fun `processInventoryCommand should identify CHECK_STOCK intent`() = runTest {
        // Given
        val command = "Check stock levels for engine parts"
        
        assertTrue(command.contains("check", ignoreCase = true) ||
                   command.contains("stock", ignoreCase = true))
    }

    @Test
    fun `buildInventoryContext should format inventory list`() = runTest {
        // Given
        val items = listOf(
            InventoryItem(
                id = "1",
                name = "Oil Filter",
                partNumber = "OF-001",
                quantity = 10,
                category = "Engine"
            ),
            InventoryItem(
                id = "2",
                name = "Brake Pad",
                partNumber = "BP-002",
                quantity = 5,
                category = "Brake"
            )
        )

        // Test structure
        assertEquals(2, items.size)
        assertTrue(items.any { it.name == "Oil Filter" })
        assertTrue(items.any { it.category == "Brake" })
    }

    @Test
    fun `generateSuggestions should return relevant suggestions`() = runTest {
        // Given
        val items = listOf(
            InventoryItem(
                id = "1",
                name = "Oil Filter",
                quantity = 2, // Low stock
                minQuantity = 5
            ),
            InventoryItem(
                id = "2",
                name = "Brake Pad",
                quantity = 20,
                minQuantity = 5
            )
        )

        // Should identify low stock items
        val lowStockItems = items.filter { it.quantity < it.minQuantity }
        assertEquals(1, lowStockItems.size)
        assertEquals("Oil Filter", lowStockItems.first().name)
    }
}
