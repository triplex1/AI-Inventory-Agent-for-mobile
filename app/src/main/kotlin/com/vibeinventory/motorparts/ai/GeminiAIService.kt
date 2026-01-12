package com.vibeinventory.motorparts.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.vibeinventory.motorparts.data.model.InventoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiAIService(private val apiKey: String) {
    
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )
    
    /**
     * Process natural language command about inventory
     */
    suspend fun processInventoryCommand(
        command: String,
        currentInventory: List<InventoryItem>
    ): Result<AIResponse> {
        return try {
            val inventoryContext = buildInventoryContext(currentInventory)
            
            val prompt = """
                You are an AI assistant for a motorparts inventory management system.
                
                Current Inventory Summary:
                $inventoryContext
                
                User Command: "$command"
                
                Analyze the user's command and provide:
                1. The intent (search, add, update, delete, check_stock, general_query)
                2. Relevant items from inventory (if applicable)
                3. A natural language response
                4. Suggested actions (if any)
                
                Respond in a concise, helpful manner focused on inventory management.
            """.trimIndent()
            
            val response = model.generateContent(prompt)
            val aiText = response.text ?: "I couldn't process that command."
            
            Result.success(
                AIResponse(
                    intent = detectIntent(command),
                    responseText = aiText,
                    relevantItems = findRelevantItems(command, currentInventory),
                    suggestedAction = null
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate inventory insights and suggestions
     */
    suspend fun generateInventoryInsights(
        inventory: List<InventoryItem>
    ): Flow<String> = flow {
        val lowStockItems = inventory.filter { it.quantity <= it.minQuantity }
        val outOfStockItems = inventory.filter { it.quantity == 0 }
        
        val prompt = """
            Analyze this motorparts inventory and provide insights:
            
            Total Items: ${inventory.size}
            Low Stock Items (${lowStockItems.size}): ${lowStockItems.joinToString { it.name }}
            Out of Stock (${outOfStockItems.size}): ${outOfStockItems.joinToString { it.name }}
            
            Provide:
            1. Key observations
            2. Recommendations for restocking
            3. Inventory health assessment
        """.trimIndent()
        
        model.generateContentStream(prompt).collect { chunk ->
            emit(chunk.text ?: "")
        }
    }
    
    /**
     * Parse natural language to extract inventory item details
     */
    suspend fun parseInventoryItemFromNaturalLanguage(
        description: String
    ): Result<InventoryItem> {
        return try {
            val prompt = """
                Extract inventory item details from this natural language description:
                "$description"
                
                Return a structured format with:
                - name: (item name)
                - partNumber: (if mentioned)
                - quantity: (if mentioned, default to 1)
                - location: (if mentioned)
                - price: (if mentioned)
                - category: (engine, brake, suspension, electrical, transmission, body, accessories, fluids, other)
                
                Example: "Add 10 brake pads to location A-12 at $45.99 each"
                Should extract: name=Brake Pads, quantity=10, location=A-12, price=45.99, category=brake
            """.trimIndent()
            
            val response = model.generateContent(prompt)
            val parsed = parseItemFromResponse(response.text ?: "")
            Result.success(parsed)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildInventoryContext(inventory: List<InventoryItem>): String {
        return inventory.take(10).joinToString("\n") { item ->
            "${item.name} (${item.partNumber}): ${item.quantity} in stock at ${item.location}"
        } + if (inventory.size > 10) "\n...and ${inventory.size - 10} more items" else ""
    }
    
    private fun detectIntent(command: String): AIIntent {
        val lowerCommand = command.lowercase()
        return when {
            lowerCommand.contains("search") || lowerCommand.contains("find") || 
            lowerCommand.contains("show") -> AIIntent.SEARCH
            lowerCommand.contains("add") || lowerCommand.contains("new") -> AIIntent.ADD
            lowerCommand.contains("update") || lowerCommand.contains("change") -> AIIntent.UPDATE
            lowerCommand.contains("delete") || lowerCommand.contains("remove") -> AIIntent.DELETE
            lowerCommand.contains("stock") || lowerCommand.contains("quantity") || 
            lowerCommand.contains("how many") -> AIIntent.CHECK_STOCK
            else -> AIIntent.GENERAL_QUERY
        }
    }
    
    private fun findRelevantItems(
        command: String,
        inventory: List<InventoryItem>
    ): List<InventoryItem> {
        val keywords = command.lowercase().split(" ")
        return inventory.filter { item ->
            keywords.any { keyword ->
                item.name.lowercase().contains(keyword) ||
                item.partNumber.lowercase().contains(keyword) ||
                item.category.lowercase().contains(keyword)
            }
        }.take(5)
    }
    
    private fun parseItemFromResponse(response: String): InventoryItem {
        // Simple parsing logic - can be enhanced with more sophisticated NLP
        return InventoryItem(
            name = extractField(response, "name") ?: "Unknown Item",
            partNumber = extractField(response, "partNumber") ?: "",
            quantity = extractField(response, "quantity")?.toIntOrNull() ?: 1,
            location = extractField(response, "location") ?: "",
            price = extractField(response, "price")?.toDoubleOrNull() ?: 0.0,
            category = extractField(response, "category") ?: "other"
        )
    }
    
    private fun extractField(text: String, field: String): String? {
        val regex = "$field[:\\s=]+([^,\\n]+)".toRegex(RegexOption.IGNORE_CASE)
        return regex.find(text)?.groupValues?.get(1)?.trim()
    }
}

data class AIResponse(
    val intent: AIIntent,
    val responseText: String,
    val relevantItems: List<InventoryItem>,
    val suggestedAction: SuggestedAction?
)

enum class AIIntent {
    SEARCH,
    ADD,
    UPDATE,
    DELETE,
    CHECK_STOCK,
    GENERAL_QUERY
}

data class SuggestedAction(
    val type: String,
    val parameters: Map<String, Any>
)
