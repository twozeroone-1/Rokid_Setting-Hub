# Home Carousel Peek Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the hub home screen show approximately 2.5 section cards at once while preserving existing movement and activation behavior.

**Architecture:** Keep the current `HubHome` composable and `LazyRow`, but replace the full-width item layout with explicit geometry that matches the submenu carousel. Cover the geometry with a pure helper test, then verify the visual result in the emulator.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, JUnit unit tests, Android emulator verification

---

## File Structure

- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`
  Responsibility: define the home carousel geometry helper and apply the 2.5-card layout to the hub home `LazyRow`.
- Modify: `app/src/test/java/com/example/rokidsettingshub/ui/hub/HubHomePagerTargetTest.kt`
  Responsibility: cover the home page target helper and the new carousel geometry helper.

## Chunk 1: Geometry and Tests

### Task 1: Add a failing geometry test

**Files:**
- Modify: `app/src/test/java/com/example/rokidsettingshub/ui/hub/HubHomePagerTargetTest.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`

- [ ] **Step 1: Write the failing test**

Add a test that expects the hub home carousel geometry to expose a dominant center card and visible side peeks.

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests com.example.rokidsettingshub.ui.hub.HubHomePagerTargetTest`
Expected: FAIL because the geometry helper does not exist yet.

- [ ] **Step 3: Write minimal implementation**

Add a pure helper in `HubScreen.kt` for the home carousel geometry.

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests com.example.rokidsettingshub.ui.hub.HubHomePagerTargetTest`
Expected: PASS

## Chunk 2: Hub Home Layout

### Task 2: Apply the 2.5-card geometry to the home `LazyRow`

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/hub/HubScreen.kt`

- [ ] **Step 1: Keep the current key behavior**

Do not change how `Left/Right/Up/Down/Enter` work in `HubHome`.

- [ ] **Step 2: Replace full-width cards with explicit geometry**

Use:
- fixed card width derived from the new helper
- symmetric edge padding so first and last cards still center correctly
- the same spacing feel as the submenu carousel

- [ ] **Step 3: Keep selected item scroll behavior intact**

Ensure changing the selected section still scrolls the `LazyRow` to the correct item.

## Chunk 3: Verification

### Task 3: Verify unit tests, build, install, and emulator behavior

**Files:**
- Modify: none unless defects are found

- [ ] **Step 1: Run regression checks**

Run: `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:installDebug`
Expected: PASS

- [ ] **Step 2: Manually verify the emulator**

Confirm:
- the home screen shows neighboring cards
- moving right advances one card at a time
- the selected card remains visually dominant
- `Enter` still opens the expected section

- [ ] **Step 3: Report exact verification evidence**

Capture the commands run and summarize the observed emulator behavior in the completion note.
