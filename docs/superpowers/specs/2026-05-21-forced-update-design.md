# Forced Update Screen — Design Spec

**Date:** 2026-05-21  
**Feature:** Automatic forced update detection and blocking  
**Status:** Approved

## Overview

When a critical update is published to the Play Store, AIpedia must detect it automatically on launch and block all app usage until the user installs the update. The block is enforced by the Android system via the Play In-App Updates Immediate flow — the app itself cannot be used while the system overlay is active.

## Approach

Google Play In-App Updates API (`play:app-update-ktx`) — Immediate update type. Chosen because:
- Official Google API for exactly this use case
- System-level overlay: impossible for the user to bypass without updating
- Zero custom UI to maintain
- Update priority is controlled per-release from Play Console (0–5 scale)

## Update Priority Control

Priority is set per release in Play Console (via Play Developer API or Gradle Play Publisher). The app only forces the update when priority ≥ 4. This allows:
- **Priority 0–3:** Optional updates, user is not interrupted
- **Priority 4–5:** Critical updates, immediate blocking overlay is shown

Priority cannot be changed after a release is published.

## Architecture

No new feature module. No ViewModel. No use case. Update detection is infrastructure, not domain logic — it lives entirely in `MainActivity`.

### Files changed

| File | Change |
|------|--------|
| `gradle/libs.versions.toml` | Add `app-update = "2.1.0"` version + `play-app-update` library entry |
| `app/build.gradle.kts` | Add `implementation(libs.play.app.update)` |
| `core/util/UpdateConfig.kt` | New file — `MIN_UPDATE_PRIORITY = 4` constant |
| `MainActivity.kt` | Add `AppUpdateManager`, `updateLauncher`, `onResume` check |

### UpdateConfig

```kotlin
// core/util/UpdateConfig.kt
object UpdateConfig {
    const val MIN_UPDATE_PRIORITY = 4
}
```

### MainActivity changes

```kotlin
private lateinit var appUpdateManager: AppUpdateManager

private val updateLauncher = registerForActivityResult(
    ActivityResultContracts.StartIntentSenderForResult()
) { result ->
    if (result.resultCode != RESULT_OK) finish()
}

override fun onCreate(...) {
    installSplashScreen()
    super.onCreate(...)
    appUpdateManager = AppUpdateManagerFactory.create(this)
    // ... existing setContent
}

override fun onResume() {
    super.onResume()
    checkForUpdate()
}

private fun checkForUpdate() {
    appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
        val priority = info.updatePriority()
        val available = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        val allowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
        if (available && priority >= UpdateConfig.MIN_UPDATE_PRIORITY && allowed) {
            appUpdateManager.startUpdateFlowForResult(
                info,
                updateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            )
        }
    }
}
```

## User Flow

1. User opens app → `onResume` fires → Play Store queried
2. **No critical update:** app loads normally
3. **Critical update available (priority ≥ 4):** system overlay appears immediately, app content is hidden
4. User taps "Update" → downloads and installs → app restarts normally
5. User cancels / presses back → `finish()` closes the app entirely

The check also runs when the user returns to the app from the launcher, catching the case where the overlay was shown, dismissed, and the user re-opens the app.

## Testing

- `FakeAppUpdateManager` for manual testing in debug builds (simulate update available + priority)
- No automated unit tests — `AppUpdateManager` is a system API not designed for JUnit mocking; behavior is verified manually using Play Console Internal Testing track with a real release

## Out of Scope

- Custom branded update screen (system overlay handles UI)
- Background checks while app is open
- Post-update restart handling (Play handles this)
