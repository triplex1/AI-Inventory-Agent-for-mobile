package com.vibeinventory.motorparts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vibeinventory.motorparts.ai.AIIntent
import com.vibeinventory.motorparts.ai.AIResponse
import com.vibeinventory.motorparts.ai.GeminiAIService
import com.vibeinventory.motorparts.data.model.InventoryItem
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class VoiceCommandViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: VoiceCommandViewModel
    private lateinit var mockAIService: GeminiAIService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockAIService = mockk(relaxed = true)
        viewModel = VoiceCommandViewModel(mockAIService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state should be correct`() {
        assertFalse(viewModel.isListening.value)
        assertTrue(viewModel.voiceInput.value.isEmpty())
        assertNull(viewModel.aiResponse.value)
        assertFalse(viewModel.isProcessing.value)
    }

    @Test
    fun `processVoiceCommand should handle successful AI response`() = runTest {
        // Given
        val command = "Add 10 oil filters"
        val mockResponse = AIResponse(
            intent = AIIntent.ADD,
            responseText = "I'll add 10 oil filters to inventory",
            relevantItems = emptyList(),
            suggestedAction = null
        )
        coEvery { 
            mockAIService.processInventoryCommand(any(), any()) 
        } returns Result.success(mockResponse)

        // When
        viewModel.processVoiceCommand(command, emptyList())
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(mockResponse, viewModel.aiResponse.value)
        assertFalse(viewModel.isProcessing.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `processVoiceCommand should handle error`() = runTest {
        // Given
        val command = "Search for brakes"
        val errorMessage = "AI service error"
        coEvery { 
            mockAIService.processInventoryCommand(any(), any()) 
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.processVoiceCommand(command, emptyList())
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isProcessing.value)
        assertNull(viewModel.aiResponse.value)
    }

    @Test
    fun `setVoiceInput should update voice input state`() {
        // Given
        val input = "Check brake pad stock"

        // When
        viewModel.setVoiceInput(input)

        // Then
        assertEquals(input, viewModel.voiceInput.value)
    }

    @Test
    fun `clearResponse should reset AI response`() {
        // When
        viewModel.clearResponse()

        // Then
        assertNull(viewModel.aiResponse.value)
    }

    @Test
    fun `clearError should reset error state`() {
        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.error.value)
    }
}
