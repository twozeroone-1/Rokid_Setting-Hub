package com.example.rokidsettingshub.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WifiInfoStateTest {

    @Test
    fun snapshotFormatsConnectedWifiValues() {
        val state = WifiInfoState.fromSnapshot(
            WifiInfoSnapshot(
                hardwarePresent = true,
                enabled = true,
                connected = true,
                ssid = "\"AndroidWifi\"",
                ipAddress = "10.0.2.16",
                interfaceName = "wlan0",
            ),
        )

        assertEquals("Present", state.hardware)
        assertEquals("Enabled", state.wifiState)
        assertEquals("Connected", state.connection)
        assertEquals("AndroidWifi", state.networkName)
        assertEquals("10.0.2.16", state.ipAddress)
        assertEquals("wlan0", state.interfaceName)
    }

    @Test
    fun snapshotFormatsDisabledWifiValues() {
        val state = WifiInfoState.fromSnapshot(
            WifiInfoSnapshot(
                hardwarePresent = true,
                enabled = false,
                connected = false,
                ssid = null,
                ipAddress = null,
                interfaceName = "wlan0",
            ),
        )

        assertEquals("Present", state.hardware)
        assertEquals("Disabled", state.wifiState)
        assertEquals("Not connected", state.connection)
        assertEquals("Unavailable", state.networkName)
        assertEquals("Unavailable", state.ipAddress)
        assertEquals("wlan0", state.interfaceName)
    }

    @Test
    fun snapshotFallsBackToUnavailableWhenValuesAreMissing() {
        val state = WifiInfoState.fromSnapshot(
            WifiInfoSnapshot(
                hardwarePresent = null,
                enabled = null,
                connected = null,
                ssid = null,
                ipAddress = null,
                interfaceName = null,
            ),
        )

        assertEquals("Unavailable", state.hardware)
        assertEquals("Unavailable", state.wifiState)
        assertEquals("Unavailable", state.connection)
        assertEquals("Unavailable", state.networkName)
        assertEquals("Unavailable", state.ipAddress)
        assertEquals("Unavailable", state.interfaceName)
    }
}
