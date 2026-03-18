# Device Info Storage Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the `Device Info` placeholder with a read-only device information screen that shows model, Android version, and storage usage.

**Architecture:** Add a small Android-backed device-info data source, surface its read-only state through `HubViewModel`, and render it with a dedicated Compose screen. Leave the rest of the hub navigation unchanged.

**Tech Stack:** Kotlin, Android SDK (`Build`, `StatFs`), Jetpack Compose, JUnit

---

## Chunk 1: Device Info State

### Task 1: Define the read-only state and source

**Files:**
- Create: `app/src/main/java/com/example/rokidsettingshub/model/DeviceInfoState.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/data/deviceinfo/DeviceInfoSource.kt`
- Create: `app/src/test/java/com/example/rokidsettingshub/model/DeviceInfoStateTest.kt`

- [ ] Write failing tests for formatting and unavailable fallbacks
- [ ] Run the tests to verify they fail
- [ ] Implement the minimal state/source helpers
- [ ] Run the tests to verify they pass

### Task 2: Expose device info from the view model

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/viewmodel/HubViewModel.kt`
- Modify: `app/src/test/java/com/example/rokidsettingshub/viewmodel/HubViewModelTest.kt`

- [ ] Write a failing view-model test for initial device info exposure
- [ ] Run the targeted test to verify it fails
- [ ] Implement the minimal view-model wiring
- [ ] Run the targeted test to verify it passes

## Chunk 2: Device Info UI

### Task 3: Replace the placeholder with a dedicated screen

**Files:**
- Create: `app/src/main/java/com/example/rokidsettingshub/ui/hub/DeviceInfoScreen.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] Write a failing UI-facing unit test for the screen copy or formatting helper
- [ ] Run the targeted test to verify it fails
- [ ] Implement the minimal screen and hub routing
- [ ] Run the targeted tests and `:app:assembleDebug`

### Task 4: Validate the full feature

**Files:**
- Verify only

- [ ] Run `:app:testDebugUnitTest`
- [ ] Run `:app:assembleDebug`
- [ ] Install to the emulator and confirm the `Device Info` screen shows storage data
