# Rokid Input Fallback Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the settings hub predictable on `Rokid Glasses` by prioritizing fixed-focus navigation, HID-friendly controls, conservative Wi-Fi messaging, and low-power rendering.

**Architecture:** Keep the existing Compose shell and Bluetooth repository, but tighten behavior around a single-focus Bluetooth landing screen and input normalization in `HubViewModel`. Preserve Wi-Fi as a placeholder section with accurate device messaging rather than removing it.

**Tech Stack:** Kotlin, Android SDK, Jetpack Compose, ViewModel, StateFlow, Android Bluetooth APIs, JUnit

---

## File Structure

Target files for this refinement:

- `docs/2026-03-18-rokid-input-fallback-design.md`
- `app/src/main/java/com/example/rokidsettingshub/MainActivity.kt`
- `app/src/main/java/com/example/rokidsettingshub/viewmodel/HubViewModel.kt`
- `app/src/main/java/com/example/rokidsettingshub/model/BluetoothFocusState.kt`
- `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`
- `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/DeviceActionSheet.kt`
- `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`
- `app/src/main/java/com/example/rokidsettingshub/ui/hub/PlaceholderSectionScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/test/java/com/example/rokidsettingshub/viewmodel/HubViewModelTest.kt`
- `app/src/test/java/com/example/rokidsettingshub/data/bluetooth/BluetoothRepositoryContractTest.kt`

## Chunk 1: Focus-First Navigation

### Task 1: Lock Bluetooth home to deterministic focus movement

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/viewmodel/HubViewModel.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/model/BluetoothFocusState.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`
- Test: `app/src/test/java/com/example/rokidsettingshub/viewmodel/HubViewModelTest.kt`

- [ ] **Step 1: Write a failing test for one-step focus movement**
Expected checks:
  - one directional input moves focus by exactly one section
  - focus never skips the first Bluetooth section
  - repeated rapid inputs are debounced
- [ ] **Step 2: Run `./gradlew.bat :app:testDebugUnitTest --tests "com.example.rokidsettingshub.viewmodel.HubViewModelTest"`**
Expected: the new test fails
- [ ] **Step 3: Implement the minimal focus-state and debounce changes**
- [ ] **Step 4: Render the Bluetooth landing screen as a fixed focus list without free scrolling**
- [ ] **Step 5: Re-run the targeted test**
Expected: PASS
- [ ] **Step 6: Commit**

### Task 2: Keep Bluetooth detail screens shallow and recoverable

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/DeviceActionSheet.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Write a failing policy test or extend an existing one for action count and order**
- [ ] **Step 2: Run the targeted test and confirm failure**
- [ ] **Step 3: Limit visible primary actions to the smallest safe set per device state**
- [ ] **Step 4: Verify `Back` closes detail before leaving Bluetooth**
- [ ] **Step 5: Run `./gradlew.bat :app:testDebugUnitTest`**
Expected: PASS
- [ ] **Step 6: Commit**

## Chunk 2: HID-Friendly Controls

### Task 3: Normalize keyboard and HID navigation behavior

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/MainActivity.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/viewmodel/HubViewModel.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`
- Test: `app/src/test/java/com/example/rokidsettingshub/viewmodel/HubViewModelTest.kt`

- [ ] **Step 1: Write failing tests for canonical navigation actions**
Cover:
  - directional movement
  - `Enter` selection
  - `Back` recovery
  - optional `Tab` next-focus behavior if already supported
- [ ] **Step 2: Run the targeted tests and confirm failure**
- [ ] **Step 3: Implement minimal input normalization without vendor-specific APIs**
- [ ] **Step 4: Re-run the targeted tests**
Expected: PASS
- [ ] **Step 5: Build with `./gradlew.bat :app:assembleDebug`**
Expected: build succeeds
- [ ] **Step 6: Commit**

## Chunk 3: Wi-Fi Messaging and Power Policy

### Task 4: Preserve Wi-Fi with accurate constrained messaging

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/hub/PlaceholderSectionScreen.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Write a failing UI-state test if the placeholder strings are covered by tests**
- [ ] **Step 2: Update Wi-Fi copy to reflect real hardware presence and manual-confirmation constraints**
- [ ] **Step 3: Verify the placeholder remains conservative and non-promissory**
- [ ] **Step 4: Run `./gradlew.bat :app:testDebugUnitTest`**
Expected: PASS
- [ ] **Step 5: Commit**

### Task 5: Keep rendering power-aware

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/MainActivity.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`

- [ ] **Step 1: Inspect whether any screen still paints unnecessary full-screen surfaces**
- [ ] **Step 2: Add a focused test only if existing test coverage can express the constraint cheaply**
- [ ] **Step 3: Remove or simplify any remaining full-screen fills**
- [ ] **Step 4: Run `./gradlew.bat :app:assembleDebug`**
Expected: build succeeds
- [ ] **Step 5: Commit**

## Chunk 4: Real-Device Validation

### Task 6: Validate on Rokid Glasses with side-touch and HID input

**Files:**
- Modify: any files needed after validation feedback
- Test: on hardware only

- [ ] **Step 1: Run `./gradlew.bat :app:assembleDebug`**
- [ ] **Step 2: Install with `adb install -r`**
- [ ] **Step 3: Launch the app and verify `MainActivity` is resumed**
- [ ] **Step 4: Validate that side-touch moves exactly one Bluetooth focus row per gesture**
- [ ] **Step 5: Validate that the first Bluetooth section is easy to reacquire**
- [ ] **Step 6: If a Bluetooth keyboard or other HID is available, verify directional movement and selection**
- [ ] **Step 7: Commit any device-driven fixes**

## Test Checklist

- `:app:testDebugUnitTest --tests "com.example.rokidsettingshub.viewmodel.HubViewModelTest"`
- `:app:testDebugUnitTest`
- `:app:assembleDebug`
- manual validation on `Rokid Glasses`
- manual validation of Wi-Fi placeholder wording

## Non-Goals to Preserve

- do not remove the Wi-Fi section
- do not add a full custom Wi-Fi credential flow in v1
- do not require vendor-private input hooks
- do not regress Bluetooth `Main Phone` safety rules

## Handoff Notes

- Prioritize deterministic navigation over adding more features
- If a control pattern only works with precise touch, reject it
- Community reports support Bluetooth HID fallback, but compatibility still varies by device
