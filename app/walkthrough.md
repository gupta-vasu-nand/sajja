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

### 4. Reconfigured Navigation Tabs (Bottom Navigation Restructuring)
- **Clock Face and Background Tabs**: Moved the two most popular custom tabs directly to the bottom navigation bar:
  - **Home**: (`dashboard` tab)
  - **Clock Face**: (`clock_settings` tab)
  - **Background**: (`background_settings` tab)
- **Removed Tools and Settings**: Eliminated the secondary Tools and Settings tabs from the bottom navigation bar, freeing up real estate and decluttering the base navigation surface.

### 5. Consolidated Radial FAB Menu
- **Integrated Concentric Actions**: Grouped configuration screens inside the circular radial floating action button menu concentric to the bottom-right corner:
  - **Themes** (`theme_settings` - Palette icon)
  - **Animations** (`animation_manager` - AutoAwesome sparkles icon)
  - **Tools** (`tools_settings` - Handyman/Build icon)
  - **Settings** (`app_settings` - Settings cog icon)

### 6. Database-Driven Custom Animation & Presets Manager Screen
- **Room Persistence Engine**: Created a database table `custom_animations` via Room (`CustomAnimationEntity.kt` and `CustomAnimationDao.kt`) allowing users to store, load, rename, update, and delete their customized animation styles under custom names. Exposed the DAO through `AppContainer` DI.
- **Predefined Presets**: Included 5 high-end presets inside the Animation Manager Screen:
  - *Matrix Rain* (Rain, speed 2.0x, count 100, green font, slide effect)
  - *Winter Flurry* (Snow, speed 0.8x, count 80, white font, fade effect)
  - *Magic Fireflies* (Fireflies, speed 1.3x, count 45, neon green, bounce effect)
  - *Meteor Shower* (Shooting stars, speed 2.5x, count 30, gold, bounce effect)
  - *Summer Bubbles* (Bubbles, speed 0.7x, count 35, white, fade effect)

### 7. Upgraded Digital Clock Customizations & Fine-Tuning
- **Dynamic Text Scaling**: Refactored the text size calculation in [ClockRenderer.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ClockRenderer.kt). The digital clock size now scales dynamically according to `numeralSize * 3.5f * clockSize`, ensuring a large, prominent display at 100% size.
- **Clock Fine-Tuning Offsets**: Introduced `clockOffsetX` and `clockOffsetY` sliders (`-300f..300f` for X, `-600f..600f` for Y) to allow users to adjust/offset the time and date placement relative to the pre-defined anchor alignment.
- **Typography & Font Style Selection**: Added selection chips for 4 typeface styles (Monospace, Sans Serif, Serif, Default) that map directly to the engine's Canvas Paint typeface rules.
- **Seconds Visibility & Format Toggle**: Integrated the `showSecondHand` settings state in [ClockRenderer.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ClockRenderer.kt) to switch formats between seconds/no-seconds. Added a "Use 24-Hour Format" preference toggle that supports automatic AM/PM marker removal.
- **Stylized AM/PM Indicator**: Completely redesigned how the AM/PM period marker is rendered. Rather than plain text appended to the time format, it is drawn dynamically as a smaller (45% size), semi-transparent superscript block positioned cleanly relative to the time, maintaining centering constraints.
- **Clock Positioning Mapping**: Introduced the `ClockPosition` enum (`CENTER`, `TOP_CENTER`, `BOTTOM_CENTER`, `TOP_LEFT`, `TOP_RIGHT`) and corresponding settings field. In [ClockRenderer.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ClockRenderer.kt), coordinates (`clockX`, `clockY`) and text alignment rules adapt dynamically.
- **Bottom Information Toggle (Date/Day)**: Added a toggle switch in the settings UI and corresponding model check `showBottomText` to show or completely hide the date and day text under the digital clock.
- **Glassmorphic backing Plate**: Added an option to draw a subtle translucent white backplate behind the digital clock, with an adjustable backing opacity slider (`0.05f..0.85f`).

### 8. Live Background Particle Effects & Animation Customizations
- **Particle System Engine**: Programmed a complete high-performance vector particle animation framework inside [ParticleEffects.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ParticleEffects.kt). It renders live background particles supporting:
  - **SNOW**: White flakes drifting down with simulated wind drift.
  - **BUBBLES**: Hollow air bubbles floating upwards with slight wave sways.
  - **STARS**: Shooting stars flying fast from right to left with a glowing tail.
  - **FIREFLIES**: Glowing green-yellow lights drifting with soft pulse rings.
  - **RAIN**: High-speed semi-transparent rain streaks falling diagonally across the wallpaper.
  - **NONE**: Disabled/turned off state.
- **Density & Speed Control**: Users can adjust particle counts (`10` to `150`) and speed factors (`0.2x` to `3.0x`) through sliders.
- **Digit Switching Animations**: Refactored the digital time renderer in [ClockRenderer.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/utils/ClockRenderer.kt) to support individual digit/character transitions:
  - **SLIDE**: Old characters slide up while new ones slide in from below.
  - **FADE**: Cross-fading digits gracefully.
  - **BOUNCE**: Scale down transition with an elastic bounce animation entry.
  - **NONE**: Regular instant updates.
