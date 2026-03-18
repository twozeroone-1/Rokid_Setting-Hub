package com.example.rokidsettingshub.model

data class WifiInfoSnapshot(
    val hardwarePresent: Boolean?,
    val enabled: Boolean?,
    val connected: Boolean?,
    val ssid: String?,
    val ipAddress: String?,
    val interfaceName: String?,
)

data class WifiInfoState(
    val hardware: String = UNAVAILABLE_TEXT,
    val wifiState: String = UNAVAILABLE_TEXT,
    val connection: String = UNAVAILABLE_TEXT,
    val networkName: String = UNAVAILABLE_TEXT,
    val ipAddress: String = UNAVAILABLE_TEXT,
    val interfaceName: String = UNAVAILABLE_TEXT,
) {
    companion object {
        const val UNAVAILABLE_TEXT = "Unavailable"

        fun fromSnapshot(snapshot: WifiInfoSnapshot): WifiInfoState = WifiInfoState(
            hardware = formatHardware(snapshot.hardwarePresent),
            wifiState = formatWifiState(snapshot.enabled),
            connection = formatConnection(snapshot.connected),
            networkName = snapshot.ssid
                ?.trim()
                ?.removeSurrounding("\"")
                ?.takeUnless { it.isBlank() || it == "<unknown ssid>" }
                ?: UNAVAILABLE_TEXT,
            ipAddress = snapshot.ipAddress?.takeUnless { it.isBlank() } ?: UNAVAILABLE_TEXT,
            interfaceName = snapshot.interfaceName?.takeUnless { it.isBlank() } ?: UNAVAILABLE_TEXT,
        )
    }
}

private fun formatHardware(hardwarePresent: Boolean?): String = when (hardwarePresent) {
    true -> "Present"
    false -> "Not available"
    null -> WifiInfoState.UNAVAILABLE_TEXT
}

private fun formatWifiState(enabled: Boolean?): String = when (enabled) {
    true -> "Enabled"
    false -> "Disabled"
    null -> WifiInfoState.UNAVAILABLE_TEXT
}

private fun formatConnection(connected: Boolean?): String = when (connected) {
    true -> "Connected"
    false -> "Not connected"
    null -> WifiInfoState.UNAVAILABLE_TEXT
}
