# Roman Clock Wallpaper - Android Live Wallpaper

A feature-rich Android live wallpaper application that displays a classic analog clock with Roman numerals, built with modern Android development practices including Jetpack Compose, Canvas rendering, and Material Design 3.

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Installation](#installation)
- [Usage Guide](#usage-guide)
- [Customization Options](#customization-options)
- [Technical Details](#technical-details)
- [Import/Export System](#importexport-system)
- [Building from Source](#building-from-source)
- [Troubleshooting](#troubleshooting)

## Features

### Core Features
- **Live Wallpaper Engine**: True Android wallpaper service with smooth rendering
- **Roman Numeral Clock**: Classic analog clock with I-XII Roman numerals
- **Real-time Updates**: Second hand with step or smooth animation modes
- **Multiple Background Types**: Solid colors, gradients, and image collages
- **Customizable Clock Face**: Adjustable border, numerals, and hands
- **Date & Day Display**: Formatted date and weekday information

### Advanced Features
- **Image Collage System**: Arrange multiple images artistically behind the clock
- **Interactive Collage Editor**: Drag, rotate, resize images with visual controls
- **Theme Import/Export**: Share and backup themes via JSON files
- **Preset Themes**: Multiple professionally designed themes included
- **Material Design 3 UI**: Modern, accessible settings interface
- **Live Preview**: Real-time preview of all customizations

## Architecture

### Component Structure
| Component | Technology | Purpose |
|-----------|------------|---------|
| Wallpaper Engine | Android WallpaperService | Live wallpaper rendering |
| Rendering System | Android Canvas API | Clock drawing and animation |
| Settings UI | Jetpack Compose | User interface and configuration |
| Data Storage | SharedPreferences + GSON | Theme persistence and serialization |
| Image Loading | Coil | Efficient image loading for collages |

### File Structure
```
src/main/
├── java/com/vng/sajja/
│   ├── RomanClockWallpaperService.kt    # Wallpaper engine
│   ├── MainActivity.kt                  # Main settings activity
│   ├── settings/                        # Data models and persistence
│   │   ├── WallpaperSettings.kt         # Settings data class
│   │   ├── WallpaperSettingsRepository.kt # Preferences manager
│   │   ├── ThemePresets.kt              # Predefined themes
│   │   └── ThemeSerializer.kt           # JSON serialization
│   └── ui/                              # Compose UI components
│       ├── SettingsScreen.kt            # Main settings screen
│       ├── CollageScreen.kt             # Collage editor
│       ├── LivePreview.kt               # Clock preview
│       ├── ColorPickerDialog.kt         # Color picker
│       └── AdvancedColorPickerDialog.kt # Enhanced color picker
└── res/
    └── xml/wallpaper.xml                # Wallpaper metadata
```

## Installation

### Prerequisites
- Android Studio Flamingo or later
- Android SDK 24+ (minSdk 24)
- Kotlin 1.8.0+
- JDK 11 or higher

### Building from Source
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Build the project (Build > Make Project)
5. Run on emulator or physical device

### APK Installation
1. Enable "Unknown Sources" in device settings
2. Transfer the APK to your device
3. Open the APK file and follow installation prompts
4. Set as wallpaper via app or wallpaper picker

## Usage Guide

### Setting as Wallpaper
1. Open the Roman Clock Wallpaper app
2. Customize your preferred settings
3. Tap "Apply Wallpaper" in the top right corner
4. Follow system prompts to set as home/lock screen wallpaper

### Settings Interface
The app uses a tab-based interface for organization:

| Tab | Purpose | Key Features |
|-----|---------|--------------|
| Background | Background configuration | Solid colors, gradients, collage settings |
| Clock | Clock face customization | Size, border, numerals, hand colors |
| Collage | Image collage editor | Add, arrange, edit multiple images |
| Theme | Preset themes | One-click theme application |
| Tools | Import/Export | Share themes, backup settings |

## Customization Options

### Background Types
| Type | Description | Customization Options |
|------|-------------|----------------------|
| Solid | Single color background | Color picker |
| Gradient | Two-color gradient | Start/end colors, angle |
| Collage | Multiple image arrangement | Image selection, layout, opacity |

### Clock Customization
| Component | Options | Range |
|-----------|---------|-------|
| Clock Size | Scale factor | 50% - 100% |
| Border | Show/hide, color, width | 2px - 30px |
| Roman Numerals | Show/hide, color, size | 20px - 80px |
| Clock Hands | Colors, widths, smooth animation | Various |
| Center Knob | Color, radius, ring settings | 5px - 30px |
| Date & Day | Show/hide, colors, sizes | 20px - 60px |

### Collage System Features
- **Multiple Layouts**: Grid, Masonry, Center Focus, Spiral, Random
- **Individual Image Controls**: Opacity, rotation, scale type, z-index
- **Batch Editing**: Apply changes to all images simultaneously
- **Interactive Editing**: Drag, pinch, rotate gestures
- **Auto-arrange**: One-click layout organization

## Technical Details

### Wallpaper Engine Implementation
The wallpaper engine uses Android's `WallpaperService` with an inner `Engine` class for proper lifecycle management. Rendering is performed on a `Canvas` obtained from `SurfaceHolder.lockCanvas()` with efficient frame skipping and bitmap caching.

### Performance Optimizations
- **Bitmap Caching**: Images are cached and properly recycled
- **Frame Rate Control**: Configurable update intervals (16ms for smooth, 1000ms for step)
- **Memory Management**: Proper bitmap scaling and disposal
- **Efficient Rendering**: Optimized drawing operations and path calculations

### Data Persistence
- **SharedPreferences**: All settings are saved persistently
- **GSON Serialization**: Theme export/import uses JSON format
- **Image URI Storage**: Secure storage of selected image URIs
- **Preset Management**: Built-in and user-saved presets

## Import/Export System

### Exporting Themes
1. Navigate to the "Tools" tab
2. Tap "Export Theme"
3. Choose sharing method (Save to file, Share via app)
4. Theme is exported as JSON with all settings

### Importing Themes
1. Navigate to the "Tools" tab
2. Choose import method:
    - "Import from File": Select JSON file
    - Paste JSON in text field
3. Confirm import to apply theme
4. Theme is immediately applied and saved

### JSON Format
Themes are exported in this JSON structure:
```json
{
  "backgroundType": "GRADIENT",
  "backgroundColor": "#000000",
  "gradientStartColor": "#FF512F",
  "gradientEndColor": "#DD2476",
  "clockSize": 0.85,
  "showBorder": false,
  "numeralColor": "#FFFFFF",
  "hourHandColor": "#FFFFFF",
  "secondHandColor": "#FF0000",
  "showDate": true,
  "dateColor": "#CCCCCC"
}
```

## Building from Source

### Dependencies
The project uses the following major dependencies:

| Dependency | Version | Purpose |
|------------|---------|---------|
| Jetpack Compose BOM | 2024.02.00 | UI framework |
| Material 3 | 1.1.2 | Design system |
| Coil Compose | 2.5.0 | Image loading |
| GSON | 2.10.1 | JSON serialization |
| AndroidX Core | 1.12.0 | Core Android extensions |

### Build Configuration
Key build settings in `build.gradle.kts`:
- `minSdk`: 24 (Android 7.0)
- `targetSdk`: 34 (Android 14)
- `compileSdk`: 34
- `kotlinCompilerExtensionVersion`: 1.5.1

### Building Steps
1. Ensure all dependencies are synced
2. Build APK: `./gradlew assembleDebug`
3. Build signed release: `./gradlew assembleRelease`
4. Install debug build: `./gradlew installDebug`

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| Wallpaper not applying | Check wallpaper permissions, restart device |
| Images not loading in collage | Grant storage permissions, check file format |
| Settings not saving | Clear app cache, reinstall app |
| Performance issues | Reduce collage complexity, disable smooth second hand |
| Import/export failing | Check JSON format, ensure proper permissions |

### Performance Tips
- Use solid colors or simple gradients for best performance
- Limit collage images to 5-10 for optimal memory usage
- Disable smooth second hand on older devices
- Use appropriate image resolutions (avoid 4K+ images in collage)

### Debugging
- Enable developer options on device
- Check Logcat for error messages
- Test with different Android versions
- Verify all permissions are granted

### Attribution
- Roman numeral font uses system serif typeface
- Color picker implementation based on Material Design 3 guidelines
- Collage system inspired by modern photo editing apps

### Contributing
Contributions are welcome. Please ensure:
- Code follows Kotlin and Compose best practices
- All new features include appropriate tests
- UI changes maintain Material Design 3 compliance
- Documentation is updated accordingly

### Support
For issues, feature requests, or questions:
1. Check the troubleshooting guide
2. Review existing GitHub issues
3. Submit detailed bug reports with device information
4. Include steps to reproduce issues

---

This project represents a complete, production-ready Android live wallpaper application with professional-grade features and modern Android development practices. The architecture is scalable, maintainable, and follows current Android development best practices.