package com.motorparts.inventoryagent.data

data class MotorPart(
    val id: String = "",
    val name: String = "",
    val partNumber: String = "",
    val stockQuantity: Int = 0,
    val price: Double = 0.0,
    val description: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "partNumber" to partNumber,
            "stockQuantity" to stockQuantity,
            "price" to price,
            "description" to description
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any>): MotorPart {
            return MotorPart(
                id = id,
                name = map["name"] as? String ?: "",
                partNumber = map["partNumber"] as? String ?: "",
                stockQuantity = (map["stockQuantity"] as? Long)?.toInt() ?: 0,
                price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                description = map["description"] as? String ?: ""
            )
        }
    }
}
