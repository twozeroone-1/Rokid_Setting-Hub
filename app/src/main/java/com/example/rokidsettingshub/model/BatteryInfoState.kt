package com.example.rokidsettingshub.model

import android.os.BatteryManager
import java.util.Locale

data class BatteryInfoSnapshot(
    val levelPercent: Int?,
    val status: Int?,
    val health: Int?,
    val temperatureTenthsC: Int?,
    val technology: String?,
    val cycleCount: Int?,
)

data class BatteryInfoState(
    val level: String = UNAVAILABLE_TEXT,
    val status: String = UNAVAILABLE_TEXT,
    val health: String = UNAVAILABLE_TEXT,
    val temperature: String = UNAVAILABLE_TEXT,
    val technology: String = UNAVAILABLE_TEXT,
    val cycleCount: String = UNAVAILABLE_TEXT,
) {
    companion object {
        const val UNAVAILABLE_TEXT = "Unavailable"

        fun fromSnapshot(snapshot: BatteryInfoSnapshot): BatteryInfoState = BatteryInfoState(
            level = snapshot.levelPercent?.let { "$it%" } ?: UNAVAILABLE_TEXT,
            status = formatBatteryStatus(snapshot.status),
            health = formatBatteryHealth(snapshot.health),
            temperature = snapshot.temperatureTenthsC
                ?.let { String.format(Locale.US, "%.1f°C", it / 10.0) }
                ?: UNAVAILABLE_TEXT,
            technology = snapshot.technology?.takeUnless { it.isBlank() } ?: UNAVAILABLE_TEXT,
            cycleCount = snapshot.cycleCount?.toString() ?: UNAVAILABLE_TEXT,
        )
    }
}

private fun formatBatteryStatus(status: Int?): String = when (status) {
    BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
    BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
    BatteryManager.BATTERY_STATUS_FULL -> "Full"
    BatteryManager.BATTERY_STATUS_UNKNOWN,
    null -> BatteryInfoState.UNAVAILABLE_TEXT
    else -> BatteryInfoState.UNAVAILABLE_TEXT
}

private fun formatBatteryHealth(health: Int?): String = when (health) {
    BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
    BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
    BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over voltage"
    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
    BatteryManager.BATTERY_HEALTH_UNKNOWN,
    null -> BatteryInfoState.UNAVAILABLE_TEXT
    else -> BatteryInfoState.UNAVAILABLE_TEXT
}
