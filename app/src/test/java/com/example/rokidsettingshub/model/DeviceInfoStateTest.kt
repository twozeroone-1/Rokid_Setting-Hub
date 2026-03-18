package com.example.rokidsettingshub.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceInfoStateTest {

    @Test
    fun snapshotFormatsModelVersionAndStorageValues() {
        val state = DeviceInfoState.fromSnapshot(
            DeviceInfoSnapshot(
                modelName = "Rokid Glasses",
                androidVersion = "12",
                totalStorageBytes = 64L * 1024L * 1024L * 1024L,
                freeStorageBytes = 16L * 1024L * 1024L * 1024L,
            ),
        )

        assertEquals("Rokid Glasses", state.modelName)
        assertEquals("Android 12", state.androidVersion)
        assertEquals("64.0 GB", state.totalStorage)
        assertEquals("48.0 GB", state.usedStorage)
        assertEquals("16.0 GB", state.freeStorage)
    }

    @Test
    fun snapshotFallsBackToUnavailableWhenValuesAreMissing() {
        val state = DeviceInfoState.fromSnapshot(
            DeviceInfoSnapshot(
                modelName = null,
                androidVersion = null,
                totalStorageBytes = null,
                freeStorageBytes = null,
            ),
        )

        assertEquals("Unavailable", state.modelName)
        assertEquals("Unavailable", state.androidVersion)
        assertEquals("Unavailable", state.totalStorage)
        assertEquals("Unavailable", state.usedStorage)
        assertEquals("Unavailable", state.freeStorage)
    }
}
