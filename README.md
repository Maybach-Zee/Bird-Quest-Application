# BirdQuest

A native Android application for bird enthusiasts to discover, log, and track bird sightings — built with Kotlin and designed for a seamless field experience.

## Overview

BirdQuest is a birdwatching companion app that allows users to explore and record bird species they encounter. Whether you're a casual nature lover or a dedicated ornithologist, BirdQuest helps you document your wildlife observations and build a personal sighting log.

The project includes research and design documentation, a signed release keystore, and a full Gradle-based Android build setup.

## Tech Stack

| Technology          | Usage                          |
|---------------------|--------------------------------|
| Kotlin              | Primary programming language   |
| Android SDK         | Mobile application framework   |
| Gradle (Kotlin DSL) | Build system                   |
| XML Layouts         | UI design and screen structure |

## Project Structure

```
Bird-Quest-Application/
├── app/                                # Main Android application module
│   ├── src/main/
│   │   ├── java/                       # Kotlin source files
│   │   └── res/                        # Layouts, drawables, strings
│   └── build.gradle.kts
├── gradle/                             # Gradle wrapper files
├── Research and Design for Part 1/    # Research docs & UI/UX designs
├── BirdQuest Application - Release Notes.txt
├── BirdQuest.jks                       # Signed release keystore
├── build.gradle.kts                    # Root build configuration
├── settings.gradle.kts                 # Project settings
├── gradle.properties
├── gradlew                             # Unix Gradle wrapper
└── gradlew.bat                         # Windows Gradle wrapper
```

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable version)
- Android SDK (API Level 21+)
- Kotlin plugin (bundled with Android Studio)
- JDK 11 or higher

### Running the App

1. Clone the repository:

   ```bash
   git clone https://github.com/Maybach-Zee/Bird-Quest-Application.git
   cd Bird-Quest-Application
   ```

2. Open the project in Android Studio.
3. Allow Gradle to sync and resolve all dependencies.
4. Run the app on an emulator or connected Android device via the Run button or `Shift + F10`.

### Building from the Command Line

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

## Features

- Bird species discovery and browsing
- Personal sighting log and tracker
- Clean, intuitive mobile-first UI
- Signed release build ready for distribution
- Native Android performance with Kotlin

## Release Notes

See `BirdQuest Application - Release Notes.txt` for the full version history, known issues, and feature updates.

## Research & Design

The `Research and Design for Part 1/` directory contains the initial research documentation and UI/UX wireframes that informed the application's development.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

This project is open source and available under the [MIT License](LICENSE).
