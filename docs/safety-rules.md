# Safety Rules

These rules are intentionally stricter than normal Android app development because this project targets Rokid AI Glasses hardware.

## Source Constraints

This file was derived from:

- [cxr-s-phase1-safe-validation.md](C:\Users\W\Rokid%20project\docs\cxr-s-phase1-safe-validation.md)
- [phase2-glasses-app-shell-design.md](C:\Users\W\Rokid%20project\docs\phase2-glasses-app-shell-design.md)

## v1 Safety Boundary

- No firmware flashing
- No bootloader changes
- No root
- No system app disable or uninstall
- No CXR-S integration in v1
- No camera permission
- No microphone permission
- No long-running background service
- No video or image processing
- No heavy rendering
- No stress or soak testing

## Runtime Limits

- Keep validation runs short
- Prefer simple Compose UI over animation-heavy UI
- Avoid repeated install-restart loops unless necessary
- Stop immediately if the glasses become unstable or hot

## Bluetooth Safety Rules

- Never auto-forget the current `Main Phone`
- Require an explicit confirmation before forgetting any device
- Treat PIN entry and pairing confirmation as system-owned flows
- Avoid hidden destructive actions

## Product Safety Rules

- v1 is a `Settings Hub`, not a system settings replacement
- `Bluetooth` is the only fully functional section in v1
- `Wi-Fi`, `Battery`, and `Device Info` should stay low-risk

