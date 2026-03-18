# Rokid Settings Hub Design

Date: 2026-03-18

## Goal

Create a separate glasses-only Android app that behaves like a lightweight system settings hub.

## Product Direction

- Independent repository and independent app
- `Settings Hub` shell structure from day one
- `Bluetooth` is the only fully implemented section in v1
- `Wi-Fi`, `Battery`, and `Device Info` exist as placeholders or read-only screens

## Key Product Decisions

- Do not put this feature set inside `RokidAIAssistant`
- Do not depend on `CXR-S` for v1
- Protect the user-selected `Main Phone`
- Allow users to:
  - scan
  - pair
  - connect
  - disconnect
  - forget
  - mark a device as `Main Phone`
- Block `Forget` on the current `Main Phone`

## Why Separate Repository

- Clear product boundary
- Lower risk to the existing AI assistant app
- Easier experimentation with Bluetooth UX
- Easier handoff and future reuse

## v1 UX Structure

### Hub Home

- `Bluetooth`
- `Wi-Fi`
- `Battery`
- `Device Info`

### Bluetooth Screen

- Bluetooth on/off state
- Current `Main Phone`
- `My Devices`
- `Available Devices`
- `Scan` action

### Device Detail / Action Sheet

Show only actions valid for the selected device:

- `Pair`
- `Connect`
- `Disconnect`
- `Set as Main Phone`
- `Forget`

## Device States

- `Main Phone`
- `Connected`
- `Paired`
- `Available`

## Device Categories

- `Phone`
- `Audio`
- `Keyboard/Mouse`
- `Remote/HID`
- `Unknown`

These categories are primarily for badges and icons in v1. They do not introduce profile-specific settings yet.

## Main Phone Rules

- User can assign any paired or connected phone as the `Main Phone`
- Only one device can be `Main Phone`
- `Main Phone` can be disconnected
- `Main Phone` cannot be forgotten
- If a new main phone is selected, the old one loses the badge immediately

## Input Model

The UI is optimized for limited glasses input:

- Up/down: move through lists
- Enter/tap: select item or confirm
- Back: return
- Long press: optional shortcut only, never the only way to access a feature

## Technical Boundary

The app directly owns:

- bonded device list
- scan results
- connect/disconnect requests
- forget requests
- local persistence for `Main Phone`

The system still owns:

- pairing confirmation
- PIN entry
- profile negotiation
- vendor-specific secure pairing behavior

## References Used

- [cxr-s-phase1-safe-validation.md](C:\Users\W\Rokid%20project\docs\cxr-s-phase1-safe-validation.md)
- [phase2-glasses-app-shell-design.md](C:\Users\W\Rokid%20project\docs\phase2-glasses-app-shell-design.md)

