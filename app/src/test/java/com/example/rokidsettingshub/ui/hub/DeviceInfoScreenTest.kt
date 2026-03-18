package com.example.rokidsettingshub.ui.hub

import androidx.compose.ui.graphics.Color
import com.example.rokidsettingshub.model.DeviceInfoState
import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceInfoScreenTest {

    @Test
    fun overviewPageShowsTopThreeEntries() {
        val entries = deviceInfoEntries(
            DeviceInfoState(
                modelName = "Rokid Glasses",
                androidVersion = "Android 12",
                totalStorage = "32.0 GB",
                usedStorage = "20.0 GB",
                freeStorage = "12.0 GB",
            ),
            page = DeviceInfoPage.Overview,
        )

        assertEquals(
            listOf(
                DeviceInfoEntry("Model", "Rokid Glasses"),
                DeviceInfoEntry("Android Version", "Android 12"),
                DeviceInfoEntry("Total Storage", "32.0 GB"),
            ),
            entries,
        )
    }

    @Test
    fun storagePageShowsRemainingEntries() {
        val entries = deviceInfoEntries(
            DeviceInfoState(
                modelName = "Rokid Glasses",
                androidVersion = "Android 12",
                totalStorage = "32.0 GB",
                usedStorage = "20.0 GB",
                freeStorage = "12.0 GB",
            ),
            page = DeviceInfoPage.Storage,
        )

        assertEquals(
            listOf(
                DeviceInfoEntry("Used Storage", "20.0 GB"),
                DeviceInfoEntry("Free Storage", "12.0 GB"),
            ),
            entries,
        )
    }

    @Test
    fun nextPageStopsAtLastPage() {
        assertEquals(DeviceInfoPage.Storage, nextDeviceInfoPage(DeviceInfoPage.Overview))
        assertEquals(DeviceInfoPage.Storage, nextDeviceInfoPage(DeviceInfoPage.Storage))
    }

    @Test
    fun previousPageStopsAtFirstPage() {
        assertEquals(DeviceInfoPage.Overview, previousDeviceInfoPage(DeviceInfoPage.Storage))
        assertEquals(DeviceInfoPage.Overview, previousDeviceInfoPage(DeviceInfoPage.Overview))
    }

    @Test
    fun deviceInfoVisualStyleUsesWhiteTextOnBlackBackground() {
        val style = deviceInfoVisualStyle()

        assertEquals(Color.White, style.titleColor)
        assertEquals(Color.White.copy(alpha = 0.82f), style.bodyColor)
        assertEquals(Color.White.copy(alpha = 0.72f), style.labelColor)
        assertEquals(Color.White, style.valueColor)
    }
}
