# Forced Update Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Automatically detect critical Play Store updates on app launch and block all usage until the user installs them, using the Play In-App Updates Immediate flow.

**Architecture:** `AppUpdateManager` is created in `MainActivity.onCreate`, `onResume` triggers a Play Store query, and if a release with priority ≥ 4 is available the system overlay is shown. If the user cancels, `finish()` closes the app. No ViewModel, no use case — this is system infrastructure.

**Tech Stack:** `com.google.android.play:app-update-ktx:2.1.0`, `ActivityResultContracts.StartIntentSenderForResult`, Kotlin, Hilt (existing)

---

## File Map

| File | Action |
|------|--------|
| `gradle/libs.versions.toml` | Modify — add version + library entry |
| `app/build.gradle.kts` | Modify — add implementation dependency |
| `app/src/main/java/com/burixer85/aipedia/core/util/UpdateConfig.kt` | Create — priority threshold constant |
| `app/src/main/java/com/burixer85/aipedia/MainActivity.kt` | Modify — add update manager + onResume check |

---

### Task 1: Add play-app-update-ktx dependency

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`

- [ ] **Step 1: Add version and library entry to libs.versions.toml**

In `gradle/libs.versions.toml`, add after the `splashscreen` line in `[versions]`:

```toml
app-update = "2.1.0"
```

And add after `play-services-ads` in `[libraries]`:

```toml
play-app-update = { group = "com.google.android.play", name = "app-update-ktx", version.ref = "app-update" }
```

- [ ] **Step 2: Add implementation dependency to app/build.gradle.kts**

In `app/build.gradle.kts`, inside the `dependencies { }` block, add after the `play-services-ads` line:

```kotlin
implementation(libs.play.app.update)
```

- [ ] **Step 3: Sync and verify the build compiles**

Run:
```
./gradlew assembleDebug
```

Expected: `BUILD SUCCESSFUL`. If you see `Could not resolve com.google.android.play:app-update-ktx`, check the version string in `libs.versions.toml` matches exactly `2.1.0`.

- [ ] **Step 4: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts
git commit -m "build: add play-app-update-ktx dependency"
```

---

### Task 2: Create UpdateConfig

**Files:**
- Create: `app/src/main/java/com/burixer85/aipedia/core/util/UpdateConfig.kt`

- [ ] **Step 1: Create the file**

```kotlin
package com.burixer85.aipedia.core.util

object UpdateConfig {
    const val MIN_UPDATE_PRIORITY = 4
}
```

This constant is the minimum Play Console release priority (0–5 scale) that triggers the forced update. Releases with priority 0–3 are silently ignored; 4–5 show the blocking overlay.

- [ ] **Step 2: Verify it compiles**

```
./gradlew assembleDebug
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/core/util/UpdateConfig.kt
git commit -m "feat(update): add UpdateConfig with minimum priority threshold"
```

---

### Task 3: Wire forced update into MainActivity

**Files:**
- Modify: `app/src/main/java/com/burixer85/aipedia/MainActivity.kt`

- [ ] **Step 1: Add imports**

Add these imports at the top of `MainActivity.kt` (after the existing imports):

```kotlin
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import androidx.activity.result.contract.ActivityResultContracts
import com.burixer85.aipedia.core.util.UpdateConfig
```

- [ ] **Step 2: Add AppUpdateManager field and updateLauncher**

Inside `MainActivity` class, before `onCreate`, add:

```kotlin
private lateinit var appUpdateManager: AppUpdateManager

private val updateLauncher = registerForActivityResult(
    ActivityResultContracts.StartIntentSenderForResult()
) { result ->
    if (result.resultCode != RESULT_OK) {
        finish()
    }
}
```

`updateLauncher` must be registered before `onCreate` completes (registering it as a field initializer achieves this). If the user cancels the update dialog or it fails, `finish()` closes the app entirely — they cannot use it without updating.

- [ ] **Step 3: Initialize AppUpdateManager in onCreate**

In `onCreate`, add `appUpdateManager = AppUpdateManagerFactory.create(this)` immediately after `super.onCreate(savedInstanceState)`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    appUpdateManager = AppUpdateManagerFactory.create(this)
    enableEdgeToEdge()
    setContent {
        AIpediaTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavigationHost(navHostController = rememberNavController())
            }
        }
    }
}
```

- [ ] **Step 4: Add onResume override and checkForUpdate**

After `onCreate`, add:

```kotlin
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

`addOnSuccessListener` is fire-and-forget — it silently does nothing if the Play Store is unreachable or the app is sideloaded. Only when all three conditions are met (update available + priority ≥ 4 + IMMEDIATE type allowed) does the overlay appear.

- [ ] **Step 5: Build and verify**

```
./gradlew assembleDebug
```

Expected: `BUILD SUCCESSFUL` with no warnings about unresolved references.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/MainActivity.kt
git commit -m "feat(update): add forced update check on app resume"
```

---

## Manual Testing Guide

The Play In-App Updates API does **not** work in sideloaded debug builds. To test the full flow:

**Option A — Internal Testing Track (recommended):**
1. Upload a new AAB to Play Console Internal Testing track
2. Set the release priority to 5 via Play Developer API or Gradle Play Publisher
3. On a test device, install the *previous* version from the Internal Testing link
4. Open the app → the system overlay should appear immediately

**Option B — FakeAppUpdateManager in a debug-only branch:**

Temporarily replace `AppUpdateManagerFactory.create(this)` in `onCreate` with:

```kotlin
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager

appUpdateManager = FakeAppUpdateManager(this).also { fake ->
    fake.setUpdateAvailable(10)   // any higher versionCode
    fake.setUpdatePriority(5)     // triggers forced update
}
```

Run on a device/emulator → the overlay appears. Revert before merging.

**Verifying cancel behavior:** When the overlay is shown, press the back button → the app should close completely (not return to HomeScreen).
