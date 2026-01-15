# Vibe Inventory - Test Suite

## Overview
This test suite provides comprehensive coverage for the Vibe Inventory Android application.

## Test Structure

### Unit Tests (\pp/src/test\)
Located in \pp/src/test/kotlin/com/vibeinventory/motorparts/\

#### ViewModel Tests
- **InventoryViewModelTest** - Tests inventory management logic
  - Loading inventory items
  - Adding, updating, deleting items
  - Search functionality
  - Error handling
  
- **VoiceCommandViewModelTest** - Tests voice command processing
  - AI command processing
  - Voice input handling
  - Response management
  
- **AnalyticsViewModelTest** - Tests analytics functionality
  - Loading analytics data
  - Error handling

#### Repository Tests
- **InventoryRepositoryTest** - Tests data layer (requires Firebase Test SDK for full functionality)
  - CRUD operations structure
  - Firestore integration patterns

#### AI Service Tests
- **GeminiAIServiceTest** - Tests AI integration
  - Command intent detection
  - Response parsing
  - Context building

#### Utility Tests
- **CsvExporterTest** - Tests CSV export functionality
- **CsvImporterTest** - Tests CSV import functionality

### Instrumented Tests (\pp/src/androidTest\)
Located in \pp/src/androidTest/kotlin/com/vibeinventory/motorparts/\

#### UI Tests
- **InventoryListScreenTest** - Tests Compose UI
  - Item display
  - Loading states
  - Error states
  - Search interaction

## Running Tests

### Run All Unit Tests
\\\ash
./gradlew test
\\\

### Run Specific Test Class
\\\ash
./gradlew test --tests "com.vibeinventory.motorparts.viewmodel.InventoryViewModelTest"
\\\

### Run All Instrumented Tests (requires emulator/device)
\\\ash
./gradlew connectedAndroidTest
\\\

### Run Tests with Coverage
\\\ash
./gradlew testDebugUnitTest jacocoTestReport
\\\

## Test Dependencies

### Unit Testing
- **JUnit 4** - Testing framework
- **MockK** - Mocking library for Kotlin
- **Kotlinx Coroutines Test** - Testing coroutines
- **Turbine** - Flow testing library
- **Truth** - Assertion library
- **AndroidX Core Testing** - Architecture components testing

### Instrumented Testing
- **Compose UI Test** - Testing Jetpack Compose UIs
- **Espresso** - UI testing framework
- **AndroidX Test** - Android testing utilities

## Test Coverage

Current test coverage includes:
- ? ViewModels (100% of main ViewModels)
- ? AI Service (structural tests)
- ? Utility classes (CSV import/export)
- ? UI Components (basic Compose tests)
- ?? Repository layer (requires Firebase Test SDK for full coverage)

## Notes

### Firebase Testing
Repository tests are structural only. For full Firebase testing:
1. Add Firebase Test SDK
2. Use Firebase Emulator Suite
3. Update tests with proper Firebase mocking

### Gemini AI Testing
AI service tests are structural. Real API testing requires:
1. Valid API key in test environment
2. Network connectivity
3. Consider using recorded responses for consistent testing

## CI/CD Integration

To integrate with CI/CD:
\\\yaml
# Example GitHub Actions
- name: Run Unit Tests
  run: ./gradlew test
  
- name: Run Instrumented Tests
  run: ./gradlew connectedAndroidTest
\\\

## Best Practices

1. **Keep tests fast** - Mock external dependencies
2. **Test behavior, not implementation** - Focus on what, not how
3. **Use descriptive test names** - Use backticks for readable names
4. **Isolate tests** - Each test should be independent
5. **Follow AAA pattern** - Arrange, Act, Assert

## Adding New Tests

When adding new features:
1. Write tests first (TDD approach)
2. Follow existing test patterns
3. Maintain test coverage above 80%
4. Update this README with new test classes

## Troubleshooting

### Tests not running?
- Sync Gradle files
- Clean project: \./gradlew clean\
- Invalidate caches in Android Studio

### MockK errors?
- Ensure MockK version is compatible with Kotlin version
- Use \elaxed = true\ for basic mocking

### Compose test errors?
- Ensure emulator/device is running for instrumented tests
- Check Compose BOM version compatibility

## Resources

- [Android Testing Documentation](https://developer.android.com/training/testing)
- [MockK Documentation](https://mockk.io/)
- [Compose Testing Guide](https://developer.android.com/jetpack/compose/testing)
