package com.example.rokidsettingshub.viewmodel

import com.example.rokidsettingshub.data.bluetooth.BluetoothDeviceActions
import com.example.rokidsettingshub.data.bluetooth.BluetoothRepository
import com.example.rokidsettingshub.data.bluetooth.BluetoothScanner
import com.example.rokidsettingshub.data.bluetooth.BondedDeviceSource
import com.example.rokidsettingshub.data.storage.StoredMainPhone
import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.HubSection
import com.example.rokidsettingshub.model.ManagedDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HubViewModelTest {

    @Test
    fun selectingBluetoothUpdatesCurrentSection() {
        val viewModel = HubViewModel()

        assertNull(viewModel.currentSection.value)

        viewModel.selectSection(HubSection.Bluetooth)

        assertEquals(HubSection.Bluetooth, viewModel.currentSection.value)
    }

    @Test
    fun returningToHubClearsCurrentSection() {
        val viewModel = HubViewModel()

        viewModel.selectSection(HubSection.Bluetooth)

        viewModel.returnToHub()

        assertNull(viewModel.currentSection.value)
    }

    @Test
    fun bluetoothRepositoryStateIsExposedForBluetoothScreen() {
        val mainPhone = ManagedDevice(
            address = "AA:BB:CC:DD:EE:41",
            name = "Pixel 10",
            deviceType = DeviceType.Phone,
            connectionState = DeviceConnectionState.Paired,
        )
        val audio = ManagedDevice(
            address = "AA:BB:CC:DD:EE:42",
            name = "Rokid Audio",
            deviceType = DeviceType.Audio,
            connectionState = DeviceConnectionState.Connected,
        )
        val scannedDevice = ManagedDevice(
            address = "AA:BB:CC:DD:EE:43",
            name = "Portable Keyboard",
            deviceType = DeviceType.KeyboardMouse,
            connectionState = DeviceConnectionState.Available,
        )
        val scanner = FakeBluetoothScanner()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(listOf(mainPhone, audio)),
            scanner = scanner,
            deviceActions = FakeBluetoothDeviceActions(),
            loadMainPhone = {
                StoredMainPhone(
                    address = mainPhone.address,
                    name = mainPhone.name,
                )
            },
        )
        val viewModel = HubViewModel(bluetoothRepository = repository)

        scanner.emit(listOf(scannedDevice))

        assertEquals(mainPhone.copy(isMainPhone = true), viewModel.bluetoothScreenState.value.mainPhone)
        assertEquals(
            listOf(
                mainPhone.copy(isMainPhone = true),
                audio.copy(isMainPhone = false),
            ),
            viewModel.bluetoothScreenState.value.myDevices,
        )
        assertEquals(
            listOf(scannedDevice),
            viewModel.bluetoothScreenState.value.availableDevices,
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

    private class FakeBluetoothDeviceActions : BluetoothDeviceActions {
        override fun connect(address: String) = Unit

        override fun disconnect(address: String) = Unit

        override fun forget(address: String) = Unit
    }
}
