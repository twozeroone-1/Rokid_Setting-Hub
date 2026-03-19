package com.example.rokidsettingshub.ui.hub

import androidx.compose.ui.graphics.Color
import com.example.rokidsettingshub.model.BatteryInfoState
import com.example.rokidsettingshub.ui.common.SubmenuCarouselItem
import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryInfoScreenTest {

    @Test
    fun overviewPageShowsTopThreeBatteryEntries() {
        val entries = batteryInfoEntries(
            state = BatteryInfoState(
                level = "100%",
                status = "Full",
                health = "Good",
                temperature = "22.5°C",
                technology = "Li-ion",
                cycleCount = "2",
            ),
            page = BatteryInfoPage.Overview,
        )

        assertEquals(
            listOf(
                BatteryInfoEntry("Battery Level", "100%"),
                BatteryInfoEntry("Status", "Full"),
                BatteryInfoEntry("Health", "Good"),
            ),
            entries,
        )
    }

    @Test
    fun detailsPageShowsRemainingBatteryEntries() {
        val entries = batteryInfoEntries(
            state = BatteryInfoState(
                level = "100%",
                status = "Full",
                health = "Good",
                temperature = "22.5°C",
                technology = "Li-ion",
                cycleCount = "2",
            ),
            page = BatteryInfoPage.Details,
        )

        assertEquals(
            listOf(
                BatteryInfoEntry("Temperature", "22.5°C"),
                BatteryInfoEntry("Technology", "Li-ion"),
                BatteryInfoEntry("Charge Cycles", "2"),
            ),
            entries,
        )
    }

    @Test
    fun selectionItemsExposeOverviewAndDetailsCards() {
        val items = batteryInfoSelectionItems(
            BatteryInfoState(
                level = "100%",
                status = "Full",
                health = "Good",
                temperature = "22.5°C",
                technology = "Li-ion",
                cycleCount = "2",
            ),
        )

        assertEquals(
            listOf(
                SubmenuCarouselItem(
                    key = "Overview",
                    title = "Overview",
                    supportingText = "100% | Full",
                ),
                SubmenuCarouselItem(
                    key = "Details",
                    title = "Details",
                    supportingText = "22.5°C | 2 cycles",
                ),
            ),
            items,
        )
    }

    @Test
    fun nextPageStopsAtLastBatteryPage() {
        assertEquals(BatteryInfoPage.Details, nextBatteryInfoPage(BatteryInfoPage.Overview))
        assertEquals(BatteryInfoPage.Details, nextBatteryInfoPage(BatteryInfoPage.Details))
    }

    @Test
    fun previousPageStopsAtFirstBatteryPage() {
        assertEquals(BatteryInfoPage.Overview, previousBatteryInfoPage(BatteryInfoPage.Details))
        assertEquals(BatteryInfoPage.Overview, previousBatteryInfoPage(BatteryInfoPage.Overview))
    }

    @Test
    fun backFromOverviewExitsBatteryScreen() {
        assertEquals(null, backFromBatteryInfoPage(BatteryInfoPage.Overview))
    }

    @Test
    fun backFromDetailsReturnsToBatteryOverview() {
        assertEquals(BatteryInfoPage.Overview, backFromBatteryInfoPage(BatteryInfoPage.Details))
    }

    @Test
    fun batteryInfoVisualStyleUsesWhiteTextOnBlackBackground() {
        val style = batteryInfoVisualStyle()

        assertEquals(Color.White, style.titleColor)
        assertEquals(Color.White.copy(alpha = 0.82f), style.bodyColor)
        assertEquals(Color.White.copy(alpha = 0.72f), style.labelColor)
        assertEquals(Color.White, style.valueColor)
    }
}
