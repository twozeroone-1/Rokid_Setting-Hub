package com.example.rokidsettingshub.ui.bluetooth

import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.ManagedDevice
import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceActionSheetPolicyTest {

    @Test
    fun availableDevicesOnlyShowPair() {
        val device = device(
            address = "AA:BB:CC:DD:EE:51",
            type = DeviceType.Audio,
            state = DeviceConnectionState.Available,
        )

        assertEquals(
            listOf(DeviceAction.Pair),
            visibleActionsFor(device),
        )
    }

    @Test
    fun pairedPhoneShowsConnectSetMainPhoneAndForget() {
        val device = device(
            address = "AA:BB:CC:DD:EE:52",
            type = DeviceType.Phone,
            state = DeviceConnectionState.Paired,
        )

        assertEquals(
            listOf(
                DeviceAction.Connect,
                DeviceAction.SetAsMainPhone,
                DeviceAction.Forget,
            ),
            visibleActionsFor(device),
        )
    }

    @Test
    fun connectedAudioShowsDisconnectAndForget() {
        val device = device(
            address = "AA:BB:CC:DD:EE:53",
            type = DeviceType.Audio,
            state = DeviceConnectionState.Connected,
        )

        assertEquals(
            listOf(
                DeviceAction.Disconnect,
                DeviceAction.Forget,
            ),
            visibleActionsFor(device),
        )
    }

    @Test
    fun mainPhoneHidesForgetAndSetMainPhone() {
        val device = device(
            address = "AA:BB:CC:DD:EE:54",
            type = DeviceType.Phone,
            state = DeviceConnectionState.Connected,
            isMainPhone = true,
        )

        assertEquals(
            listOf(DeviceAction.Disconnect),
            visibleActionsFor(device),
        )
    }

    private fun device(
        address: String,
        type: DeviceType,
        state: DeviceConnectionState,
        isMainPhone: Boolean = false,
    ): ManagedDevice {
        return ManagedDevice(
            address = address,
            name = "Device $address",
            deviceType = type,
            connectionState = state,
            isMainPhone = isMainPhone,
        )
    }
}
