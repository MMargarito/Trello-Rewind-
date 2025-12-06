# Kanhero - Android Kanban Board App

A delightful, minimalist Kanban board application for Android with smooth animations, tactile feedback, and an intuitive user experience.

## 🎯 Features

- **3-Column Kanban Board**: TO DO → DOING → DONE
- **Smooth Animations**: Beautiful card transitions and confetti celebrations
- **Tactile Feedback**: Haptic vibrations and sound effects for all interactions
- **Offline-First**: All data stored locally with Room database
- **Simple Monetization**: Free tier (3 boards) + $10 lifetime unlock for unlimited boards
- **Modern UI**: Built with Jetpack Compose and Material Design 3

## 🏗️ Technical Architecture

### Tech Stack

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Billing**: Google Play Billing Library
- **Animations**: Compose Animations + Custom Confetti
- **Haptics**: Android Vibrator API
- **Sounds**: SoundPool API

### Project Structure

```
/app
  /ui
    /boards          → BoardListScreen, BoardScreen, CardItem
    /settings        → SettingsScreen
    /theme           → Color, Type, Theme
  /data
    /entity          → BoardEntity, CardEntity
    /dao             → BoardDao, CardDao
    AppDatabase.kt   → Room database configuration
  /viewmodel         → BoardViewModel, CardViewModel, BillingViewModel
  /interaction       → HapticsManager, SoundManager, ConfettiManager
  /billing           → BillingClientWrapper
  MainActivity.kt    → Main entry point
```

### Database Schema

**Board**
- `id`: Int (Primary Key)
- `title`: String
- `createdAt`: Long

**Card**
- `id`: Int (Primary Key)
- `boardId`: Int (Foreign Key → Board)
- `text`: String
- `status`: Enum (TODO | DOING | DONE)
- `position`: Int (for ordering)
- `createdAt`: Long

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 34
- JDK 17
- Gradle 8.2+

### Building the App

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd "Kanhero"
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button (Shift+F10)

### Building APK/Bundle

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing configuration)
./gradlew assembleRelease

# Android App Bundle (for Play Store)
./gradlew bundleRelease
```

## 📱 App Features in Detail

### Board Management
- Create unlimited boards (with premium unlock)
- Delete boards with confirmation
- Navigate between boards easily

### Card Management
- Create cards in any column (TODO, DOING, DONE)
- **Drag & drop cards between columns** ✅ **NEW!**
  - Long-press any card to start dragging
  - Visual feedback with floating overlay
  - Column highlighting when hovering
  - Smooth transitions between statuses
- **Edit cards after creation** ✅ **NEW!**
  - Click card or edit button to modify text
  - Real-time character counter
  - Smart validation and change detection
- Delete cards with confirmation
- Visual feedback for all actions

### Interaction Features
- **Haptics**: Light tap on drag, medium tap on drop, success tap on completion
- **Sounds**: Professional audio feedback for all actions ✅ **NEW!**
  - Card pickup/drag sounds
  - Card drop sounds
  - Task completion celebration sound
  - Card and board creation sounds
- **Confetti**: Celebration animation when moving cards to DONE
- **Settings**: Toggle haptics, sounds, and animations on/off

### Monetization
- **Free Tier**: Up to 3 boards
- **Premium**: One-time $10 payment for unlimited boards
- Google Play Billing integration
- Purchase restoration support

## 🎨 Customization

### Colors
Edit `app/src/main/java/com/trellorewind/app/ui/theme/Color.kt` to customize:
- Column background colors
- Card colors
- Theme colors

### Animations
Modify `ConfettiManager.kt` to adjust:
- Particle count
- Animation duration
- Colors and effects

### Haptics
Configure vibration patterns in `HapticsManager.kt`:
- Intensity levels
- Duration
- Patterns

## 📦 Dependencies

Key dependencies used in this project:

```kotlin
// Jetpack Compose
androidx.compose.material3:material3
androidx.navigation:navigation-compose

// Room Database
androidx.room:room-ktx

// Google Play Billing
com.android.billingclient:billing-ktx

// Lottie (optional)
com.airbnb.android:lottie-compose
```

## 🔧 Configuration

### Google Play Billing Setup

1. Create a product in Google Play Console
2. Product ID: `unlimited_boards_lifetime`
3. Product Type: In-app product (one-time purchase)
4. Price: $10.00 USD

### Adding Sound Files

1. Create directory: `app/src/main/res/raw/`
2. Add `.wav` sound files:
   - `card_drag.wav`
   - `card_drop.wav`
   - `card_done.wav`
   - `confetti.wav`
3. Update `SoundManager.kt` to load the sounds

## 📝 Development Notes

### Architecture Decisions

1. **MVVM Pattern**: Clean separation of concerns, easy testing
2. **Room Database**: Type-safe, compile-time verification
3. **Jetpack Compose**: Modern, declarative UI with less boilerplate
4. **StateFlow**: Reactive data flow, lifecycle-aware

### Future Enhancements

- [x] **Drag & drop card reordering** ✅ **IMPLEMENTED!**
- [x] **Card editing functionality** ✅ **IMPLEMENTED!**
- [ ] Board templates
- [ ] Export/import boards
- [ ] Dark mode toggle
- [ ] Cloud sync (optional)
- [ ] Collaborative boards

## 🐛 Known Issues

- None! All core features are implemented and working ✅

## 📄 License

This project is proprietary software for Kanhero.

## 👨‍💻 Development

**Minimum SDK**: 26 (Android 8.0)  
**Target SDK**: 34 (Android 14)  
**Compile SDK**: 34

## 🤝 Contributing

This is a private project. For questions or issues, contact the development team.

---

**Built with ❤️ using Jetpack Compose**

