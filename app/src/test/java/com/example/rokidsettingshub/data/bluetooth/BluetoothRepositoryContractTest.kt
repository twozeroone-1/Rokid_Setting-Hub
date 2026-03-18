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
    fun startScanDispatchesToScannerAndMarksScanning() {
        val scanner = FakeBluetoothScanner()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = scanner,
            deviceActions = FakeDeviceActions(),
            loadMainPhone = { null },
        )

        val scanStarted = repository.startScan()

        assertTrue(scanStarted)
        assertTrue(scanner.startRequested)
        assertTrue(repository.state.value.isScanning)
    }

    @Test
    fun scanFinishedMarksRepositoryIdle() {
        val scanner = FakeBluetoothScanner()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = scanner,
            deviceActions = FakeDeviceActions(),
            loadMainPhone = { null },
        )

        repository.startScan()
        scanner.emitScanState(isScanning = false)

        assertFalse(repository.state.value.isScanning)
    }

    @Test
    fun pairDispatchesRequest() {
        val deviceActions = FakeDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = { null },
        )

        val pairingStarted = repository.pair("AA:BB:CC:DD:EE:27")

        assertTrue(pairingStarted)
        assertEquals(listOf("AA:BB:CC:DD:EE:27"), deviceActions.pairedAddresses)
    }

    @Test
    fun openingDeviceDetailsDispatchesRequest() {
        val deviceActions = FakeDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = { null },
        )

        repository.openDeviceDetails("AA:BB:CC:DD:EE:29")

        assertEquals(listOf("AA:BB:CC:DD:EE:29"), deviceActions.detailAddresses)
    }

    @Test
    fun deviceStateChangesReloadBondedDevices() {
        val bondedDeviceSource = FakeBondedDeviceSource(emptyList())
        val scanner = FakeBluetoothScanner()
        val pairedPhone = device(
            address = "AA:BB:CC:DD:EE:28",
            name = "Galaxy Fold",
            type = DeviceType.Phone,
            state = DeviceConnectionState.Paired,
        )
        val repository = BluetoothRepository(
            bondedDeviceSource = bondedDeviceSource,
            scanner = scanner,
            deviceActions = FakeDeviceActions(),
            loadMainPhone = { null },
        )

        bondedDeviceSource.devices = listOf(pairedPhone)
        scanner.emitDeviceStateChanged()

        assertEquals(listOf(pairedPhone), repository.state.value.myDevices)
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
        var devices: List<ManagedDevice>,
    ) : BondedDeviceSource {
        override fun loadBondedDevices(): List<ManagedDevice> = devices
    }

    private class FakeBluetoothScanner : BluetoothScanner {
        private var listener: ((List<ManagedDevice>) -> Unit)? = null
        private var scanStateListener: ((Boolean) -> Unit)? = null
        private var deviceStateChangedListener: (() -> Unit)? = null
        var startRequested = false
        var stopRequested = false

        override fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit) {
            this.listener = listener
        }

        override fun setScanStateListener(listener: (Boolean) -> Unit) {
            scanStateListener = listener
        }

        override fun setDeviceStateChangedListener(listener: () -> Unit) {
            deviceStateChangedListener = listener
        }

        override fun startScan(): Boolean {
            startRequested = true
            return true
        }

        override fun stopScan() {
            stopRequested = true
        }

        fun emitScanState(isScanning: Boolean) {
            scanStateListener?.invoke(isScanning)
        }

        fun emitDeviceStateChanged() {
            deviceStateChangedListener?.invoke()
        }

        fun emit(devices: List<ManagedDevice>) {
            listener?.invoke(devices)
        }
    }

    private class FakeDeviceActions : BluetoothDeviceActions {
        val pairedAddresses = mutableListOf<String>()
        val detailAddresses = mutableListOf<String>()
        val connectedAddresses = mutableListOf<String>()
        val disconnectedAddresses = mutableListOf<String>()
        val forgottenAddresses = mutableListOf<String>()

        override fun pair(address: String): Boolean {
            pairedAddresses += address
            return true
        }

        override fun connect(address: String) {
            connectedAddresses += address
        }

        override fun disconnect(address: String) {
            disconnectedAddresses += address
        }

        override fun openDetails(address: String) {
            detailAddresses += address
        }

        override fun forget(address: String) {
            forgottenAddresses += address
        }
    }
}
