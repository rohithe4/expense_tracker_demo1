# Setup and Run Guide

## 🖥 Environment Requirements
- **Android Studio**: Ladybug or later.
- **JDK**: 17+ (Java 17 is recommended).
- **Gradle**: 8.x+
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## 📦 Core Stack
- **Languages**: Kotlin (1.9+)
- **UI**: Jetpack Compose (BOM 2024.0x)
- **Architecture**: ViewModel, MVI-like StateFlow.
- **Database**: Room Persistence Library.
- **Navigation**: Compose Navigation.

## 🚀 How to Run Locally

### 1. Clone & Open
Open the project folder `ExpenseTrackerDemo` in Android Studio.

### 2. Sync Gradle
Ensure the project syncs correctly. If you encounter issue with Room annotation processing:
- Check `build.gradle.kts` (app) for `androidx.room:room-compiler`.
- Ensure `ksp` or `kapt` plugin is correctly applied.

### 3. Run on Device/Emulator
- Use a physical device or a Pixel 7/8 emulator (API 33+).
- Run the `app` configuration.

## 🧪 Testing and Validation
- **Local Database**: Use Android Studio's **App Inspection** tool to view the `expense_database` in real-time.
- **Compose Preview**: Most components in `ui/components` have `@Preview` functions. Use them to verify design tweaks without rebuilds.

## 🛠 Antigravity Workflow Patterns
If you are an AI assistant (Antigravity):
- **Tooling**: Use `run_command` with `./gradlew assembleDebug` to verify compilation.
- **Pre-Commit**: Always verify that `Color.kt` and `Type.kt` haven't been overwritten with generic defaults.
- **Haptic Feedback**: Note that `AmountEntrySheet` uses `HapticFeedbackType.TextHandleMove`; this is intentional for physical keystroke feel.

---
> [!IMPORTANT]
> This project uses **KSP** for Room. Ensure your `ksp` settings are valid in the Gradle properties if you add new `@Entity` classes.
