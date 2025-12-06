# Kanhero - Release Build Instructions

## 🔐 Step 1: Generate a Keystore (Using Android Studio)

The easiest way is to use Android Studio:

1. Open Android Studio
2. Go to **Build** → **Generate Signed Bundle / APK**
3. Select **Android App Bundle** → Click **Next**
4. Click **Create new...** to create a new keystore
5. Fill in the keystore details:
   - **Key store path**: Choose a safe location (e.g., `C:\Users\Manue\kanhero-release.jks`)
   - **Password**: Create a strong password (save this!)
   - **Key alias**: `kanhero`
   - **Key password**: Create a strong password (save this!)
   - **Validity**: 25+ years (9125 days)
   - **Certificate info**: Fill in your details
6. Click **OK** to create the keystore

⚠️ **IMPORTANT**: Save these passwords securely! You'll need them for every update.

---

## 📝 Step 2: Configure Signing (Optional - for command-line builds)

If you want to build from command line, create a file called `keystore.properties` in your project root:

```properties
storeFile=C:/Users/Manue/kanhero-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=kanhero
keyPassword=YOUR_KEY_PASSWORD
```

Then update `app/build.gradle.kts` signing config (already prepared).

⚠️ **Never commit keystore.properties to git!**

---

## 🏗️ Step 3: Build Release Bundle

### Option A: Using Android Studio (Recommended)
1. **Build** → **Generate Signed Bundle / APK**
2. Select **Android App Bundle**
3. Choose your keystore and enter passwords
4. Select **release** build variant
5. Click **Create**
6. Bundle will be at: `app/build/outputs/bundle/release/app-release.aab`

### Option B: Using Command Line
```bash
./gradlew bundleRelease
```

---

## 📱 Step 4: Test Release Build

Before uploading, test the release build:

1. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Install on a real device
3. Test all features:
   - [ ] Create a board
   - [ ] Add cards to all columns
   - [ ] Move cards (buttons and drag)
   - [ ] Edit cards
   - [ ] Delete cards (test undo)
   - [ ] Rename board
   - [ ] Delete board (test undo)
   - [ ] Toggle dark mode
   - [ ] Toggle haptics/sounds/animations
   - [ ] Verify confetti on DONE

---

## 🌐 Step 5: Google Play Console Setup

