package com.example.rokidsettingshub.data.bluetooth

import android.Manifest
import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Test

class BluetoothPermissionRequirementsTest {

    @Test
    fun preAndroid12RequiresNoRuntimeBluetoothPermissions() {
        assertEquals(
            emptyList<String>(),
            BluetoothPermissionRequirements.runtimePermissionsFor(Build.VERSION_CODES.R),
        )
    }

    @Test
    fun android12AndAboveRequiresNearbyDevicePermissions() {
        assertEquals(
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            ),
            BluetoothPermissionRequirements.runtimePermissionsFor(Build.VERSION_CODES.S),
        )
    }
}
