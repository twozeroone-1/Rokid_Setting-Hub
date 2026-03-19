package com.example.rokidsettingshub.ui.bluetooth

import com.example.rokidsettingshub.model.BluetoothFocusSection
import org.junit.Assert.assertEquals
import org.junit.Test

class BluetoothScreenTest {

    @Test
    fun systemBluetoothSettingsLaunchIsOnlyAvailableOnScanSection() {
        assertEquals(false, canLaunchSystemBluetoothSettings(BluetoothFocusSection.Status))
        assertEquals(false, canLaunchSystemBluetoothSettings(BluetoothFocusSection.MainPhone))
        assertEquals(false, canLaunchSystemBluetoothSettings(BluetoothFocusSection.MyDevices))
        assertEquals(false, canLaunchSystemBluetoothSettings(BluetoothFocusSection.AvailableDevices))
        assertEquals(true, canLaunchSystemBluetoothSettings(BluetoothFocusSection.Scan))
    }

    @Test
    fun subFunctionScanIsOnlyAvailableOnScanSection() {
        assertEquals(false, canUseSubFunctionBluetoothScan(BluetoothFocusSection.Status))
        assertEquals(false, canUseSubFunctionBluetoothScan(BluetoothFocusSection.MainPhone))
        assertEquals(false, canUseSubFunctionBluetoothScan(BluetoothFocusSection.MyDevices))
        assertEquals(false, canUseSubFunctionBluetoothScan(BluetoothFocusSection.AvailableDevices))
        assertEquals(true, canUseSubFunctionBluetoothScan(BluetoothFocusSection.Scan))
    }
}
