package com.example.rokidsettingshub.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ManagedDevicePolicyTest {

    @Test
    fun mainPhoneCannotBeForgotten() {
        val mainPhone = ManagedDevice(
            address = "AA:BB:CC:DD:EE:01",
            name = "Main Phone",
            deviceType = DeviceType.Phone,
            connectionState = DeviceConnectionState.Paired,
            isMainPhone = true,
        )

        assertFalse(mainPhone.canForget)
    }

    @Test
    fun assigningNewMainPhoneReplacesOldOne() {
        val oldMainPhone = ManagedDevice(
            address = "AA:BB:CC:DD:EE:01",
            name = "Old Main",
            deviceType = DeviceType.Phone,
            connectionState = DeviceConnectionState.Connected,
            isMainPhone = true,
        )
        val candidate = ManagedDevice(
            address = "AA:BB:CC:DD:EE:02",
            name = "New Main",
            deviceType = DeviceType.Phone,
            connectionState = DeviceConnectionState.Paired,
        )

        val updatedDevices = ManagedDevice.assignMainPhone(
            devices = listOf(oldMainPhone, candidate),
            address = candidate.address,
        )

        assertFalse(updatedDevices.first { it.address == oldMainPhone.address }.isMainPhone)
        assertTrue(updatedDevices.first { it.address == candidate.address }.isMainPhone)
    }

    @Test
    fun connectionCapabilitiesVaryByState() {
        val availableDevice = ManagedDevice(
            address = "AA:BB:CC:DD:EE:03",
            name = "Available Device",
            deviceType = DeviceType.Audio,
            connectionState = DeviceConnectionState.Available,
        )
        val pairedDevice = ManagedDevice(
            address = "AA:BB:CC:DD:EE:04",
            name = "Paired Device",
            deviceType = DeviceType.Audio,
            connectionState = DeviceConnectionState.Paired,
        )
        val connectedDevice = ManagedDevice(
            address = "AA:BB:CC:DD:EE:05",
            name = "Connected Device",
            deviceType = DeviceType.Audio,
            connectionState = DeviceConnectionState.Connected,
        )

        assertFalse(availableDevice.canConnect)
        assertFalse(availableDevice.canDisconnect)

        assertTrue(pairedDevice.canConnect)
        assertFalse(pairedDevice.canDisconnect)

        assertFalse(connectedDevice.canConnect)
        assertTrue(connectedDevice.canDisconnect)
    }

    @Test
    fun onlyEligiblePhonesCanBecomeMainPhone() {
        val pairedPhone = ManagedDevice(
            address = "AA:BB:CC:DD:EE:06",
            name = "Eligible Phone",
            deviceType = DeviceType.Phone,
            connectionState = DeviceConnectionState.Paired,
        )
        val connectedAudio = ManagedDevice(
            address = "AA:BB:CC:DD:EE:07",
            name = "Connected Audio",
            deviceType = DeviceType.Audio,
            connectionState = DeviceConnectionState.Connected,
        )

        assertTrue(pairedPhone.canBeMainPhone)
        assertFalse(connectedAudio.canBeMainPhone)
    }
}
