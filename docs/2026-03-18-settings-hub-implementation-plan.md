# Rokid Settings Hub Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a separate glasses-first `Settings Hub` Android app where `Bluetooth` is fully functional in v1 and the other sections are shell screens only.

**Architecture:** Start with a single Android app module using Compose and a small state-driven shell. Keep Bluetooth logic isolated in focused packages and explicitly avoid `CXR-S`, camera, microphone, or long-running services in v1.

**Tech Stack:** Kotlin, Android SDK, Jetpack Compose, ViewModel, StateFlow, SharedPreferences or DataStore, Android Bluetooth APIs, JUnit

---

## File Structure

Planned file layout for the first implementation:

- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/example/rokidsettingshub/MainActivity.kt`
- `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`
- `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`
- `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/DeviceActionSheet.kt`
- `app/src/main/java/com/example/rokidsettingshub/ui/common/SectionCard.kt`
- `app/src/main/java/com/example/rokidsettingshub/viewmodel/HubViewModel.kt`
- `app/src/main/java/com/example/rokidsettingshub/model/HubSection.kt`
- `app/src/main/java/com/example/rokidsettingshub/model/ManagedDevice.kt`
- `app/src/main/java/com/example/rokidsettingshub/model/DeviceType.kt`
- `app/src/main/java/com/example/rokidsettingshub/model/BluetoothScreenState.kt`
- `app/src/main/java/com/example/rokidsettingshub/data/bluetooth/BluetoothRepository.kt`
- `app/src/main/java/com/example/rokidsettingshub/data/bluetooth/BluetoothScanner.kt`
- `app/src/main/java/com/example/rokidsettingshub/data/storage/MainPhoneStore.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/test/java/com/example/rokidsettingshub/...`

## Chunk 1: Repository Scaffold

### Task 1: Create the Android project skeleton

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle.properties`
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Create the Gradle settings file**
- [ ] **Step 2: Create the root build file**
- [ ] **Step 3: Create the app module build file with Compose enabled**
- [ ] **Step 4: Add only low-risk permissions**
Expected permissions:
  - `BLUETOOTH`
  - `BLUETOOTH_ADMIN` if needed for target
  - `BLUETOOTH_CONNECT`
  - `BLUETOOTH_SCAN`
Do not add:
  - camera
  - microphone
  - internet unless justified later
- [ ] **Step 5: Run `./gradlew :app:assembleDebug`**
Expected: build succeeds
- [ ] **Step 6: Commit**

## Chunk 2: App Shell

### Task 2: Build the `Settings Hub` shell

**Files:**
- Create: `app/src/main/java/com/example/rokidsettingshub/MainActivity.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/ui/common/SectionCard.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/model/HubSection.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/viewmodel/HubViewModel.kt`

- [ ] **Step 1: Write a failing UI-state test for section selection**
- [ ] **Step 2: Run the test and verify it fails**
- [ ] **Step 3: Implement the minimal `HubSection` and `HubViewModel`**
- [ ] **Step 4: Create the `HubScreen` with four sections**
Sections:
  - Bluetooth
  - Wi-Fi
  - Battery
  - Device Info
- [ ] **Step 5: Make `Bluetooth` navigate to a placeholder Bluetooth screen**
- [ ] **Step 6: Run unit tests**
- [ ] **Step 7: Run `./gradlew :app:assembleDebug`**
- [ ] **Step 8: Commit**

