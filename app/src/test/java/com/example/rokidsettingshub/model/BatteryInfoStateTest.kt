package com.example.rokidsettingshub.model

import android.os.BatteryManager
import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryInfoStateTest {

    @Test
    fun snapshotFormatsBatteryValues() {
        val state = BatteryInfoState.fromSnapshot(
            BatteryInfoSnapshot(
                levelPercent = 87,
                status = BatteryManager.BATTERY_STATUS_CHARGING,
                health = BatteryManager.BATTERY_HEALTH_GOOD,
                temperatureTenthsC = 225,
                technology = "Li-ion",
                cycleCount = 2,
            ),
        )

        assertEquals("87%", state.level)
        assertEquals("Charging", state.status)
        assertEquals("Good", state.health)
        assertEquals("22.5°C", state.temperature)
        assertEquals("Li-ion", state.technology)
        assertEquals("2", state.cycleCount)
    }

    @Test
    fun snapshotFallsBackToUnavailableWhenValuesAreMissing() {
        val state = BatteryInfoState.fromSnapshot(
            BatteryInfoSnapshot(
                levelPercent = null,
                status = null,
                health = null,
                temperatureTenthsC = null,
                technology = null,
                cycleCount = null,
            ),
        )

        assertEquals("Unavailable", state.level)
        assertEquals("Unavailable", state.status)
        assertEquals("Unavailable", state.health)
        assertEquals("Unavailable", state.temperature)
        assertEquals("Unavailable", state.technology)
        assertEquals("Unavailable", state.cycleCount)
    }
}
