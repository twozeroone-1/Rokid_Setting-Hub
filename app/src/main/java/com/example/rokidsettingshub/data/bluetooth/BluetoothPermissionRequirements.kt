package com.example.rokidsettingshub.data.bluetooth

import android.Manifest
import android.os.Build

object BluetoothPermissionRequirements {
    fun runtimePermissionsFor(sdkInt: Int): List<String> {
        return if (sdkInt >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        } else {
            emptyList()
        }
    }
}
