package com.example.rokidsettingshub.model

enum class BluetoothFocusSection {
    Status,
    MainPhone,
    MyDevices,
    AvailableDevices,
    Scan,
}

data class BluetoothFocusState(
    val selectedSection: BluetoothFocusSection = BluetoothFocusSection.Status,
    val detailSection: BluetoothFocusSection? = null,
)
