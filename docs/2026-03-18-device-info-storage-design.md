# Device Info Storage Design

**Goal:** Replace the `Device Info` placeholder with a read-only screen that shows basic device metadata and storage usage on Rokid Glasses.

**Scope:**
- Show `Model`
- Show `Android Version`
- Show storage summary as `Total`, `Used`, and `Free`
- Keep the screen read-only

**Design:**
- Add a small device-info data source that reads `Build.MODEL`, `Build.VERSION.RELEASE`, and filesystem stats from Android.
- Expose a `DeviceInfoState` from `HubViewModel` alongside the existing Bluetooth state.
- Render a dedicated `DeviceInfoScreen` instead of the generic placeholder when `HubSection.DeviceInfo` is selected.
- Keep Wi-Fi and Battery on the existing placeholder path for now.

**Failure Handling:**
- If storage stats cannot be read, show `Unavailable`.
- Format sizes into human-readable binary units (`GB`, `MB`) to fit the 480x640 UI.

**Notes:**
- This keeps the feature narrow and useful without turning `Device Info` into a large diagnostics page.
- Additional fields like memory, build fingerprint, or serial can be added later without changing the section model again.
