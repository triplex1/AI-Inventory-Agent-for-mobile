package com.vibeinventory.motorparts.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vibeinventory.motorparts.data.model.InventoryItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InventoryRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val inventoryCollection = firestore.collection("inventory")
    
    /**
     * Get all inventory items as a Flow
     */
    fun getAllInventoryItems(): Flow<Result<List<InventoryItem>>> = callbackFlow {
        val subscription = inventoryCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(InventoryItem::class.java)?.copy(id = doc.id)
                }
                trySend(Result.success(items))
            }
        }
        
        awaitClose { subscription.remove() }
    }
    
    /**
     * Get a single inventory item by ID
     */
    suspend fun getInventoryItem(id: String): Result<InventoryItem?> {
        return try {
            val document = inventoryCollection.document(id).get().await()
            val item = document.toObject(InventoryItem::class.java)?.copy(id = document.id)
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add a new inventory item
     */
    suspend fun addInventoryItem(item: InventoryItem): Result<String> {
        return try {
            val docRef = inventoryCollection.add(item).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing inventory item
     */
    suspend fun updateInventoryItem(item: InventoryItem): Result<Unit> {
        return try {
            if (item.id.isEmpty()) {
                return Result.failure(IllegalArgumentException("Item ID cannot be empty"))
            }
            inventoryCollection.document(item.id).set(item).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete an inventory item
     */
    suspend fun deleteInventoryItem(id: String): Result<Unit> {
        return try {
            inventoryCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Search inventory items by query
     */
    suspend fun searchInventoryItems(query: String): Result<List<InventoryItem>> {
        return try {
            val snapshot = inventoryCollection
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            
            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(InventoryItem::class.java)?.copy(id = doc.id)
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update item quantity
     */
    suspend fun updateQuantity(id: String, newQuantity: Int): Result<Unit> {
        return try {
            inventoryCollection.document(id)
                .update("quantity", newQuantity)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
