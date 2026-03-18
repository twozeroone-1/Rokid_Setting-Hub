package com.example.rokidsettingshub.data.bluetooth

import com.example.rokidsettingshub.model.ManagedDevice

interface BluetoothScanner {
    fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit)
}
