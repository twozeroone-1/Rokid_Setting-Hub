package com.example.rokidsettingshub.model

data class BluetoothScreenState(
    val isBluetoothEnabled: Boolean = true,
    val mainPhone: ManagedDevice? = null,
    val myDevices: List<ManagedDevice> = emptyList(),
    val availableDevices: List<ManagedDevice> = emptyList(),
    val isScanning: Boolean = false,
    val scanNotice: BluetoothScanNotice? = null,
)
