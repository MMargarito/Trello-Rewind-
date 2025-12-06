# Kanhero - Google Play Store Submission Guide

## 📋 Pre-Submission Checklist

### ✅ Technical Requirements (Completed)

- [x] **Target SDK**: 34 (Android 14) - Required for new apps
- [x] **Min SDK**: 26 (Android 8.0) - Good device coverage
- [x] **Code Shrinking**: R8 enabled for release builds
- [x] **ProGuard Rules**: Configured for Room, Billing, Compose
- [x] **Backup Rules**: Data extraction rules for Android 12+
- [x] **App Bundle Support**: Configured for optimized delivery
- [x] **Permissions**: Only necessary permissions declared

### 🔐 App Signing Setup (Required Before Release)

1. **Generate a Keystore** (if you don't have one):
   ```bash
   keytool -genkey -v -keystore kanhero-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias kanhero
   ```

2. **Configure signing in `app/build.gradle.kts`**:
   - Uncomment the signing config section
   - Add your keystore details
   - **IMPORTANT**: Never commit your keystore or passwords to git!

3. **Use Google Play App Signing** (Recommended):
   - Let Google manage your app signing key
   - Upload your upload key to Play Console
   - More secure and enables key recovery

---

## 📝 Play Console Requirements

### Store Listing Information

#### App Details
| Field | Value |
|-------|-------|
| **App Name** | Kanhero |
| **Short Description** (80 chars max) | A delightful Kanban board app with smooth animations and satisfying feedback. |
| **Full Description** | See below |

#### Full Description (4000 chars max)
```
🎯 Kanhero - Be the Hero of Your Tasks!

Transform your productivity with Kanhero, a beautifully designed Kanban board app that makes task management satisfying and fun.

✨ KEY FEATURES:

📋 Simple 3-Column Workflow
• TO DO - Capture all your tasks
• DOING - Track what you're working on
• DONE - Celebrate your completions

🎨 Delightful Interactions
• Drag & drop cards between columns
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

🔧 Customizable
• Toggle haptic feedback on/off
• Enable/disable sound effects
• Control animations in settings

Perfect for:
• Personal task management
• Simple project tracking
• Daily to-do lists
• Goal tracking

Download Kanhero today and make your task management satisfying! 🚀
```

---

### Graphics Assets Required

#### Screenshots (Required)
| Type | Size | Quantity |
|------|------|----------|
| Phone | 1080 x 1920 px (or 16:9) | 2-8 screenshots |
| 7" Tablet | 1200 x 1920 px | Optional |
| 10" Tablet | 1600 x 2560 px | Optional |

**Recommended Screenshots:**
1. Board list screen (showing multiple boards)
2. Kanban board with cards in all columns
3. Drag & drop action
4. Card editing dialog
5. Settings screen with dark mode toggle
6. Dark mode view of the board
7. Confetti celebration when completing a task
8. Premium unlock dialog

#### Feature Graphic (Required)
- Size: 1024 x 500 px
- Use your icon-pack's `playstore-icon.png` as reference

#### App Icon
- Size: 512 x 512 px
- Already in: `icon-pack/android/playstore-icon.png`

---

### Content Rating

**Complete the questionnaire with these answers:**

| Question | Answer |
|----------|--------|
| Violence | No |
| Sexual Content | No |
| Language | No |
| Controlled Substances | No |
| User-Generated Content | No (local only) |
| Personal Information | No |
| Location Sharing | No |
| In-App Purchases | Yes ($10 one-time) |

**Expected Rating**: Everyone (E)

---

### Privacy Policy (Required)

You need a privacy policy URL. Here's a template:

```
KANHERO PRIVACY POLICY

Last Updated: [DATE]

1. INFORMATION WE COLLECT
Kanhero does not collect any personal information. All your data (boards, cards, preferences) is stored locally on your device.

2. DATA STORAGE
• All data is stored locally using Android's Room database
• Your data never leaves your device
• We do not have access to your tasks or boards

3. THIRD-PARTY SERVICES
• Google Play Billing: Used only for processing in-app purchases
• No analytics or tracking services are used

4. PERMISSIONS
• Vibration: For haptic feedback (optional, can be disabled)
• Billing: For processing in-app purchases
• Internet: Required for Google Play Billing verification

5. DATA BACKUP
• Android Auto Backup may back up your app data to Google Drive
• This is controlled by your device settings

6. CHILDREN'S PRIVACY
Kanhero does not knowingly collect information from children under 13.

7. CHANGES TO THIS POLICY
We may update this policy. Changes will be posted here.

8. CONTACT
For questions: [YOUR EMAIL]
```

**Host this on:**
- GitHub Pages (free)
- Your website
- Google Sites (free)

---

### In-App Products Setup

Create this product in Play Console → Monetization → In-app products:

| Field | Value |
|-------|-------|
| Product ID | `unlimited_boards_lifetime` |
| Name | Unlimited Boards |
| Description | Unlock unlimited boards - one-time purchase |
| Price | $9.99 USD |
| Product Type | Managed product (one-time) |

---

## 🚀 Build & Release

### Generate Release Bundle

```bash
# Clean build
./gradlew clean

# Generate release bundle
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

### Before Uploading

1. **Test the release build** on a real device
2. **Verify all features work** with minification enabled
3. **Test in-app purchase** using a test account
4. **Check for crashes** in the release build

---

## 📊 Target Audience & Content

| Field | Selection |
|-------|-----------|
| Target Age | All ages |
| Category | Productivity |
| Tags | Kanban, Tasks, To-Do, Productivity, Organization |
| Contains Ads | No |
| In-App Purchases | Yes |

---

## ⚠️ Common Rejection Reasons to Avoid

1. **Missing Privacy Policy** - Add the URL in Store Listing
2. **Broken In-App Purchases** - Test thoroughly
3. **Crashes on Launch** - Test release build on multiple devices
4. **Misleading Screenshots** - Use actual app screenshots
5. **Missing Permissions Explanation** - All permissions are justified

---

## 📅 Release Timeline

1. **Day 1**: Submit for review
2. **Day 2-7**: Google review (typically 1-3 days for new apps)
3. **Day 7+**: Published (if approved)

---

## 📞 Support Information

Add to your Store Listing:
- **Developer Email**: [Your email]
- **Website**: [Optional]
- **Privacy Policy URL**: [Required]

---

## ✅ Final Checklist Before Submit

- [ ] Keystore generated and secured
- [ ] Release bundle generated successfully
- [ ] App tested on real device (release build)
- [ ] In-app purchase tested
- [ ] All screenshots prepared (1080x1920 minimum)
- [ ] Feature graphic created (1024x500)
- [ ] Privacy policy hosted and accessible
- [ ] Store listing completed
- [ ] Content rating questionnaire completed
- [ ] In-app product created in Play Console
- [ ] Target audience declared

---

**Good luck with your launch! 🎉**

