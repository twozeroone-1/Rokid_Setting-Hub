package com.example.rokidsettingshub.ui.hub

import com.example.rokidsettingshub.R
import com.example.rokidsettingshub.model.HubSection

data class SectionCopy(
    val cardBodyResId: Int,
    val placeholderBodyResId: Int,
)

internal fun sectionCopyFor(section: HubSection): SectionCopy = when (section) {
    HubSection.Bluetooth -> SectionCopy(
        cardBodyResId = R.string.section_bluetooth_card_body,
        placeholderBodyResId = R.string.bluetooth_placeholder_body,
    )
    HubSection.WiFi -> SectionCopy(
        cardBodyResId = R.string.section_wifi_card_body,
        placeholderBodyResId = R.string.section_wifi_placeholder_body,
    )
    HubSection.Battery -> SectionCopy(
        cardBodyResId = R.string.section_battery_card_body,
        placeholderBodyResId = R.string.section_battery_placeholder_body,
    )
    HubSection.DeviceInfo -> SectionCopy(
        cardBodyResId = R.string.section_device_info_card_body,
        placeholderBodyResId = R.string.section_device_info_placeholder_body,
    )
}
