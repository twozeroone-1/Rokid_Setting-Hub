# Rokid Input Fallback Design

Date: 2026-03-18

## Goal

Refine `Rokid Settings Hub` so it behaves predictably on `Rokid Glasses` when the side-touch input is imprecise, while keeping power usage conservative and preserving Wi-Fi as a constrained but real device capability.

## Problem Statement

Current app behavior is usable on paper but still risky on the glasses:

- side-touch navigation can skip more than one visual target
- long scroll surfaces are hard to control on the glasses
- a bright full-screen app background wastes power on a battery-constrained device
- Wi-Fi should not be removed because the hardware exists, but its UX must acknowledge device limitations
- external Bluetooth HID input should be treated as a practical fallback, not as an edge case

## Device Constraints

- Display target is `480 x 640`
- Input is limited and can be noisy
- The device battery is small enough that always painting large bright surfaces is undesirable
- Bluetooth is a core system path between glasses and phone
- Wi-Fi exists on the device, but connection flow may require system confirmation and awkward password entry

## Community Findings

### Rokid Forum

- In `rokid glassess设置WIFI`, posted on `2025-10-05`, a user reports that Wi-Fi setup opens but password entry on the glasses is difficult.
- The accepted reply from `2025-10-09` says `WifiNetworkSpecifier` can connect to a known network, but the flow still shows a system confirmation dialog that must be accepted manually.

### Reddit

- In `Rokid Glasses & Keyboard Mapping, it work!`, posted in December 2025, a user reports successful Bluetooth keyboard use with arrow keys, `Enter`, `Tab`, and brightness shortcuts.
- In `Bluetooth in Rokid glasses??`, posted in February 2026, users report Bluetooth peripherals such as small rings, game controllers, watches running `WowMouse`, and page-turner accessories working with the glasses.
- These are community reports, not official API guarantees, but they strongly support a HID-friendly navigation model.

## Product Decisions

- Keep `Wi-Fi` in the hub
- Treat `Bluetooth HID` as a first-class fallback input path
- Prefer `focus movement` over free scrolling on the main Bluetooth screen
- Never require a long press or gesture timing trick as the only way to reach a feature
- Keep background rendering sparse and dark to reduce unnecessary display power use

## Interaction Model

### Bluetooth Home

The Bluetooth landing view uses a fixed focus list instead of a scroll-heavy card stack:

- `Status`
- `Main Phone`
- `My Devices`
- `Available Devices`
- `Scan`

Rules:

- one directional input moves focus by exactly one section
- `Enter` or tap opens the focused section only
- no inertial scrolling on the landing screen
- only one visual focus target is emphasized at a time

### Detail Screens

Detail screens stay shallow and action-oriented:

- show at most three primary actions at once
- preserve deterministic ordering
- reserve destructive actions for explicit confirmation states
- `Back` closes detail first, then returns to the hub

## Input Fallback Layers

### Layer 1: Side-Touch

- debounce repeated directional input
- avoid nested scroll containers
- avoid dense vertical lists when a single-focus flow can work

### Layer 2: Bluetooth HID

- support the same navigation model for keyboard, ring, watch, or controller input
- treat `Arrow`, `Enter`, `Tab`, and `Back` as canonical navigation actions
- avoid any UI behavior that only works with touch precision

### Layer 3: Numbered / Short Action Lists

- when detail screens are needed, keep the action count low enough that the user can recover from an accidental move
- keep labels short and visually distinct

## Wi-Fi Policy

- Do not remove the `Wi-Fi` section
- Keep v1 Wi-Fi scope conservative: state visibility and placeholder messaging are acceptable
- Any future connection flow must acknowledge that password entry and confirmation are system-constrained on the glasses

Suggested user-facing stance:

- Wi-Fi hardware is present
- some networks require manual confirmation
- v1 does not promise a full custom network setup flow

## Power and Visual Policy

- do not paint full-screen bright surfaces unless a screen truly needs it
- prefer dark window background with content-sized surfaces
- avoid decorative gradients, glow, or large animated fills
- keep emphasis local to the focused item, not the whole screen

## Testing Strategy

- unit tests for focus movement and debounce behavior in `HubViewModel`
- unit tests for Bluetooth repository state transitions under HID-friendly navigation assumptions
- manual validation on `Rokid Glasses` for side-touch, `Back`, and Bluetooth peripheral input
- manual validation that Wi-Fi placeholder language is accurate and non-misleading

## Non-Goals

- building a full custom Wi-Fi password entry flow in v1
- adding gesture-specific vendor integration beyond standard Android input handling
- assuming every Bluetooth ring or controller is compatible

## References Used

- [rokid glassess设置WIFI](https://forum.rokid.com/post/detail/2203)
- [Rokid Glasses & Keyboard Mapping, it work!](https://www.reddit.com/r/rokid_official/comments/1pepbwd/rokid_glasses_keyboard_mapping_it_work/)
- [Bluetooth in Rokid glasses??](https://www.reddit.com/r/rokid_official/comments/1rg7dox/bluetooth_in_rokid_glasses/)
- [Ring for Rokid AI glasses](https://www.reddit.com/r/rokid_official/comments/1qjoj85/ring_for_rokid_ai_glasses/)
- [New Way to Control Your Glasses.](https://www.reddit.com/r/rokid_official/comments/1ows719/new_way_to_control_your_glasses/)