### Create Developer Account
1. Go to [Google Play Console](https://play.google.com/console)
2. Pay the one-time $25 registration fee
3. Complete identity verification

### Create New App
1. Click **Create app**
2. Fill in:
   - **App name**: Kanhero
   - **Default language**: English (US)
   - **App or game**: App
   - **Free or paid**: Free (with in-app purchases)
3. Accept policies and create

---

## 📝 Step 6: Store Listing

### App Details
Copy these to Play Console:

**Short Description (80 chars):**
```
A delightful Kanban board app with smooth animations and satisfying feedback.
```

**Full Description:**
```
🎯 Kanhero - Be the Hero of Your Tasks!

Transform your productivity with Kanhero, a beautifully designed Kanban board app that makes task management satisfying and fun.

✨ KEY FEATURES:

📋 Simple 3-Column Workflow
• TO DO - Capture all your tasks
• DOING - Track what you're working on  
• DONE - Celebrate your completions

🎨 Delightful Interactions
• Swipe between columns
• Drag & drop cards with touch
• Satisfying haptic feedback
• Sound effects for every action
• Confetti celebration when completing tasks!

🌙 Modern Design
• Clean, minimalist interface
• Dark mode support
• Smooth animations throughout
• Material Design 3 guidelines

📱 Works Offline
• All data stored locally on your device
• No account required
• Your data stays private

💰 Fair Pricing
• Create up to 3 boards for FREE
• Unlock unlimited boards with a one-time $10 purchase
• No subscriptions, no ads!

Perfect for personal task management, simple project tracking, and daily to-do lists.

Download Kanhero today and make your task management satisfying! 🚀
```

---

## 🖼️ Step 7: Graphics Assets

### Required Screenshots
Capture these on a real device or emulator:

1. **Board list** - showing multiple boards
2. **Kanban board** - cards in all columns
3. **Card with move buttons** - showing the UI
4. **Dark mode** - board in dark theme
5. **Settings screen** - showing toggles
6. **Confetti celebration** - card moving to DONE

**Screenshot sizes:**
- Phone: 1080 x 1920 px minimum (or 16:9 ratio)
- Upload 2-8 screenshots

### Feature Graphic
- Size: 1024 x 500 px
- Create in Canva, Figma, or similar
- Show app name and key visual

### App Icon
- Already in: `icon-pack/android/playstore-icon.png`
- Size: 512 x 512 px

---

## 📜 Step 8: Privacy Policy

You MUST have a privacy policy URL. Options:

### Option A: GitHub Pages (Free)
1. Create a repository on GitHub
2. Add a `privacy-policy.md` file
3. Enable GitHub Pages in settings
4. URL will be: `https://yourusername.github.io/repo/privacy-policy`

### Option B: Google Sites (Free)
1. Go to [Google Sites](https://sites.google.com)
2. Create a new site
3. Add your privacy policy text
4. Publish and copy the URL

### Privacy Policy Template:
```
KANHERO PRIVACY POLICY
Last Updated: [DATE]

1. DATA COLLECTION
Kanhero does not collect any personal information. All data is stored locally on your device.

2. DATA STORAGE  
Your boards and cards are stored in a local database on your device. We never access or transmit this data.

3. THIRD-PARTY SERVICES
• Google Play Billing: Used only for processing in-app purchases

4. PERMISSIONS
• Vibration: For haptic feedback (optional)
• Internet: Required for Google Play Billing

5. CONTACT
Email: [YOUR EMAIL]
```

---

## 💰 Step 9: In-App Product Setup

In Play Console → Monetization → In-app products:

1. Click **Create product**
2. Fill in:
   - **Product ID**: `unlimited_boards_lifetime`
   - **Name**: Unlimited Boards
   - **Description**: Create unlimited boards - one-time purchase, lifetime access
   - **Price**: $9.99 USD
3. **Activate** the product

---

## ✅ Step 10: Content Rating

Complete the questionnaire in Play Console:

| Question | Answer |
|----------|--------|
| Violence | No |
| Sexual Content | No |
| Language | No |
| Controlled Substances | No |
| User-Generated Content | No |
| Personal Information | No |
| In-App Purchases | Yes |

**Expected Rating**: Everyone (E)

---

## 🎯 Step 11: Target Audience

In Play Console → Policy → App content:

- **Target age**: All ages
- **Appeals to children**: No (productivity app)
- **Contains ads**: No

---

## 🚀 Step 12: Release

1. Go to **Production** → **Create new release**
2. Click **Upload** and select your `.aab` file
3. Add release notes:
   ```
   Initial release of Kanhero!
   
   • 3-column Kanban board (TO DO, DOING, DONE)
   • Drag & drop cards between columns
   • Beautiful dark mode
   • Haptic feedback and sound effects
   • Confetti celebrations
   • 3 free boards, unlimited with one-time purchase
   ```
4. Click **Review release**
5. Fix any issues flagged
6. Click **Start rollout to Production**

---

## ⏱️ Timeline

- **Review time**: Usually 1-3 days for new apps
- **First rejection**: Common - just fix issues and resubmit
- **Publication**: Automatic after approval

---

## 📞 Support Info

Add to your store listing:
- **Developer email**: Your email (required)
- **Privacy policy URL**: Your hosted policy (required)
- **Website**: Optional

---

## ✅ Final Checklist

Before submitting:

- [ ] Release bundle built and tested on device
- [ ] All screenshots captured (2-8 phone screenshots)
- [ ] Feature graphic created (1024x500)
- [ ] App icon uploaded (512x512)
- [ ] Privacy policy hosted and accessible
- [ ] Store listing completed
- [ ] Content rating questionnaire done
- [ ] In-app product created and activated
- [ ] Target audience declared

**Good luck with your launch! 🎉**


