package com.example.rokidsettingshub.data.bluetooth

import com.example.rokidsettingshub.data.storage.StoredMainPhone
import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.ManagedDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BluetoothRepositoryContractTest {

    @Test
    fun loadsBondedDevicesAndMarksStoredMainPhone() {
        val mainPhone = device(
            address = "AA:BB:CC:DD:EE:21",
            name = "Pixel 10",
            type = DeviceType.Phone,
            state = DeviceConnectionState.Paired,
        )
        val audio = device(
            address = "AA:BB:CC:DD:EE:22",
            name = "Rokid Audio",
            type = DeviceType.Audio,
            state = DeviceConnectionState.Connected,
        )

        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(listOf(mainPhone, audio)),
            scanner = FakeBluetoothScanner(),
            deviceActions = FakeDeviceActions(),
            loadMainPhone = {
                StoredMainPhone(
                    address = mainPhone.address,
                    name = mainPhone.name,
                )
            },
        )

        assertEquals(
            listOf(
                mainPhone.copy(isMainPhone = true),
                audio.copy(isMainPhone = false),
            ),
            repository.state.value.myDevices,
        )
        assertEquals(
            mainPhone.copy(isMainPhone = true),
            repository.state.value.mainPhone,
        )
    }

    @Test
    fun scanResultsUpdateAvailableDevices() {
        val scanner = FakeBluetoothScanner()
        val scannedDevice = device(
            address = "AA:BB:CC:DD:EE:23",
            name = "Portable Keyboard",
            type = DeviceType.KeyboardMouse,
            state = DeviceConnectionState.Available,
        )
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = scanner,
            deviceActions = FakeDeviceActions(),
            loadMainPhone = { null },
        )

        scanner.emit(listOf(scannedDevice))

        assertEquals(listOf(scannedDevice), repository.state.value.availableDevices)
    }

    @Test
    fun connectDispatchesRequest() {
        val deviceActions = FakeDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = { null },
        )

        repository.connect("AA:BB:CC:DD:EE:24")

        assertEquals(listOf("AA:BB:CC:DD:EE:24"), deviceActions.connectedAddresses)
    }

    @Test
    fun disconnectDispatchesRequest() {
        val deviceActions = FakeDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = { null },
        )

        repository.disconnect("AA:BB:CC:DD:EE:25")

        assertEquals(listOf("AA:BB:CC:DD:EE:25"), deviceActions.disconnectedAddresses)
    }

    @Test
    fun forgetIsBlockedForStoredMainPhone() {
        val mainPhoneAddress = "AA:BB:CC:DD:EE:26"
        val deviceActions = FakeDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = {
                StoredMainPhone(
                    address = mainPhoneAddress,
                    name = "Protected Phone",
                )
            },
        )

        val forgotDevice = repository.forget(mainPhoneAddress)

        assertFalse(forgotDevice)
        assertTrue(deviceActions.forgottenAddresses.isEmpty())
    }

    private fun device(
        address: String,
        name: String,
        type: DeviceType,
        state: DeviceConnectionState,
    ): ManagedDevice {
        return ManagedDevice(
            address = address,
            name = name,
            deviceType = type,
            connectionState = state,
        )
    }

    private class FakeBondedDeviceSource(
        private val devices: List<ManagedDevice>,
    ) : BondedDeviceSource {
        override fun loadBondedDevices(): List<ManagedDevice> = devices
    }

    private class FakeBluetoothScanner : BluetoothScanner {
        private var listener: ((List<ManagedDevice>) -> Unit)? = null

        override fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit) {
            this.listener = listener
        }

        fun emit(devices: List<ManagedDevice>) {
            listener?.invoke(devices)
        }
    }

    private class FakeDeviceActions : BluetoothDeviceActions {
        val connectedAddresses = mutableListOf<String>()
        val disconnectedAddresses = mutableListOf<String>()
        val forgottenAddresses = mutableListOf<String>()

        override fun connect(address: String) {
            connectedAddresses += address
        }

        override fun disconnect(address: String) {
            disconnectedAddresses += address
        }

        override fun forget(address: String) {
            forgottenAddresses += address
        }
    }
}
