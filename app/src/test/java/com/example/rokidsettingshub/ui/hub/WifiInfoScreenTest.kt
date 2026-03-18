package com.example.rokidsettingshub.ui.hub

import androidx.compose.ui.graphics.Color
import com.example.rokidsettingshub.model.WifiInfoState
import org.junit.Assert.assertEquals
import org.junit.Test

class WifiInfoScreenTest {

    @Test
    fun overviewPageShowsTopThreeWifiEntries() {
        val entries = wifiInfoEntries(
            state = WifiInfoState(
                hardware = "Present",
                wifiState = "Enabled",
                connection = "Connected",
                networkName = "AndroidWifi",
                ipAddress = "10.0.2.16",
                interfaceName = "wlan0",
            ),
            page = WifiInfoPage.Overview,
        )

        assertEquals(
            listOf(
                WifiInfoEntry("Hardware", "Present"),
                WifiInfoEntry("Wi-Fi State", "Enabled"),
                WifiInfoEntry("Connection", "Connected"),
            ),
            entries,
        )
    }

    @Test
    fun detailsPageShowsRemainingWifiEntries() {
        val entries = wifiInfoEntries(
            state = WifiInfoState(
                hardware = "Present",
                wifiState = "Enabled",
                connection = "Connected",
                networkName = "AndroidWifi",
                ipAddress = "10.0.2.16",
                interfaceName = "wlan0",
            ),
            page = WifiInfoPage.Details,
        )

        assertEquals(
            listOf(
                WifiInfoEntry("Network Name", "AndroidWifi"),
                WifiInfoEntry("IP Address", "10.0.2.16"),
                WifiInfoEntry("Interface", "wlan0"),
            ),
            entries,
        )
    }

    @Test
    fun nextPageStopsAtLastWifiPage() {
        assertEquals(WifiInfoPage.Details, nextWifiInfoPage(WifiInfoPage.Overview))
        assertEquals(WifiInfoPage.Details, nextWifiInfoPage(WifiInfoPage.Details))
    }

    @Test
    fun previousPageStopsAtFirstWifiPage() {
        assertEquals(WifiInfoPage.Overview, previousWifiInfoPage(WifiInfoPage.Details))
        assertEquals(WifiInfoPage.Overview, previousWifiInfoPage(WifiInfoPage.Overview))
    }

    @Test
    fun wifiInfoVisualStyleUsesWhiteTextOnBlackBackground() {
        val style = wifiInfoVisualStyle()

        assertEquals(Color.White, style.titleColor)
        assertEquals(Color.White.copy(alpha = 0.82f), style.bodyColor)
        assertEquals(Color.White.copy(alpha = 0.72f), style.labelColor)
        assertEquals(Color.White, style.valueColor)
    }
}
