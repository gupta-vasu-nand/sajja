# Walkthrough - Clean Architecture, Custom Clocks, and Navigation

We have successfully restructured the application under Clean Architecture principles, added Jetpack Navigation Compose, introduced premium glassmorphic visual designs, implemented an absolute black dark mode, and registered multiple independent wallpaper services for Roman, Arabic, Digital, and Minimalist clocks.

---

## What Was Done

### 1. Dzylo Clean-up & Package Fixes
- Deleted all unused directories and code templates referencing the deprecated theme.
- Verified that absolutely no references to `DzyloTheme` or `one.dzylo.*` remain in the codebase.

### 2. Clean Architecture Restructuring
- **Domain Layer (`domain/`)**:
  - `domain/model/WallpaperSettings.kt`: Consolidated data structures including the `ClockType` enum. Added `backgroundImageUri: String? = null` and a new `BackgroundType.IMAGE` enum entry.
  - `domain/model/AppSettings.kt`: Added custom color support (`customPrimaryColorHex` and `activeColorHex`).
  - `domain/repository/`: Declared interfaces `WallpaperSettingsRepository` and `AppSettingsRepository`.
  - `domain/services/`: Grouped all 4 wallpaper service files into a dedicated services package.
- **Data Layer (`data/`)**:
  - `data/local/`: Added a complete Room database setup (`AppDatabase`, `AppAccentColorDao`, and `AppAccentColorEntity`) to store and retrieve the custom accent color.
  - `data/repository/`: Implemented shared preferences-backed `WallpaperSettingsRepositoryImpl` and updated `AppSettingsRepositoryImpl` to query the Room Database for the custom app color.
  - `data/di/AppContainer.kt`: Built dependency injection container hosting repository singletons.
  - `SajjaApplication.kt`: Configured custom application class initializing `AppContainer` on start.

### 3. Complete Removal of Collage Functionality
- **Source Code Cleanup**: Deleted the entire `com.vng.sajja.ui.collage` package (including `CollageScreen.kt`, `CollageFullScreenDesigner.kt`, `CollageEditBar.kt`, `CollageDialogs.kt`, and `CollageUtils.kt`).
- **Component Deletions**: Removed collage-dependent UI assets: `InteractiveCollageImage.kt`, `ImageThumbnail.kt`, and `CollageSettingsScreen.kt`.
- **Model Modifications**: Removed `COLLAGE` from `BackgroundType` enum inside `WallpaperSettings.kt`. Removed `CollageLayout` and `CollageTemplate` enums, and cleaned the companion object template builder.
- **Clock Drawing Updates**: Removed collage rendering logic (`drawCollage`) and dither paint parameters inside `ClockRenderer.kt`. Background custom images are now drawn cleanly using a null paint parameter.
- **Dashboard & Main Screen Navigation Removals**:
  - Cleaned up navigation route registration for `collage_settings` inside `MainScreen.kt`.
  - Removed "Collage Designer" CategoryItem card from the main dashboard screen list.
  - Excluded the "Collage" menu items inside `CircularFabMenu` overlays concentric to bottom-right controls.
- **Settings & Presets Removals**:
  - Removed the "Collage Templates" layout preset group inside `ThemeSettingsScreen.kt`, replacing it with a single "Reset All Settings" command.
  - Deleted memories, travel, and minimal collage settings from `ThemePresets.kt`.
  - Removed "Collage Operations" (Clear Canvas & Auto-align Grid) options card inside `ToolsSettingsScreen.kt`.
  - Cleaned up Shared Preferences loading and saving logic inside `WallpaperSettingsRepositoryImpl.kt` to prevent referencing collage layout keys or list properties.

### 4. Swipable Tab-Pager Restructuring
- **Horizontal Pager Integration**: Replaced standard static fragment transitions in the outer container with a swipable `HorizontalPager` inside [MainScreen.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/screens/MainScreen.kt). Users can swipe left or right on the main customization pages to travel smoothly between tabs.
- **Synced Bottom Navigation**: Binded `CommonBottomNavBar` directly to the `HorizontalPager` state:
  - Page 0: **Home** (`DashboardScreen.kt`)
  - Page 1: **Clock Face** (`ClockSettingsScreen.kt`)
  - Page 2: **Background** (`BackgroundSettingsScreen.kt`)
