# Vibe Inventory - Motorparts Management System

<div align="center">

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2034-green.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-orange.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern Android app built with Kotlin and Jetpack Compose for managing motorparts inventory using AI-powered voice commands.

[Features](#-features) • [Setup](#-setup-instructions) • [Documentation](#-documentation) • [Contributing](#-contributing)

</div>

---

## 📑 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Setup Instructions](#-setup-instructions)
- [App Screens](#-app-screens)
- [Voice Commands](#-voice-command-examples)
- [Testing](#-testing)
- [Documentation](#-documentation)
- [Security](#-security-notes)
- [Roadmap](#-roadmap--future-enhancements)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

## 🚀 Features

- **📱 Modern UI**: Clean, dark-mode industrial aesthetic with Material Design 3
- **📦 Inventory Forms**: Add and edit inventory items with a simple form UI
- **🎤 Voice Commands**: Natural language inventory management powered by Gemini AI
- **☁️ Cloud Sync**: Real-time inventory synchronization with Firebase Firestore
- **🤖 AI Assistant**: Intelligent inventory insights and suggestions
- **📊 Analytics Dashboard**: Comprehensive inventory analytics with visualizations
- **📄 Report Generation**: Export PDF reports and CSV files
- **📥 CSV Import/Export**: Bulk import and export inventory data
- **🔍 Smart Search**: Quick part lookup and filtering
- **⚡ Real-time Updates**: Live inventory tracking and updates
- **🧪 Comprehensive Testing**: Unit tests for ViewModels, Repositories, and Utils

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin 1.9.0+
- **UI Framework**: Jetpack Compose 1.5.0+
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Firebase Firestore
- **AI**: Google Gemini 1.5 Flash API
- **Navigation**: Jetpack Navigation Compose
- **Async**: Kotlin Coroutines & Flow

### Key Dependencies
- **Firebase**: Firestore for real-time data synchronization
- **Material 3**: Modern Material Design components
- **Accompanist**: Compose utilities and animations
- **Kotlin Coroutines**: Asynchronous programming
- **Kotlin Flow**: Reactive streams
- **AndroidX Lifecycle**: Lifecycle-aware components

## 📁 Project Structure

```
app/src/main/kotlin/com/vibeinventory/motorparts/
├── MainActivity.kt                          # Main entry point
├── ui/
│   ├── theme/                              # App theming
│   │   ├── Color.kt                        # Industrial color palette
│   │   ├── Theme.kt                        # Material 3 theme
│   │   └── Type.kt                         # Typography system
│   ├── navigation/                         # Navigation setup
│   │   ├── NavigationItem.kt              # Nav destinations
│   │   └── MainNavigation.kt              # Nav host & bottom bar
│   └── screens/                            # Feature screens
│       ├── inventory/                      # Inventory management
│       │   └── InventoryListScreen.kt      # Inventory list UI
│       ├── voice/                          # Voice commands
│       │   └── VoiceCommandScreen.kt       # Voice command UI
│       ├── analytics/                      # Analytics dashboard
│       │   └── AnalyticsScreen.kt          # Analytics UI
│       └── settings/                       # App settings
│           └── SettingsScreen.kt           # Settings UI
├── data/
│   ├── model/                              # Data models
│   │   ├── InventoryItem.kt               # Inventory item model
│   │   └── InventoryAnalytics.kt          # Analytics data model
│   └── repository/                         # Data layer
│       ├── InventoryRepository.kt          # Firestore operations
│       └── AnalyticsRepository.kt          # Analytics operations
├── ai/
│   └── GeminiAIService.kt                 # AI service integration
├── speech/
│   └── SpeechRecognitionManager.kt        # Voice recognition
├── utils/                                   # Utilities
│   ├── CsvExporter.kt                     # CSV export functionality
│   ├── CsvImporter.kt                     # CSV import functionality
│   └── PdfReportGenerator.kt              # PDF report generation
└── viewmodel/                              # ViewModels
    ├── InventoryViewModel.kt              # Inventory state management
    ├── VoiceCommandViewModel.kt           # Voice command state
    ├── VoiceCommandViewModelFactory.kt    # ViewModel factory
    └── AnalyticsViewModel.kt              # Analytics state management

app/src/test/kotlin/.../                    # Unit tests
├── AllTestsSuite.kt                        # Test suite runner
├── viewmodel/                              # ViewModel tests
├── data/repository/                        # Repository tests
├── ai/                                     # AI service tests
└── utils/                                  # Utility tests

app/src/androidTest/kotlin/.../            # Android UI tests
└── ui/                                     # UI component tests
```

## 🎨 Design System

### Color Palette (Industrial Dark Mode)
- **Background**: `#0A0A0A` (Industrial Black)
- **Surface**: `#1E1E1E` (Dark Gray)
- **Primary**: `#FF6B35` (Industrial Orange)
- **Secondary**: `#FFA726` (Industrial Amber)
- **Accent**: `#42A5F5` (Industrial Blue)

### Typography
- Sans-serif font family
- Material 3 type scale
- Bold headers, medium body text

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 34
- Firebase account
- Google AI Studio account (for Gemini API)

### Firebase Setup

1. **Create Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use existing one
   - Add Android app with package name: `com.vibeinventory.motorparts`

2. **Download Configuration**:
   - Download `google-services.json`
   - Place it in `app/` directory

3. **Enable Firestore**:
   - In Firebase Console, go to Firestore Database
   - Click "Create Database"
   - Start in test mode (for development)
   - Choose a location

4. **Firestore Security Rules** (for production):
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /inventory/{itemId} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```

### Gemini AI Setup

1. **Get API Key**:
   - Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Create a new API key
   - Copy the key

2. **Add to Project**:
   Create `app/src/main/res/values/secrets.xml`:
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <resources>
       <string name="gemini_api_key">YOUR_API_KEY_HERE</string>
   </resources>
   ```

3. **Initialize in Code**:
   ```kotlin
   val apiKey = getString(R.string.gemini_api_key)
   val aiService = GeminiAIService(apiKey)
   ```

### Build & Run

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/triplex1/AI-Inventory-Agent-for-mobile.git
   cd AI-Inventory-Agent-for-mobile
   ```

2. **Configure API Keys**:
   - Create `app/src/main/res/values/secrets.xml` with your Gemini API key (see [Gemini AI Setup](#gemini-ai-setup) above)
   - Add `google-services.json` to `app/` directory (see [Firebase Setup](#firebase-setup) above)

3. **Sync Gradle**:
   - Open project in Android Studio Hedgehog or newer
   - Wait for Gradle sync to complete
   - Resolve any dependency issues if prompted

4. **Run the App**:
   - Connect an Android device via USB (with USB debugging enabled) or start an emulator
   - Click Run ▶️ button or press `Shift+F10` (Windows/Linux) / `Ctrl+R` (Mac)
   - Select your target device when prompted

5. **Build APK** (Optional):
   ```bash
   ./gradlew assembleDebug
   ```
   APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## 📱 App Screens

### 1. Inventory List
- **View all inventory**: Browse complete motorparts inventory
- **Search & Filter**: Quick part lookup by name, category, or part number
- **Stock Indicators**: Visual status for healthy, low, and out-of-stock items
- **Add Items**: Floating Action Button (FAB) opens the add inventory form
- **Real-time Sync**: Live updates from Firebase Firestore
- **Edit Items**: Tap an item to open the edit form and update details

### 1.1 Inventory Add/Edit Form
- **Create New Items**: Enter name, part number, quantity, location, price, and category
- **Edit Existing Items**: Pre-filled form when opened from an existing item
- **Basic Validation**: Requires name and part number, with inline error feedback
- **Firestore Integration**: Uses `InventoryViewModel` and `InventoryRepository` to persist changes
- **Simple Navigation**: Back navigation returns to the inventory list after save or cancel

### 2. Voice Command
- **Voice-to-Text**: Real-time speech recognition
- **Natural Language Processing**: Understand conversational commands
- **AI-Powered Responses**: Intelligent command interpretation via Gemini AI
- **Execute Operations**: Search, query, and interact with inventory using voice
- **Animated UI**: Interactive microphone button with visual feedback
- **Live Transcription**: See your speech converted to text in real-time

### 3. Analytics Dashboard
- **Summary Cards**: Total items, total value, low stock count, out of stock alerts
- **Stock Visualization**: Bar charts showing inventory health distribution
- **Category Breakdown**: Statistics for each category (item count, total value, alerts)
- **Top Value Items**: List of most valuable inventory items
- **Low Stock Alerts**: Real-time monitoring with color-coded warnings
- **Export Options**: Generate PDF reports or export CSV data

### 4. Settings
- **Account Management**: User profile and preferences
- **Notification Preferences**: Configure alerts for low stock
- **AI Configuration**: Customize Gemini AI settings
- **Voice Settings**: Adjust speech recognition parameters
- **Database Sync**: Manage Firestore synchronization
- **Backup & Restore**: Import/export inventory data
- **App Preferences**: Theme, language, and display options

## 🎤 Voice Command Examples

### ✅ Working Commands (Implemented)
- "Show me all brake pads in stock"
- "How many spark plugs do we have?"
- "What items are low on stock?"
- "Find parts in location A-12"
- "Check oil filter inventory"
- "Search for brake components"

### 🚧 Coming Soon
- "Add 10 oil filters to location B-05" (requires inventory form)
- "Update brake pad quantity to 20" (requires edit functionality)

### 💡 Tips for Best Results
- Speak clearly and pause between commands
- Use specific part names or categories for better accuracy
- Include quantities or locations in your queries
- The AI understands context from previous commands

## 🔥 Firebase Collections

### `inventory` Collection
```json
{
  "name": "Brake Pad Set",
  "partNumber": "BP-2024-001",
  "description": "High-performance ceramic brake pads",
  "category": "brake",
  "quantity": 15,
  "minQuantity": 5,
  "location": "A-12",
  "price": 45.99,
  "supplier": "Premium Parts Co.",
  "barcode": "1234567890123",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z",
  "tags": ["brake", "ceramic", "performance"]
}
```

## 🤖 AI Features

1. **Natural Language Understanding**: Parse voice commands into structured actions
2. **Inventory Insights**: Generate smart recommendations
3. **Context-Aware Responses**: AI knows current inventory state
4. **Intent Detection**: Classify commands (search, add, update, delete, check stock)
5. **Smart Extraction**: Parse item details from natural language

## 🧪 Testing

The project includes comprehensive test coverage for core components:

### Unit Tests
- **ViewModels**: Test coverage for `InventoryViewModel`, `VoiceCommandViewModel`, and `AnalyticsViewModel`
- **Repositories**: Test `InventoryRepository` and data layer logic
- **AI Service**: Test `GeminiAIService` integration and command processing
- **Utils**: Test CSV import/export and PDF generation utilities

### Android UI Tests
- **Screen Tests**: UI component testing for `InventoryListScreen`

### Running Tests

**Run all unit tests:**
```bash
./gradlew test
```

**Run Android tests:**
```bash
./gradlew connectedAndroidTest
```

**Run specific test class:**
```bash
./gradlew test --tests "com.vibeinventory.motorparts.viewmodel.InventoryViewModelTest"
```

**View test reports:**
- Unit test reports: `app/build/reports/tests/test/index.html`
- Android test reports: `app/build/reports/androidTests/connected/index.html`

### Test Structure
```
app/src/test/kotlin/com/vibeinventory/motorparts/
├── AllTestsSuite.kt                        # Test suite configuration
├── viewmodel/
│   ├── InventoryViewModelTest.kt
│   ├── VoiceCommandViewModelTest.kt
│   └── AnalyticsViewModelTest.kt
├── data/repository/
│   └── InventoryRepositoryTest.kt
├── ai/
│   └── GeminiAIServiceTest.kt
└── utils/
    ├── CsvExporterTest.kt
    └── CsvImporterTest.kt

app/src/androidTest/kotlin/com/vibeinventory/motorparts/
└── ui/
    └── InventoryListScreenTest.kt
```

## 🔐 Security Notes

- Never commit `google-services.json` to version control
- Keep `secrets.xml` in `.gitignore`
- Use Firebase Authentication in production
- Implement proper Firestore security rules
- Secure API keys using Android Keystore

## 📚 Documentation

Additional documentation files are available in the repository:

- **[SETUP_GUIDE.md](SETUP_GUIDE.md)**: Detailed setup and configuration guide
- **[VOICE_RECOGNITION_GUIDE.md](VOICE_RECOGNITION_GUIDE.md)**: Voice command features and usage
- **[ANALYTICS_FEATURES.md](ANALYTICS_FEATURES.md)**: Analytics, reporting, and data export/import features
- **[inventory_template.csv](inventory_template.csv)**: CSV template for bulk import

## 📝 Roadmap / Future Enhancements

### ✅ Completed Features
- [x] **Voice Recognition** - Real Android Speech Recognizer integration
- [x] **AI Integration** - Full Gemini AI command processing
- [x] **Live Transcription** - Real-time voice-to-text display
- [x] **Permission Handling** - Automatic microphone permission flow
- [x] **Inventory Forms** - Add/edit form for inventory items
- [x] **Analytics Dashboard** - Comprehensive inventory analytics with visualizations
- [x] **CSV Export/Import** - Bulk data import and export functionality
- [x] **PDF Report Generation** - Professional inventory reports
- [x] **Test Suite** - Comprehensive unit and UI tests
- [x] **Low Stock Alerts** - Automated monitoring and notifications

### 🚧 In Progress / Planned
- [ ] **Barcode Scanning** - ML Kit integration for barcode scanning
- [ ] **Offline Mode** - Local caching and sync when connection restored
- [ ] **User Authentication** - Firebase Authentication integration
- [ ] **Multi-warehouse Support** - Manage multiple warehouse locations
- [ ] **Push Notifications** - FCM integration for low stock alerts
- [ ] **Advanced Search** - Enhanced filtering and search capabilities
- [ ] **Inventory History** - Track inventory changes over time
- [ ] **Supplier Management** - Manage supplier information and contacts

## 🐛 Troubleshooting

### Gradle Build Errors

**Issue**: Build fails with JDK version errors
- **Solution**: 
  - Ensure JDK 17 is installed and configured
  - Set `JAVA_HOME` environment variable
  - In Android Studio: File → Project Structure → SDK Location → JDK Location

**Issue**: Gradle sync fails
- **Solution**:
  - Invalidate caches: File → Invalidate Caches / Restart
  - Delete `.gradle` folder in project root
  - Sync project again: File → Sync Project with Gradle Files

**Issue**: Dependency resolution errors
- **Solution**:
  - Clean project: Build → Clean Project
  - Rebuild: Build → Rebuild Project
  - Check internet connection for dependency downloads

### Firebase Connection Issues

**Issue**: App crashes on startup with Firebase errors
- **Solution**:
  - Verify `google-services.json` is in `app/` directory (not in root)
  - Ensure package name in `google-services.json` matches `com.vibeinventory.motorparts`
  - Rebuild project after adding `google-services.json`

**Issue**: Firestore connection timeout
- **Solution**:
  - Check internet connectivity
  - Verify Firebase project is active in Firebase Console
  - Check Firestore database is created and accessible
  - Review security rules in Firestore Console

### AI/Gemini Not Responding

**Issue**: Voice commands not processing
- **Solution**:
  - Verify Gemini API key in `app/src/main/res/values/secrets.xml`
  - Check API key is valid and has quota remaining
  - Ensure internet connectivity
  - Check Logcat for API error messages

**Issue**: Speech recognition not working
- **Solution**:
  - Grant microphone permission when prompted
  - Check device has microphone access enabled in Settings
  - Ensure Google Speech Services are installed/updated on device
  - Test with a different device or emulator

### Runtime Issues

**Issue**: App crashes on specific screens
- **Solution**:
  - Check Logcat for detailed error messages
  - Verify all required dependencies are included
  - Ensure minimum SDK version (API 24+) is met
  - Clear app data: Settings → Apps → Vibe Inventory → Clear Data

**Issue**: CSV import/export fails
- **Solution**:
  - Verify file permissions (Storage permission for Android 11+)
  - Check CSV format matches template
  - Ensure proper file encoding (UTF-8)
  - Review error messages in Logcat

## 💡 Tips & Best Practices

- **Development**: Use Android Studio's built-in emulator for testing voice features
- **Testing**: Run unit tests before making commits to ensure stability
- **Firebase**: Start with Firestore test mode, then implement proper security rules for production
- **API Keys**: Never commit sensitive files - use `.gitignore` for `secrets.xml` and `google-services.json`
- **Performance**: Use Android Profiler to monitor app performance during development

## 📄 License

MIT License - Feel free to use this project for learning and commercial purposes.

## 👨‍💻 Contributing

We welcome contributions! Here's how you can help:

### Getting Started

1. **Fork the repository** and clone your fork
2. **Create a feature branch**: `git checkout -b feature/your-feature-name`
3. **Make your changes** following the existing code style
4. **Write tests** for new functionality
5. **Commit your changes**: `git commit -m "Add: Description of changes"`
6. **Push to your fork**: `git push origin feature/your-feature-name`
7. **Open a Pull Request** with a clear description

### Contribution Guidelines

- Follow Kotlin coding conventions and Android best practices
- Write clear, descriptive commit messages
- Add tests for new features and bug fixes
- Update documentation for user-facing changes
- Ensure all tests pass before submitting PR
- Keep PRs focused and manageable in size

### Reporting Issues

When reporting bugs or requesting features, please include:
- Android version and device information
- Steps to reproduce the issue
- Expected vs actual behavior
- Relevant logs or screenshots

### Code Style

- Use meaningful variable and function names
- Add comments for complex logic
- Follow MVVM architecture patterns
- Use Kotlin coroutines for asynchronous operations
- Prefer Jetpack Compose for UI components

---

<div align="center">

**Built with ❤️ using Kotlin & Jetpack Compose**

Made with dedication for efficient inventory management

[⭐ Star this repo](https://github.com/triplex1/AI-Inventory-Agent-for-mobile) if you find it helpful!

</div>