### Task 3: Add placeholder screens for non-Bluetooth sections

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/ui/hub/PlaceholderSectionScreen.kt`
- Create: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Add placeholder section routes**
- [ ] **Step 2: Show simple read-only or coming-soon screens**
- [ ] **Step 3: Verify navigation works**
- [ ] **Step 4: Commit**

## Chunk 3: Bluetooth Domain and Storage

### Task 4: Define Bluetooth models and policy

**Files:**
- Create: `app/src/main/java/com/example/rokidsettingshub/model/ManagedDevice.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/model/DeviceType.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/model/BluetoothScreenState.kt`
- Create: `app/src/test/java/com/example/rokidsettingshub/model/ManagedDevicePolicyTest.kt`

- [ ] **Step 1: Write failing tests for `Main Phone` protection**
Required checks:
  - main phone cannot be forgotten
  - a new main phone replaces the old one
  - connect/disconnect capabilities vary by state
- [ ] **Step 2: Run the tests and confirm failure**
- [ ] **Step 3: Implement minimal models and capability helpers**
- [ ] **Step 4: Run the tests and confirm pass**
- [ ] **Step 5: Commit**

### Task 5: Persist `Main Phone`

**Files:**
- Create: `app/src/main/java/com/example/rokidsettingshub/data/storage/MainPhoneStore.kt`
- Create: `app/src/test/java/com/example/rokidsettingshub/data/storage/MainPhoneStoreTest.kt`

- [ ] **Step 1: Write a failing persistence test**
- [ ] **Step 2: Run the test and confirm failure**
- [ ] **Step 3: Implement minimal local storage**
Suggested stored values:
  - device address
  - user-visible name snapshot
- [ ] **Step 4: Run the tests and confirm pass**
- [ ] **Step 5: Commit**

## Chunk 4: Bluetooth Data Layer

### Task 6: Build a low-risk Bluetooth repository

**Files:**
- Create: `app/src/main/java/com/example/rokidsettingshub/data/bluetooth/BluetoothRepository.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/data/bluetooth/BluetoothScanner.kt`
- Create: `app/src/test/java/com/example/rokidsettingshub/data/bluetooth/BluetoothRepositoryContractTest.kt`

- [ ] **Step 1: Write repository contract tests using fakes**
Cover:
  - bonded list load
  - scan result updates
  - connect request dispatch
  - disconnect request dispatch
  - forget blocked for main phone
- [ ] **Step 2: Run the tests and confirm failure**
- [ ] **Step 3: Implement the repository behind interfaces**
- [ ] **Step 4: Keep system-owned pairing flow out of the repository core**
- [ ] **Step 5: Run the tests and confirm pass**
- [ ] **Step 6: Commit**

### Task 7: Integrate pairing and forget boundaries safely

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/data/bluetooth/BluetoothRepository.kt`
- Create: `app/src/test/java/com/example/rokidsettingshub/data/bluetooth/BluetoothSafetyRuleTest.kt`

- [ ] **Step 1: Add tests for forbidden destructive actions**
- [ ] **Step 2: Run the tests and confirm failure**
- [ ] **Step 3: Implement confirmation gating and main-phone protection**
- [ ] **Step 4: Run the tests and confirm pass**
- [ ] **Step 5: Commit**

## Chunk 5: Bluetooth UI

### Task 8: Build the Bluetooth home screen

**Files:**
- Create: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/viewmodel/HubViewModel.kt`
- Create: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Write a UI-state test for Bluetooth sections**
- [ ] **Step 2: Run the test and confirm failure**
- [ ] **Step 3: Implement the Bluetooth screen sections**
Required sections:
  - Bluetooth status
  - Main Phone
  - My Devices
  - Available Devices
  - Scan action
- [ ] **Step 4: Wire the screen to the repository state**
- [ ] **Step 5: Run tests and build**
- [ ] **Step 6: Commit**

### Task 9: Add device action sheet

**Files:**
- Create: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/DeviceActionSheet.kt`
- Create: `app/src/test/java/com/example/rokidsettingshub/ui/bluetooth/DeviceActionSheetPolicyTest.kt`

- [ ] **Step 1: Write a failing test for action visibility**
- [ ] **Step 2: Run the test and confirm failure**
- [ ] **Step 3: Implement action visibility rules**
Examples:
  - `Pair` only for available devices
  - `Connect` only for paired or main-phone devices when disconnected
  - `Forget` hidden for main phone
- [ ] **Step 4: Run tests and confirm pass**
- [ ] **Step 5: Commit**

## Chunk 6: Device Validation

### Task 10: Build and validate on the glasses

**Files:**
- Modify: any files needed after device feedback
- Test: on hardware only

- [ ] **Step 1: Run `./gradlew :app:assembleDebug`**
- [ ] **Step 2: Install on the glasses with `adb install -r`**
- [ ] **Step 3: Launch the app**
- [ ] **Step 4: Verify the hub home renders correctly at `480 x 640`**
- [ ] **Step 5: Verify Bluetooth screen opens and remains responsive**
- [ ] **Step 6: Verify no abnormal heat or instability**
- [ ] **Step 7: Commit**

### Task 11: Validate Bluetooth flows manually

**Files:**
- No code required unless defects are found

- [ ] **Step 1: Verify bonded devices appear**
- [ ] **Step 2: Verify scan results appear**
- [ ] **Step 3: Verify a non-main device can be forgotten**
- [ ] **Step 4: Verify main phone cannot be forgotten**
- [ ] **Step 5: Verify main phone reassignment works**
- [ ] **Step 6: Verify pair/connect/disconnect flows behave as expected**
- [ ] **Step 7: Commit any fixes**

## Test Checklist

- Unit tests for main-phone protection
- Unit tests for action visibility
- Unit tests for local persistence
- Contract tests for Bluetooth repository behavior
- `:app:assembleDebug`
- Real-device validation on Rokid AI Glasses

## Non-Goals to Preserve

- Do not add CXR-S in v1
- Do not add camera or microphone
- Do not add long-running foreground services
- Do not turn this into a full Android Settings clone

## Handoff Notes

- The next agent should start by scaffolding the Android app, not by adding advanced Bluetooth features
- Keep the Bluetooth implementation conservative and hardware-safe
- If any step requires system-level work beyond normal app permissions, stop and reassess

