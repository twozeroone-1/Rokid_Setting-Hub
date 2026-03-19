# Submenu Carousel Design

**Date:** 2026-03-19

**Goal:** Replace the current vertical submenu section list with a horizontal carousel that matches the home screen interaction model while preserving strong orientation cues about previous and next items.

## Problem

The current Bluetooth submenu uses a vertical list of large cards. That layout works for scanning all items at once, but it breaks the mental model established by the hub home screen, which already uses a horizontal card carousel. It also makes navigation feel inconsistent: the user enters a large horizontal selector, then immediately has to switch to vertical movement rules inside the section.

The main user request is not just “make it horizontal.” The submenu should also expose where the user is in the sequence. Seeing a sliver of the previous and next cards helps the user infer that more content exists and roughly how close they are to the end.

## Chosen Direction

Use a `2.5-card` horizontal carousel for submenu section selection.

- The selected submenu card sits in the center and remains visually dominant.
- The previous and next cards remain partially visible at the left and right edges.
- The carousel advances one item per key press.
- `Enter` opens the detail screen for the selected submenu item.
- `Back` exits to the hub home screen.

This keeps the selection layer consistent with the home screen while still giving users strong directional hints.

## Why This Direction

### Rejected option: 1.5-card focused carousel

This keeps attention strongly centered, but the next item is only weakly visible. It reduces orientation and makes the submenu feel shorter or more abrupt than it is.

### Rejected option: flatter 2.5-card layout

Showing more of neighboring cards improves predictability, but it weakens the focus signal for the current card. In a glasses-first UI, selection clarity matters more than raw information density.

### Selected option: 2.5-card hint-first carousel

This gives the best balance:

- clear current focus
- clear previous/next hints
- consistent interaction with the home carousel
- easier estimation of progress through submenu items

## Interaction Model

### Selection layer

Applies to submenu section selection for Bluetooth first, then the rest of the submenu-style sections.

- `Left`: move to previous submenu card
- `Right`: move to next submenu card
- `Enter` / center key: open the selected submenu detail
- `Back`: return to hub home

### Detail layer

Detail screens stay as separate screens instead of becoming another carousel.

- The carousel is for choosing a section.
- The detail screen is for reading, acting, or drilling deeper.
- `Back` returns from detail to submenu carousel.

This separation keeps navigation predictable and avoids nesting multiple horizontal navigation systems in one layer.

## Layout Rules

### Carousel proportions

- Center card should occupy roughly `56%` of the available width.
- Side hint cards should occupy roughly `22%` each.
- The center card must retain a visibly stronger selection treatment than side cards.

The exact numbers can shift slightly during implementation, but the visual outcome should preserve the “2.5 cards visible” effect.

### Visual hierarchy

- Center card uses the strongest border emphasis.
- Side cards are still readable enough to telegraph their identity.
- Side cards should feel intentionally clipped, not accidentally cut off.
- Motion should move the track, not jump focus invisibly.

### Responsiveness

- On narrow emulator/device widths, keep the same pattern rather than collapsing back to a full single-card pager.
- If width pressure becomes too strong, reduce card padding before abandoning the 2.5-card structure.

## Architectural Direction

Create a reusable submenu carousel component instead of baking the behavior into Bluetooth only.

Recommended shape:

- a focused, reusable composable in `ui/common` or another shared UI package
- accepts item list, selected index, title/body mapping, and activation callback
- owns the horizontal list state and visual selection treatment
- keeps screen-specific detail content outside the shared carousel

Bluetooth becomes the first adopter. Wi-Fi, Battery, and Device Info can then use the same selection shell if their screen structure benefits from the same interaction model.

## Implementation Constraints

- Preserve the existing detail-screen behavior.
- Do not expand scope into unrelated Bluetooth behavior changes.
- Prefer reusing the visual language already used by `SectionCard`.
- Keep focus movement state in the existing `HubViewModel` / Bluetooth focus model unless a small extraction clearly improves reuse.

## Testing Strategy

Implementation should verify:

- moving right advances exactly one submenu card
- moving left goes back exactly one submenu card
- the selected card remains centered or visually dominant
- side cards remain partially visible
- `Enter` still opens the correct detail screen
- `Back` still exits the carousel or the detail screen appropriately

At minimum, add logic/unit coverage where possible and run the app on the emulator for manual confirmation of the 2.5-card presentation.

## Rollout

1. Build a reusable horizontal submenu carousel shell.
2. Convert Bluetooth submenu selection to the new shell.
3. Verify detail entry/exit still works.
4. Evaluate applying the same shell to Wi-Fi, Battery, and Device Info.
