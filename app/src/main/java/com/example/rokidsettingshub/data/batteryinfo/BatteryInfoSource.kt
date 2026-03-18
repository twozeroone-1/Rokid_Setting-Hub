package com.example.rokidsettingshub.data.batteryinfo

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.rokidsettingshub.model.BatteryInfoSnapshot
import com.example.rokidsettingshub.model.BatteryInfoState
import java.io.File

interface BatteryInfoSource {
    fun load(): BatteryInfoState
}

class AndroidBatteryInfoSource(
    private val context: Context,
    private val cycleCountFiles: List<File> = listOf(
        File("/sys/class/power_supply/battery/cycle_count"),
    ),
) : BatteryInfoSource {
    override fun load(): BatteryInfoState {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        return BatteryInfoState.fromSnapshot(
            BatteryInfoSnapshot(
                levelPercent = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    ?.takeIf { it >= 0 },
                status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    ?.takeIf { it >= 0 },
                health = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
                    ?.takeIf { it >= 0 },
                temperatureTenthsC = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
                    ?.takeIf { it >= 0 },
                technology = batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
                cycleCount = readCycleCount(),
            ),
        )
    }

    private fun readCycleCount(): Int? = cycleCountFiles.firstNotNullOfOrNull { file ->
        runCatching {
            file.takeIf { it.exists() }?.readText()?.trim()?.toIntOrNull()
        }.getOrNull()
    }
}

object UnavailableBatteryInfoSource : BatteryInfoSource {
    override fun load(): BatteryInfoState = BatteryInfoState()
}
