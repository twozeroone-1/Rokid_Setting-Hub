package com.example.rokidsettingshub.ui.bluetooth

import com.example.rokidsettingshub.model.BluetoothFocusSection
import org.junit.Assert.assertEquals
import org.junit.Test

class BluetoothOverviewPagerTargetTest {

    @Test
    fun eachBluetoothSectionMapsToItsOwnHorizontalPage() {
        assertEquals(0, bluetoothOverviewPageTarget(BluetoothFocusSection.Status))
        assertEquals(1, bluetoothOverviewPageTarget(BluetoothFocusSection.MainPhone))
        assertEquals(2, bluetoothOverviewPageTarget(BluetoothFocusSection.MyDevices))
        assertEquals(3, bluetoothOverviewPageTarget(BluetoothFocusSection.AvailableDevices))
        assertEquals(4, bluetoothOverviewPageTarget(BluetoothFocusSection.Scan))
    }
}
