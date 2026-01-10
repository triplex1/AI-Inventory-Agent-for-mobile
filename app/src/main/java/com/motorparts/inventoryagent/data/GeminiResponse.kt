package com.motorparts.inventoryagent.data

import com.google.gson.annotations.SerializedName

data class GeminiResponse(
    @SerializedName("action")
    val action: String = "UNKNOWN",
    
    @SerializedName("part_name")
    val partName: String? = null,
    
    @SerializedName("part_number")
    val partNumber: String? = null,
    
    @SerializedName("quantity")
    val quantity: Int? = null,
    
    @SerializedName("price")
    val price: Double? = null,
    
    @SerializedName("response_text")
    val responseText: String = ""
)

enum class VoiceAction {
    UPDATE_STOCK,
    QUERY_PRICE,
    UPDATE_PRICE,
    CHECK_STOCK,
    UNKNOWN
}
