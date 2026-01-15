package com.vibeinventory.motorparts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vibeinventory.motorparts.ai.GeminiAIService
import com.vibeinventory.motorparts.data.model.InventoryItem
import com.vibeinventory.motorparts.data.repository.InventoryRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: InventoryViewModel
    private lateinit var mockRepository: InventoryRepository
    private lateinit var mockAIService: GeminiAIService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        mockAIService = mockk(relaxed = true)
        viewModel = InventoryViewModel(mockRepository, mockAIService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state should be empty list`() {
        assertEquals(emptyList<InventoryItem>(), viewModel.inventoryItems.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadInventoryItems should update items successfully`() = runTest {
        // Given
        val testItems = listOf(
            InventoryItem(
                id = "1",
                name = "Oil Filter",
                partNumber = "OF-001",
                quantity = 10,
                price = 15.99
            ),
            InventoryItem(
                id = "2",
                name = "Brake Pad",
                partNumber = "BP-002",
                quantity = 5,
                price = 45.99
            )
        )
        coEvery { mockRepository.getAllInventoryItems() } returns flowOf(Result.success(testItems))

        // When
        viewModel.loadInventoryItems()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(testItems, viewModel.inventoryItems.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadInventoryItems should handle error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockRepository.getAllInventoryItems() } returns 
            flowOf(Result.failure(Exception(errorMessage)))

        // When
        viewModel.loadInventoryItems()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `addInventoryItem should call repository`() = runTest {
        // Given
        val newItem = InventoryItem(
            name = "Spark Plug",
            partNumber = "SP-003",
            quantity = 20,
            price = 8.99
        )
        coEvery { mockRepository.addInventoryItem(any()) } returns Result.success(Unit)

        // When
        viewModel.addInventoryItem(newItem)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.addInventoryItem(newItem) }
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `updateInventoryItem should call repository`() = runTest {
        // Given
        val updatedItem = InventoryItem(
            id = "1",
            name = "Updated Filter",
            partNumber = "OF-001",
            quantity = 15,
            price = 18.99
        )
        coEvery { mockRepository.updateInventoryItem(any()) } returns Result.success(Unit)

        // When
        viewModel.updateInventoryItem(updatedItem)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.updateInventoryItem(updatedItem) }
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `deleteInventoryItem should call repository`() = runTest {
        // Given
        val itemId = "1"
        coEvery { mockRepository.deleteInventoryItem(any()) } returns Result.success(Unit)

        // When
        viewModel.deleteInventoryItem(itemId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.deleteInventoryItem(itemId) }
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `updateQuantity should call repository`() = runTest {
        // Given
        val itemId = "1"
        val newQuantity = 25
        coEvery { mockRepository.updateQuantity(any(), any()) } returns Result.success(Unit)

        // When
        viewModel.updateQuantity(itemId, newQuantity)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.updateQuantity(itemId, newQuantity) }
    }

    @Test
    fun `searchInventoryItems should filter items by query`() = runTest {
        // Given
        val testItems = listOf(
            InventoryItem(id = "1", name = "Oil Filter", partNumber = "OF-001"),
            InventoryItem(id = "2", name = "Brake Pad", partNumber = "BP-002"),
            InventoryItem(id = "3", name = "Oil Pan", partNumber = "OP-003")
        )
        coEvery { mockRepository.getAllInventoryItems() } returns flowOf(Result.success(testItems))
        viewModel.loadInventoryItems()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.searchInventoryItems("oil")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val filteredItems = viewModel.filteredInventoryItems.value
        assertEquals(2, filteredItems.size)
        assertTrue(filteredItems.all { it.name.contains("Oil", ignoreCase = true) })
    }

    @Test
    fun `clearError should reset error state`() {
        // Given
        viewModel.clearError()

        // Then
        assertNull(viewModel.error.value)
    }
}
