# Home Carousel Peek Design

**Date:** 2026-03-19

**Goal:** Adjust the hub home screen so the main section selector shows approximately 2.5 cards at once, matching the submenu carousel affordance without changing hub navigation rules.

## Problem

The submenu screens now show a center-focused horizontal carousel with neighboring cards partially visible. The hub home screen still uses a full-width single-card pager. That makes the entry screen feel visually different from the submenu selection screens even though both use the same directional navigation model.

The user request is specifically about visibility: the main menu should expose the previous and next sections the same way the submenu does, so users can infer what comes next before moving.

## Chosen Direction

Keep the existing hub home behavior and state model, but change the carousel geometry to a `2.5-card` presentation.

- The selected hub card remains the dominant center card.
- The previous and next hub cards stay partially visible at the edges.
- `Left/Right` and `Up/Down` still move one section at a time.
- `Enter` still opens the selected section.
- No section routing or focus ownership changes.

## Why This Direction

### Rejected option: replace Hub home with the shared submenu composable

That would visually unify the two selectors, but it is a larger refactor than necessary. The hub home already has working selection state, routing, and tests. Replacing it would increase risk without adding new behavior.

### Rejected option: keep the single-card pager

This preserves the current implementation, but it leaves the UI inconsistent with the new submenu selection pattern and weakens orientation cues about surrounding sections.

### Selected option: keep the current hub shell and only change geometry

This is the smallest change that satisfies the request. It preserves proven input behavior while aligning the visual density of the home screen with the submenu screens.

## Layout Rules

- Center card should occupy roughly `56%` of the available width.
- Side cards should remain partially visible at both edges.
- Card spacing should stay close to the submenu carousel spacing so the two layers feel related.
- First and last cards should still land in a visually centered position when selected.

The exact fractions can share the submenu geometry if that keeps the visual language aligned.

## Architecture

Keep `HubHome` in `HubScreen.kt` as the owner of:

- hardware key handling
- selected section routing
- `LazyRow` state

Extract any geometry or target helper needed for testing into pure functions inside the same file unless a shared helper clearly reduces duplication.

## Testing Strategy

Add a failing logic-level test first for the home carousel geometry or target helper. Verify:

- the home screen keeps one horizontal page target per section
- the geometry used for the hub exposes the same `2.5-card` affordance as the submenu carousel

Then run unit tests, build the app, and manually verify on the emulator that the home screen shows neighboring cards while still opening the correct section.
