package com.example.rokidsettingshub.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class SubmenuCarouselPolicyTest {

    @Test
    fun carouselUsesRecommendedTwoPointFiveCardFractions() {
        val geometry = submenuCarouselGeometry()

        assertEquals(0.56f, geometry.centerCardFraction)
        assertEquals(0.22f, geometry.sidePeekFraction)
    }

    @Test
    fun pageTargetClampsIntoAvailableItemRange() {
        assertEquals(0, submenuCarouselPageTarget(selectedIndex = -3, itemCount = 5))
        assertEquals(2, submenuCarouselPageTarget(selectedIndex = 2, itemCount = 5))
        assertEquals(4, submenuCarouselPageTarget(selectedIndex = 9, itemCount = 5))
    }

    @Test
    fun emptyCarouselFallsBackToFirstPage() {
        assertEquals(0, submenuCarouselPageTarget(selectedIndex = 3, itemCount = 0))
    }
}
