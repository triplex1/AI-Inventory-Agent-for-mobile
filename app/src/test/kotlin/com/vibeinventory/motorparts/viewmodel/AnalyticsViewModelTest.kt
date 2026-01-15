package com.vibeinventory.motorparts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vibeinventory.motorparts.data.model.InventoryAnalytics
import com.vibeinventory.motorparts.data.repository.AnalyticsRepository
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
class AnalyticsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AnalyticsViewModel
    private lateinit var mockRepository: AnalyticsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        viewModel = AnalyticsViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state should be null analytics`() {
        assertNull(viewModel.analytics.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadAnalytics should update analytics successfully`() = runTest {
        // Given
        val testAnalytics = InventoryAnalytics(
            totalItems = 100,
            totalValue = 5000.0,
            lowStockItems = 5,
            categoryCounts = mapOf("Engine" to 30, "Brake" to 20)
        )
        coEvery { mockRepository.getAnalytics() } returns flowOf(Result.success(testAnalytics))

        // When
        viewModel.loadAnalytics()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(testAnalytics, viewModel.analytics.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadAnalytics should handle error`() = runTest {
        // Given
        val errorMessage = "Failed to load analytics"
        coEvery { mockRepository.getAnalytics() } returns 
            flowOf(Result.failure(Exception(errorMessage)))

        // When
        viewModel.loadAnalytics()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }
}
