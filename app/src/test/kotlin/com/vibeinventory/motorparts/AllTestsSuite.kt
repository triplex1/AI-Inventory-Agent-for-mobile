package com.vibeinventory.motorparts

import com.vibeinventory.motorparts.ai.GeminiAIServiceTest
import com.vibeinventory.motorparts.data.repository.InventoryRepositoryTest
import com.vibeinventory.motorparts.utils.CsvExporterTest
import com.vibeinventory.motorparts.utils.CsvImporterTest
import com.vibeinventory.motorparts.viewmodel.AnalyticsViewModelTest
import com.vibeinventory.motorparts.viewmodel.InventoryViewModelTest
import com.vibeinventory.motorparts.viewmodel.VoiceCommandViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test Suite for Vibe Inventory App
 * Runs all unit tests together
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    InventoryViewModelTest::class,
    VoiceCommandViewModelTest::class,
    AnalyticsViewModelTest::class,
    InventoryRepositoryTest::class,
    GeminiAIServiceTest::class,
    CsvExporterTest::class,
    CsvImporterTest::class
)
class AllTestsSuite
