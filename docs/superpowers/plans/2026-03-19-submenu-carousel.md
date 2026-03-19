# Submenu Carousel Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert submenu section selection from a vertical list to a shared horizontal 2.5-card carousel that matches the hub home interaction model.

**Architecture:** Add a reusable submenu carousel composable for horizontal section selection, then migrate Bluetooth to it first. Keep detail screens separate from the carousel so selection and detail behavior remain distinct and predictable.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, existing `HubViewModel` focus state, JUnit unit tests, emulator-based manual verification

---

## File Structure

- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`
  Responsibility: replace the overview section list with the shared carousel shell and preserve Bluetooth detail behavior.
- Create: `app/src/main/java/com/example/rokidsettingshub/ui/common/SubmenuCarousel.kt`
  Responsibility: reusable horizontal 2.5-card selection shell for submenu screens.
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/common/SectionCard.kt`
  Responsibility: support the visual treatment needed by clipped side cards if the shared carousel needs card sizing or variant hooks.
- Create or modify: `app/src/test/java/com/example/rokidsettingshub/ui/common/SubmenuCarouselPolicyTest.kt`
  Responsibility: pure logic tests for carousel sizing helpers / paging target rules if extracted.
- Modify: `app/src/test/java/com/example/rokidsettingshub/ui/hub/HubHomePagerTargetTest.kt`
  Responsibility: pattern reference if shared page-targeting helpers are reused.

## Chunk 1: Shared Carousel Shell

### Task 1: Define carousel geometry helpers

**Files:**
- Create: `app/src/test/java/com/example/rokidsettingshub/ui/common/SubmenuCarouselPolicyTest.kt`
- Create: `app/src/main/java/com/example/rokidsettingshub/ui/common/SubmenuCarousel.kt`

- [ ] **Step 1: Write the failing test**

Add tests for any extracted helper that defines the selected card width fraction, side card width fraction, or page target calculation.

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: FAIL because the helper or behavior does not exist yet.

- [ ] **Step 3: Write minimal implementation**

Create the shared helper/composable API with the smallest set of parameters needed for:
- current selected index
- list of items
- previous/next movement callbacks
- activate callback

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: PASS for the new carousel helper tests.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/rokidsettingshub/ui/common/SubmenuCarousel.kt app/src/test/java/com/example/rokidsettingshub/ui/common/SubmenuCarouselPolicyTest.kt
git commit -m "feat: add submenu carousel shell"
```

### Task 2: Implement the reusable horizontal carousel composable

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/common/SubmenuCarousel.kt`
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/common/SectionCard.kt`

- [ ] **Step 1: Write the failing test**

If a pure helper can cover carousel item sizing or selection state mapping, add that failing test first. Do not start with UI-only code if a logic seam exists.

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: FAIL on the newly added helper expectation.

- [ ] **Step 3: Write minimal implementation**

Implement a `LazyRow`-based carousel with:
- `userScrollEnabled = false`
- side padding that reveals adjacent cards
- selected card emphasis
- animated scroll to the selected item

Only add the minimum SectionCard flexibility needed for clipped edge presentation.

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/rokidsettingshub/ui/common/SubmenuCarousel.kt app/src/main/java/com/example/rokidsettingshub/ui/common/SectionCard.kt app/src/test/java/com/example/rokidsettingshub/ui/common/SubmenuCarouselPolicyTest.kt
git commit -m "feat: implement submenu carousel layout"
```

## Chunk 2: Bluetooth Migration

### Task 3: Replace Bluetooth overview list with carousel selection

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`
- Test: `app/src/test/java/com/example/rokidsettingshub/ui/common/SubmenuCarouselPolicyTest.kt`

- [ ] **Step 1: Write the failing test**

Add or extend a logic-level test for any extracted index/page-target helper used by Bluetooth section selection.

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: FAIL until the Bluetooth screen uses the new helper behavior.

- [ ] **Step 3: Write minimal implementation**

Update the Bluetooth overview to:
- render the shared submenu carousel
- map `Left/Right` to focus movement
- keep `Enter` activating the selected section
- keep `Back` returning to the hub

Remove the now-unneeded vertical list scrolling logic from the Bluetooth overview.

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt app/src/test/java/com/example/rokidsettingshub/ui/common/SubmenuCarouselPolicyTest.kt
git commit -m "feat: move bluetooth submenu to carousel"
```

### Task 4: Preserve Bluetooth detail navigation

**Files:**
- Modify: `app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt`

- [ ] **Step 1: Write the failing test**

If there is a logic seam for Bluetooth focus transitions, add a test covering:
- overview `Enter` opens detail for the selected section
- detail `Back` returns to overview

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: FAIL if the transition helper is not yet covered.

- [ ] **Step 3: Write minimal implementation**

Ensure detail screens remain separate and that no horizontal carousel logic leaks into the detail layer.

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt
git commit -m "fix: preserve bluetooth detail transitions"
```

## Chunk 3: Verification and Extension Check

### Task 5: Verify emulator behavior

**Files:**
- Modify: none unless defects are found

- [ ] **Step 1: Install the updated debug build**

Run: `.\gradlew.bat :app:installDebug`
Expected: APK installs successfully.

- [ ] **Step 2: Manually verify carousel behavior**

Check on emulator/device:
- left/right moves one submenu card at a time
- center card is dominant
- side cards remain visible
- enter opens detail
- back returns correctly

- [ ] **Step 3: Run regression tests**

Run: `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug`
Expected: PASS

- [ ] **Step 4: Decide extension scope**

If the Bluetooth carousel feels correct, note whether the same shell should be applied next to Wi-Fi, Battery, and Device Info or deferred to a follow-up change.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/rokidsettingshub/ui/bluetooth/BluetoothScreen.kt app/src/main/java/com/example/rokidsettingshub/ui/common/SubmenuCarousel.kt app/src/main/java/com/example/rokidsettingshub/ui/common/SectionCard.kt app/src/test/java/com/example/rokidsettingshub/ui/common/SubmenuCarouselPolicyTest.kt
git commit -m "feat: ship submenu carousel interaction"
```
