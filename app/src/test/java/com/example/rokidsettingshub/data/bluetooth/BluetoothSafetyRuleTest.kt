package com.example.rokidsettingshub.data.bluetooth

import com.example.rokidsettingshub.data.storage.StoredMainPhone
import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.ManagedDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BluetoothSafetyRuleTest {

    @Test
    fun forgetRequiresExplicitConfirmation() {
        val deviceActions = FakeDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = { null },
            confirmForget = { false },
        )

        val forgotDevice = repository.forget("AA:BB:CC:DD:EE:31")

        assertFalse(forgotDevice)
        assertTrue(deviceActions.forgottenAddresses.isEmpty())
    }

    @Test
    fun confirmedForgetDispatchesForNonMainDevice() {
        val deviceActions = FakeDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = { null },
            confirmForget = { true },
        )

        val forgotDevice = repository.forget("AA:BB:CC:DD:EE:32")

        assertTrue(forgotDevice)
        assertEquals(listOf("AA:BB:CC:DD:EE:32"), deviceActions.forgottenAddresses)
    }

    @Test
    fun mainPhoneCannotBeForgottenEvenWhenConfirmed() {
        val mainPhone = ManagedDevice(
            address = "AA:BB:CC:DD:EE:33",
            name = "Protected Phone",
            deviceType = DeviceType.Phone,
            connectionState = DeviceConnectionState.Paired,
        )
        val deviceActions = FakeDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(listOf(mainPhone)),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = {
                StoredMainPhone(
                    address = mainPhone.address,
                    name = mainPhone.name,
                )
            },
            confirmForget = { true },
        )

        val forgotDevice = repository.forget(mainPhone.address)

        assertFalse(forgotDevice)
        assertTrue(deviceActions.forgottenAddresses.isEmpty())
    }

    private class FakeBondedDeviceSource(
        private val devices: List<ManagedDevice>,
    ) : BondedDeviceSource {
        override fun loadBondedDevices(): List<ManagedDevice> = devices
    }

    private class FakeBluetoothScanner : BluetoothScanner {
        override fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit) = Unit
    }

    private class FakeDeviceActions : BluetoothDeviceActions {
        val forgottenAddresses = mutableListOf<String>()

        override fun connect(address: String) = Unit

        override fun disconnect(address: String) = Unit

        override fun forget(address: String) {
            forgottenAddresses += address
        }
    }
}
