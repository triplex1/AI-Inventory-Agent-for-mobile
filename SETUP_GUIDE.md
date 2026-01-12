# 🚀 Quick Setup Guide - Vibe Inventory

Follow these steps to get your motorparts inventory app up and running!

## ⚡ Quick Start (5 Minutes)

### Step 1: Firebase Setup

1. **Create Firebase Project**
   - Go to https://console.firebase.google.com/
   - Click "Add project"
   - Enter project name: `vibe-inventory` (or your choice)
   - Disable Google Analytics (optional for now)
   - Click "Create project"

2. **Add Android App**
   - In Firebase Console, click the Android icon
   - Package name: `com.vibeinventory.motorparts`
   - App nickname: `Vibe Inventory`
   - Click "Register app"

3. **Download Configuration**
   - Download `google-services.json`
   - Copy it to: `app/google-services.json`
   
4. **Enable Firestore**
   - In Firebase Console sidebar, click "Firestore Database"
   - Click "Create database"
   - Choose "Start in test mode" (for development)
   - Select your preferred location
   - Click "Enable"

### Step 2: Gemini AI Setup

1. **Get API Key**
   - Visit: https://makersuite.google.com/app/apikey
   - Sign in with Google account
   - Click "Create API Key"
   - Copy the generated key

2. **Add to Project**
   - Copy `app/src/main/res/values/secrets.xml.example` to `secrets.xml`
   - Open `secrets.xml`
   - Replace `YOUR_GEMINI_API_KEY_HERE` with your actual API key
   - Save the file

   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <resources>
       <string name="gemini_api_key">AIzaSyB... (your actual key)</string>
   </resources>
   ```

### Step 3: Open in Android Studio

1. **Open Project**
   - Launch Android Studio
   - Click "Open an existing project"
   - Navigate to: `vibe_codes_proj/AI inventory agent`
   - Click "OK"

2. **Gradle Sync**
   - Wait for Gradle sync to complete (may take a few minutes)
   - If prompted, accept any SDK updates

3. **Configure JDK**
   - Go to: File → Project Structure → SDK Location
   - Ensure JDK 17 is selected
   - Click "Apply"

### Step 4: Run the App

1. **Start Emulator or Connect Device**
   - **Emulator**: Click device dropdown → "Device Manager" → Start an emulator
   - **Physical Device**: Enable USB debugging and connect via USB

2. **Run Application**
   - Click the green "Run" button ▶️
   - Or press `Shift + F10`
   - Select your device
   - Wait for build and installation

3. **Test the App**
   - App should launch with bottom navigation
   - Navigate between Inventory, Voice, and Settings screens

---

## 📋 Verification Checklist

- [ ] Firebase project created
- [ ] `google-services.json` downloaded and placed in `app/` folder
- [ ] Firestore database enabled in Firebase Console
- [ ] Gemini API key obtained
- [ ] `secrets.xml` created with API key
- [ ] Project opened in Android Studio
- [ ] Gradle sync completed successfully
- [ ] App runs on device/emulator
- [ ] All three screens accessible via bottom navigation

---

## 🎨 What You Should See

### 1. Inventory List Screen
- Dark industrial theme
- Search bar at top
- Sample inventory items (will be empty until you add data)
- Orange floating action button (+ icon)

### 2. Voice Command Screen
- Large microphone button in center
- "Tap to speak" text
- Clean, centered layout

### 3. Settings Screen
- List of settings categories
- Cards for each setting option
- Industrial gray theme

---

## 🔥 Adding Sample Data to Firestore

To see inventory items in the app:

1. **Open Firebase Console**
   - Go to Firestore Database
   - Click "Start collection"
   - Collection ID: `inventory`
   - Click "Next"

2. **Add Document**
   - Document ID: (leave auto-generated)
   - Add fields:
     ```
     name: "Brake Pad Set" (string)
     partNumber: "BP-2024-001" (string)
     quantity: 15 (number)
     location: "A-12" (string)
     price: 45.99 (number)
     category: "brake" (string)
     description: "High-performance ceramic brake pads" (string)
     minQuantity: 5 (number)
     supplier: "Premium Parts Co." (string)
     ```
   - Click "Save"

3. **Add More Items**
   - Repeat for more test data
   - The app will automatically sync and display them!

---

## 🎤 Testing Voice Commands (Coming Soon)

Voice recognition requires additional setup:
- Microphone permissions
- Speech recognition service integration
- Currently shows placeholder UI

For now, the app demonstrates:
- ✅ Complete UI/UX
- ✅ Navigation flow
- ✅ Firestore integration
- ✅ AI service setup
- ⏳ Voice input (needs implementation)

---

## 🐛 Common Issues & Solutions

### Issue: "google-services.json not found"
**Solution**: Ensure the file is in `app/` folder, not `app/src/`

### Issue: "API key not valid"
**Solution**: 
- Check API key has no extra spaces
- Verify key is enabled in Google Cloud Console
- Wait a few minutes for API activation

### Issue: Gradle sync failed
**Solution**:
- Check internet connection
- File → Invalidate Caches → Restart
- Update Gradle plugin if prompted

### Issue: App crashes on launch
**Solution**:
- Check Logcat for error messages
- Verify all dependencies downloaded
- Ensure minSdk 26 device/emulator

---

## 📱 Next Steps

1. **Customize the App**
   - Modify colors in `Color.kt`
   - Add your own inventory categories
   - Adjust price formatting

2. **Add Real Data**
   - Connect to your actual inventory
   - Import CSV data to Firestore
   - Set up data validation rules

3. **Implement Voice**
   - Add Android Speech Recognition
   - Connect to Gemini AI processing
   - Test voice command workflows

4. **Deploy**
   - Add authentication
   - Configure production Firestore rules
   - Generate signed APK

---

## 🆘 Need Help?

- **Firebase Issues**: https://firebase.google.com/support
- **Gemini AI**: https://ai.google.dev/docs
- **Android Studio**: https://developer.android.com/studio/intro

---

**Ready to build! 🎉** Open the project in Android Studio and start customizing!