- **Responsive 60 FPS Drawing Cycles**: Updated the redraw thread handlers inside `DigitalClockWallpaperService.kt`, `RomanClockWallpaperService.kt`, `ArabicClockWallpaperService.kt`, `MinimalClockWallpaperService.kt`, and [LivePreview.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/LivePreview.kt) to scale frame updates to 60fps (`16ms`) dynamically when active animations or particles are running, and drops back to `1000ms` when idle to conserve battery.

### 9. Compact and Professional Color Picker Redesign
- **Double Padding & Nesting Removal**: Completely rewrote [AdvancedColorPickerDialog.kt](file:///c:/Users/Vasu/Android Studio/Sajja/app/src/main/java/com/vng/sajja/ui/components/AdvancedColorPickerDialog.kt). Replaced the nested AlertDialog + Card setup with a singular Material 3 `Dialog` holding a rounded `Surface`.
- **Keyboardless Sliders**: Stretched red, green, and blue adjustment bars into a single horizontal row (`[Circle] Color [=== Slider ===] value`). Eliminated soft keyboard popups entirely, allowing quick, fluid color editing without pushing the dialog off-screen.

### 10. Unified dynamic accent color schemes
- **Cohesive Sliders**: Programmed all sliders globally in `CommonComponents.kt` and `ClockSettingsScreen.kt` to use dynamic, contrast-aware `SliderDefaults.colors()` matching the active primary color.
- **Contrast-Aware Filter Chips & Assist Chips**:
  - Styled `FilterChip` dynamically in `BackgroundSettingsScreen.kt` and `ClockSettingsScreen.kt` to swap to `MaterialTheme.colorScheme.primary` container with high-contrast text (`getContrastingTextColor`) when selected, and a clean transparent glassmorphic container when unselected.
- **Cohesive Action Buttons**: Standardized all primary action and intent buttons to use dynamic color styling, tying them to the custom accent theme color.

### 11. Dynamic Active Service Routing & Apply Actions
- **Wallpaper Service Component Resolution**: Reconfigured the live wallpaper intent launcher in [MainScreen.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/screens/MainScreen.kt). The engine component name now resolves class pathways dynamically based on the active `ClockType` settings (Roman, Arabic, Digital, Minimalist) instead of being hardcoded to Roman.

### 12. Cohesive Secondary & Tertiary Theme Derivation
- **Mathematical HSV Calibration**: Refined the color derivation logic in [Theme.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/theme/Theme.kt). Saturation and brightness ranges are mathematically bounded:
  - Dark Mode: Keeps secondary/tertiary colors highly vibrant and pastel.
  - Light Mode: Adjusts colors to be slightly deeper, ensuring high contrast against light surfaces.

### 13. Multi-Line Text Wrapping Constraints
- **Ellipsis Parameter Injections**: Added `maxLines = 1` and `overflow = TextOverflow.Ellipsis` properties on text fields to prevent line wrapping:
  - Navigation Item Labels in [CommonBottomNavBar.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/components/CommonBottomNavBar.kt).
  - Card Titles and Descriptions in [MainScreen.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/screens/MainScreen.kt).

### 14. Contrast-Aware FAB Color Adaptations
- **Contrast Color Integration**: Updated [CircularFabMenu.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/components/CircularFabMenu.kt) so that both the primary Floating Action Button and its circular menu overlays compute icon colors via `getContrastingTextColor`. The icons dynamically switch between black and white to match the dynamic background, ensuring perfect visibility in all themes.

### 15. Animated First-Launch Splash Screen
- **Launcher Asset Configuration**: Copied the playstore icon `ic_launcher-playstore.png` to the Android resource library `app/src/main/res/drawable/ic_launcher_playstore.png` to facilitate loading in the Compose environment.
- **Slide-Down Slide Easing Animation**: Designed [SajjaSplashScreen.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/ui/components/SajjaSplashScreen.kt) featuring an exact 2-second timing delay. The app logo slides gracefully from above the screen down to the center with a smooth deceleration curve.
- **Matched Background Colors**: Configured a linear background gradient blending a dark midnight blue (`#020208`) to navy/purple (`#0F1026`) that is scanned and matched directly from the borders of the launcher icon.
- **Warm Start Prevention**: Implemented a companion object launch flag inside [MainActivity.kt](file:///c:/Users/Vasu/Android%20Studio/Sajja/app/src/main/java/com/vng/sajja/MainActivity.kt). The splash screen renders *only* during the first process cold-start. Resuming the application from the background or rotating the screen (configuration changes) bypasses the splash screen completely.

### 16. Navigation Stack Fixes
- **Bottom Navigation Click Bug Resolved**: Updated the click action inside `CommonBottomNavBar.kt`. Clicking the "Home" option now explicitly calls `navController.popBackStack("dashboard", false)`, which instantly clears any overlay screens (like the collage or settings view) from the backstack and reliably loads the home dashboard.

---

## Verification Results

### Compile Verification
Ran Kotlin compiler verification successfully:
- Command: `./gradlew compileDebugKotlin`
- Output: `BUILD SUCCESSFUL`
