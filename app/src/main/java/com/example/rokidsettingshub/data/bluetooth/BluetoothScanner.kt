package com.example.rokidsettingshub.data.bluetooth

import com.example.rokidsettingshub.model.ManagedDevice

interface BluetoothScanner {
    fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit)
    fun setScanStateListener(listener: (Boolean) -> Unit)
    fun setDeviceStateChangedListener(listener: () -> Unit)
    fun startScan(): Boolean
    fun stopScan()
}
