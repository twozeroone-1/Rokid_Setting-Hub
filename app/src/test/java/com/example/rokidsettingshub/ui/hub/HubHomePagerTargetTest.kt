package com.example.rokidsettingshub.ui.hub

import com.example.rokidsettingshub.model.HubSection
import org.junit.Assert.assertEquals
import org.junit.Test

class HubHomePagerTargetTest {

    @Test
    fun eachSectionMapsToItsOwnHorizontalPage() {
        assertEquals(0, hubHomePageTarget(HubSection.Bluetooth))
        assertEquals(1, hubHomePageTarget(HubSection.WiFi))
        assertEquals(2, hubHomePageTarget(HubSection.Battery))
        assertEquals(3, hubHomePageTarget(HubSection.DeviceInfo))
    }

    @Test
    fun homeCarouselGeometryMatchesTwoPointFiveCardLayout() {
        val geometry = hubHomeCarouselGeometry()

        assertEquals(0.56f, geometry.centerCardFraction)
        assertEquals(0.22f, geometry.sidePeekFraction)
    }
}
