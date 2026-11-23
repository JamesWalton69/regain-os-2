# Regain — Full Android Native Duplicate

A full native Android reproduction of **Regain** ([regainapp.ai](https://regainapp.ai/)), the #1 focus app and screen time management system trusted by millions of students to eliminate doomscrolling and reclaim 3+ hours a day.

Built using modern **Kotlin**, **Jetpack Compose (Material 3)**, **Android AccessibilityService**, **WindowManager Overlays**, **Foreground Services**, and **Embedded Ambient Audio**.

---

## 📱 Prebuilt APK

The compiled Android package is ready to install:
- **Location:** [`Regain-v1.0.0.apk`](file:///E:/Coding/Projects/regain2/Regain-v1.0.0.apk)
- **Install command:**
  ```bash
  adb install -r "E:\Coding\Projects\regain2\Regain-v1.0.0.apk"
  ```

---

## ✨ Features Implemented (1:1 with Regain)

### 1. Focus Timer & Immersion Dial (Screenshot 1)
- **3 Operating Modes:**
  - **Timer:** Standard countdown with customizable duration (25m, 45m, 60m, custom).
  - **Stopwatch:** Open-ended count-up timer for deep study flow.
  - **Pomodoro:** Multi-round intervals with automatic short breaks (5m) and long breaks (15m).
- **Focus Subjects / Tags:** Study, Physics, Maths, Coding, Reading, Deep Work, with ability to add custom tags with color indicators.
- **Visual Dial:** High-contrast circular progress ring, atmospheric Aurora Borealis background, and large bold digital readout (`24:56`).
- **Foreground Service:** Continuous countdown with ongoing lock-screen notifications and action controls.

### 2. Science-Backed Ambient Soundscapes
Embedded looping sound generators in `res/raw` with interactive volume control:
- 🎧 **Lo-Fi Beats & Chords**
- 🌧️ **Rain & Thunder**
- 🔥 **Cozy Fireplace**
- 🌊 **Ocean Waves**
- 🌲 **Forest Birds**
- 📻 **White / Brown Noise**

### 3. Signature Feature — Block Reels & Shorts (Screenshot 6)
- **Surgical Blocker:** Unlike blunt blockers that kill the whole app, Regain allows messaging, search, and study posts while blocking infinite-scroll dopamine loops.
- **Node-Level Accessibility Inspection:** Intercepts Instagram Reels, YouTube Shorts, Snapchat Spotlight, and Facebook Reels.
- **Floating Blocker Dialog:** Pops up the authentic dark card with hourglass logo, settings shortcut, Reels icon, and a "Back to Feed" escape button.

### 4. App Blocker & Strict Mode (Screenshot 2)
- **Zero-Loophole Discipline:** When Strict Mode is active, users cannot pause, quit early, or uninstall the app until the session ends.
- **Fullscreen Lock Screen:** Displays an OLED pitch-black screen with mascot **Rega** holding the blocked app icon with a polite stop hand and a "Return to Focus" button.
- **Supported Targets:** Instagram, YouTube, Snapchat, Facebook, Reddit, TikTok, X (Twitter), Netflix, plus custom apps.

### 5. YouTube Study Mode (Screenshot 3)
- **Curated Educational Channels:** Allows whitelisted study channels (PhysicsWallah, Unacademy, Dear Sir, MIT OpenCourseWare, 3Blue1Brown) marked with a green checkmark.
- **Distraction Shield:** Locks entertainment feeds, music, gaming, and Shorts shelves during study sessions.

### 6. Multiplayer Live Focus Rooms (Screenshot 4)
- **Study Together Globally:** Live focus rooms (e.g., "IIT Aspirants", "Med School Deep Work", "Late Night Study").
- **Live Counter:** Real-time active focus counter (`🟢 520,203 focusing`).
- **Participant Grid:** Live avatars showing study partner names and active durations (`You 2h 45m`, `Rohan 2h 30m`, `Ayaan Khan 2h 15m`).
- **Floating Emoji Reactions:** Tap to send animated floating reactions (❤️, 👏, 💯, 🔥, 😮).

### 7. Leaderboards & Streaks (Screenshot 5)
- **Top 3 Podium:** 1st (Gold badge), 2nd (Silver badge), 3rd (Bronze badge) with profile avatars and focus time (e.g. `23h 20m`, `19h 15m`).
- **Rankings List:** Ranks 4..50+ with daily streak counters (🔥 7-day streak) and points.

### 8. Daily Study Schedules (Screenshot 7)
- **Scheduled Study Blocks:**
  - 🌤️ Morning Study (6:00 AM - 8:00 AM)
  - ☀️ Afternoon (2:00 PM - 4:00 PM)
  - 📚 Physics (8:00 AM - 10:00 AM)
  - 📐 Maths (6:00 PM - 9:00 PM)
- **Alarm Receiver:** Auto-starts focus sessions and strict blocks when the scheduled hour arrives.

### 9. Screen Time & Study Analytics (Screenshot 8)
- **Daily Focus Stats:** `3h 10m Today`
- **7-Day Bar Chart:** Monday through Sunday visualization with active day highlighted.
- **Tag Distribution Breakdown:** Color-coded progress bars (Physics 1h 45m, Maths 1h 12m, Untagged 56m).

### 10. Meet Rega — The Focus Mascot
- Friendly green seed companion with interactive speech bubbles, motivational encouragement, and streak reminders.

### 11. Regain Pro & Custom Themes
- 🌌 **Aurora Night** (Default emerald glow)
- 🌑 **Midnight OLED** (Pure pitch black `#000000`)
- 🌿 **Deep Forest** (Pine & sage green)
- 🌅 **Sunset Serenity** (Warm amber & violet)
- ⚡ **Cyberpunk Neon** (Cyan & electric blue)

---

## 🛠️ Project Structure

```
regain2/
├── Regain-v1.0.0.apk                 # Ready-to-install Android APK
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml       # Permissions, Services & Receiver declarations
│   │   ├── java/ai/regain/app/
│   │   │   ├── RegainApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── FocusModels.kt    # Data classes, enums & theme definitions
│   │   │   │   └── PreferencesManager.kt # SharedPreferences & persistence
│   │   │   ├── service/
│   │   │   │   ├── FocusTimerService.kt # Foreground service & sound player
│   │   │   │   ├── RegainAccessibilityService.kt # Reels & App detection
│   │   │   │   └── BlockerOverlayManager.kt # Floating & fullscreen overlays
│   │   │   ├── receiver/
│   │   │   │   └── ScheduleAlarmReceiver.kt # Scheduled study blocks
│   │   │   └── ui/
│   │   │       ├── MainApp.kt        # Navigation drawer & screen routing
│   │   │       ├── theme/            # Color.kt & Theme.kt
│   │   │       ├── components/
│   │   │       │   ├── AuroraBackground.kt # Starry sky & aurora canvas
│   │   │       │   ├── FocusDial.kt   # High-contrast circular timer
│   │   │       │   ├── RegaMascotView.kt # Mascot avatar & speech bubbles
│   │   │       │   ├── SoundscapeSheet.kt # Ambient sound selector
│   │   │       │   └── TagSelectorDialog.kt # Subject tag picker
│   │   │       └── screens/
│   │   │           ├── FocusScreen.kt # Screen 1
│   │   │           ├── MultiplayerScreen.kt # Screen 4
│   │   │           ├── LeaderboardScreen.kt # Screen 5
│   │   │           ├── ScheduleScreen.kt # Screen 7
│   │   │           ├── AnalyticsScreen.kt # Screen 8
│   │   │           ├── AppBlockerScreen.kt # Screens 2 & 6
│   │   │           ├── YouTubeStudyScreen.kt # Screen 3
│   │   │           └── SettingsScreen.kt # Themes & info
│   │   └── res/
│   │       ├── drawable/             # Vectors (ic_rega_mascot, ic_launcher)
│   │       ├── layout/               # Overlays (overlay_reels_blocked, overlay_app_blocked)
│   │       ├── raw/                  # Ambient audio (lofi, rain, forest, fireplace, ocean, white_noise)
│   │       └── xml/                  # accessibility_service_config.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```
