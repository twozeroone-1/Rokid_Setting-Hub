package com.example.rokidsettingshub.ui.hub

import com.example.rokidsettingshub.model.HubSection
import org.junit.Assert.assertEquals
import org.junit.Test

class HubHomeScrollTargetTest {

    @Test
    fun firstTwoSectionsKeepTheListAtTheTop() {
        assertEquals(0, hubHomeScrollTarget(HubSection.Bluetooth))
        assertEquals(0, hubHomeScrollTarget(HubSection.WiFi))
    }

    @Test
    fun lowerSectionsShiftTheViewportDownByOneCard() {
        assertEquals(1, hubHomeScrollTarget(HubSection.Battery))
        assertEquals(2, hubHomeScrollTarget(HubSection.DeviceInfo))
    }
}