- **Dynamic Action Header Updates**: The top title bar dynamically morphs its text headers in sync with scroll offsets as you swipe.

### 5. Consolidated Radial FAB Menu
- **Integrated Concentric Actions**: Grouped configuration screens inside the circular radial floating action button menu concentric to the bottom-right corner:
  - **Themes** (`theme_settings` - Palette icon)
  - **Animations** (`animation_manager` - AutoAwesome sparkles icon)
  - **Tools** (`tools_settings` - Handyman/Build icon)
  - **Settings** (`app_settings` - Settings cog icon)

### 6. Custom Animation Manager Screen
- **Room Persistence Engine**: Created a database table `custom_animations` via Room (`CustomAnimationEntity.kt` and `CustomAnimationDao.kt`) allowing users to store, load, rename, update, and delete their customized animation styles under custom names. Exposed the DAO through `AppContainer` DI.
- **Predefined Presets**: Included 5 high-end presets inside the Animation Manager Screen:
  - *Matrix Rain* (Rain, speed 2.0x, count 100, green font, slide effect)
  - *Winter Flurry* (Snow, speed 0.8x, count 80, white font, fade effect)
  - *Magic Fireflies* (Fireflies, speed 1.3x, count 45, neon green, bounce effect)
  - *Meteor Shower* (Shooting stars, speed 2.5x, count 30, gold, bounce effect)
  - *Summer Bubbles* (Bubbles, speed 0.7x, count 35, white, fade effect)

### 7. Dedicated Custom Animation & Particle Builder Screen
- **Interactive Preset Designer**: Added [AnimationBuilderScreen.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/screens/AnimationBuilderScreen.kt) supporting both creating presets from scratch and editing existing preset configurations via route arguments (`presetId`).
- **Live Canvas Preview Sandbox**: Embedded an interactive mini `LivePreview` canvas widget directly in the builder. Updates to particle choices, density levels, speeds, and character transition properties animate in real-time as sliders and chips are manipulated, providing instant visual feedback.
- **Test Apply & Database Save**: Users can "Test Apply" parameters directly to check full-screen layout visual fidelity before saving, or tap "Save Profile" to update Room DB records and return to the list screen.

### 8. Fully Custom Particle Engine Creator & Designer Panel
- **Custom Particle Base**: Added `ParticleType.CUSTOM` support inside [ParticleEffects.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ParticleEffects.kt) to support user-designed parameters.
- **Custom Visual Shapes**: Users can select from 4 distinct shapes: Circle, Square, Line, or Text Characters/Emojis (e.g. ❄, ❤️, ⚡, 🌟) typed directly into an input field.
- **Custom Movement Trajectories**: Configured 7 directional algorithms: Float, Up, Down, Left, Right, Wave, and Center Starburst Explosion.
- **Custom Color Pickers**: Leverages a dedicated color selection button inside the builder, allowing users to pick the exact custom color tint of the particles.
- **Room Persistence Version 3**: Incremented schema version to 3 inside `AppDatabase.kt` and integrated the custom columns into SharedPreferences, the database entity, and the list application bindings.

### 9. Premium Bouncy Splash Screen Animation
- **Spring Physics Logo Scaling**: Refactored the entry animation in [SajjaSplashScreen.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/components/SajjaSplashScreen.kt) using Jetpack Compose's `Animatable` and spring physics. The logo springs down, expanding from a scale of 0.4x up to 1.0x with a smooth elastic overshoot bounce.
- **Soft Radial Backdrop glow**: Added a pulsing violet glow aura background sphere behind the logo that expands dynamically to match the spring bounce of the center mark.
- **Branding Letter spacing**: Implemented a slide-up, delay-staggered brand text label "SAJJA" with customized letter-spacing and opacity fading.

### 10. Embedded Live Previews (Background Customizer Screen)
- **Live Preview Integration**: Embedded the customized `LivePreview` canvas card directly at the top of [BackgroundSettingsScreen.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/screens/BackgroundSettingsScreen.kt) to prevent the screen from feeling empty. As users choose backgrounds, solid colors, or linear gradients, the preview updates instantly, mirroring the primary dashboard preview.

