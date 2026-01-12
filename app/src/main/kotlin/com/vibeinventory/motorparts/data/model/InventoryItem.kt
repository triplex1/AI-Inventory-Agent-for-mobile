package com.vibeinventory.motorparts.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class InventoryItem(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val partNumber: String = "",
    val description: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val minQuantity: Int = 5,
    val location: String = "",
    val price: Double = 0.0,
    val supplier: String = "",
    val barcode: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
    val tags: List<String> = emptyList()
)

enum class InventoryCategory(val displayName: String) {
    ENGINE("Engine Parts"),
    BRAKE("Brake System"),
    SUSPENSION("Suspension"),
    ELECTRICAL("Electrical"),
    TRANSMISSION("Transmission"),
    BODY("Body Parts"),
    ACCESSORIES("Accessories"),
    FLUIDS("Fluids & Lubricants"),
    OTHER("Other")
}
