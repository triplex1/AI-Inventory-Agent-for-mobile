# Vibe Inventory - Motorparts Management System

A modern Android app built with Kotlin and Jetpack Compose for managing motorparts inventory using AI-powered voice commands.

## 🚀 Features

- **📱 Modern UI**: Clean, dark-mode industrial aesthetic with Material Design 3
- **🎤 Voice Commands**: Natural language inventory management powered by Gemini AI
- **☁️ Cloud Sync**: Real-time inventory synchronization with Firebase Firestore
- **🤖 AI Assistant**: Intelligent inventory insights and suggestions
- **📊 Real-time Updates**: Live inventory tracking and updates
- **🔍 Smart Search**: Quick part lookup and filtering

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Firebase Firestore
- **AI**: Google Gemini 1.5 Flash
- **Navigation**: Jetpack Navigation Compose
- **Async**: Kotlin Coroutines & Flow

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
│       ├── inventory/                      # Inventory list screen
│       ├── voice/                          # Voice command screen
│       └── settings/                       # Settings screen
├── data/
│   ├── model/                              # Data models
│   │   └── InventoryItem.kt               # Inventory item model
│   └── repository/                         # Data layer
│       └── InventoryRepository.kt          # Firestore operations
├── ai/
│   └── GeminiAIService.kt                 # AI service integration
└── viewmodel/                              # ViewModels
    ├── InventoryViewModel.kt              # Inventory state management
    └── VoiceCommandViewModel.kt           # Voice command state
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

1. **Clone/Open Project**:
   ```bash
   cd "vibe_codes_proj/AI inventory agent"
   ```

2. **Sync Gradle**:
   - Open project in Android Studio
   - Let Gradle sync complete

3. **Run**:
   - Connect Android device or start emulator
   - Click Run ▶️ or press Shift+F10

## 📱 App Screens

### 1. Inventory List
- View all motorparts inventory
- Search and filter parts
- Quick stock status indicators
- Add new parts with FAB
- Real-time sync with Firestore

### 2. Voice Command
- Voice-to-text input
- Natural language processing
- AI-powered responses
- Execute inventory operations
- Animated microphone button

### 3. Settings
- Account management
- Notification preferences
- AI configuration
- Voice settings
- Database sync options
- Backup & restore

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

## 🔐 Security Notes

- Never commit `google-services.json` to version control
- Keep `secrets.xml` in `.gitignore`
- Use Firebase Authentication in production
- Implement proper Firestore security rules
- Secure API keys using Android Keystore

## 📝 TODO / Future Enhancements

### ✅ Completed
- [x] **Voice Recognition** - Real Android Speech Recognizer integration
- [x] **AI Integration** - Full Gemini AI command processing
- [x] **Live Transcription** - Real-time voice-to-text display
- [x] **Permission Handling** - Automatic microphone permission flow

### 🚧 In Progress / Next Steps
- [ ] Add inventory item form (add/edit dialog)
- [ ] Add barcode scanning with ML Kit
- [ ] Implement offline mode with local caching
- [ ] Add user authentication
- [ ] Generate PDF reports
- [ ] Multi-warehouse support
- [ ] Push notifications for low stock
- [ ] Analytics dashboard
- [ ] Export/import CSV functionality

## 🐛 Troubleshooting

### Gradle Build Errors
- Ensure JDK 17 is configured
- Sync project with Gradle files
- Clean and rebuild project

### Firebase Connection Issues
- Verify `google-services.json` is in `app/` folder
- Check internet connection
- Confirm Firebase project is active

### AI Not Responding
- Verify Gemini API key is valid
- Check API quota limits
- Ensure internet connectivity

## 📄 License

MIT License - Feel free to use this project for learning and commercial purposes.

## 👨‍💻 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

---

**Built with ❤️ using Kotlin & Jetpack Compose**