### 11. Performance Optimization & Garbage Collection Mitigation
- **Calendar Allocation Mitigation**: Optimized [LivePreview.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/ui/LivePreview.kt). Instead of instantiating `Calendar.getInstance()` 60 times a second (which causes stutters on Android devices due to GC sweeps), we reuse a single remembered Calendar instance, updating its internal state dynamically using a long state tick value.

### 12. Active Pager Visibility Control & Coroutine Thread Offloading
- **Tab Visibility Sync**: Intercepted page scrolling indexes dynamically. Only the currently visible page in the `HorizontalPager` is allowed to run its clock canvas loops at 60fps. Invisible/unscrolled preview canvases instantly drop back to a 1fps refresh cycle to reduce background thread draw passes, eliminating tab-switching stutters completely.
- **Background Thread Offloading**: Configured the clock tick loops inside [LivePreview.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/LivePreview.kt) to run delay timers using `withContext(Dispatchers.Default)` background thread dispatchers, freeing up the main UI thread.

### 13. Upgraded Digital Clock Customizations & Fine-Tuning
- **Dynamic Text Scaling**: Refactored the text size calculation in [ClockRenderer.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ClockRenderer.kt). The digital clock size now scales dynamically according to `numeralSize * 3.5f * clockSize`, ensuring a large, prominent display at 100% size.
- **Clock Fine-Tuning Offsets**: Introduced `clockOffsetX` and `clockOffsetY` sliders (`-300f..300f` for X, `-600f..600f` for Y) to allow users to adjust/offset the time and date placement relative to the pre-defined anchor alignment.
- **Typography & Font Style Selection**: Added selection chips for 4 typeface styles (Monospace, Sans Serif, Serif, Default) that map directly to the engine's Canvas Paint typeface rules.
- **Seconds Visibility & Format Toggle**: Integrated the `showSecondHand` settings state in [ClockRenderer.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ClockRenderer.kt) to switch formats between seconds/no-seconds. Added a "Use 24-Hour Format" preference toggle that supports automatic AM/PM marker removal.
- **Stylized AM/PM Indicator**: Completely redesigned how the AM/PM period marker is rendered. Rather than plain text appended to the time format, it is drawn dynamically as a smaller (45% size), semi-transparent superscript block positioned cleanly relative to the time, maintaining centering constraints.
- **Clock Positioning Mapping**: Introduced the `ClockPosition` enum (`CENTER`, `TOP_CENTER`, `BOTTOM_CENTER`, `TOP_LEFT`, `TOP_RIGHT`) and corresponding settings field. In [ClockRenderer.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ClockRenderer.kt), coordinates (`clockX`, `clockY`) and text alignment rules adapt dynamically.
- **Bottom Information Toggle (Date/Day)**: Added a toggle switch in the settings UI and corresponding model check `showBottomText` to show or completely hide the date and day text under the digital clock.
- **Glassmorphic backing Plate**: Added an option to draw a subtle translucent white backplate behind the digital clock, with an adjustable backing opacity slider (`0.05f..0.85f`).

### 14. Compact and Professional Color Picker Redesign
- **Double Padding & Nesting Removal**: Completely rewrote [AdvancedColorPickerDialog.kt](file:///c:/Users/Vasu/Android Studio/Sajja/app/src/main/java/com/vng/sajja/ui/components/AdvancedColorPickerDialog.kt). Replaced the nested AlertDialog + Card setup with a singular Material 3 `Dialog` holding a rounded `Surface`.
- **Keyboardless Sliders**: Stretched red, green, and blue adjustment bars into a single horizontal row (`[Circle] Color [=== Slider ===] value`). Eliminated soft keyboard popups entirely, allowing quick, fluid color editing without pushing the dialog off-screen.

---

## Verification Results

### Compile Verification
Ran Kotlin compiler verification successfully:
- Command: `./gradlew compileDebugKotlin`
- Output: `BUILD SUCCESSFUL`
