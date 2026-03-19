package com.example.rokidsettingshub.viewmodel

import com.example.rokidsettingshub.data.bluetooth.BluetoothDeviceActions
import com.example.rokidsettingshub.data.bluetooth.BluetoothRepository
import com.example.rokidsettingshub.data.bluetooth.BluetoothScanner
import com.example.rokidsettingshub.data.bluetooth.BondedDeviceSource
import com.example.rokidsettingshub.data.batteryinfo.BatteryInfoSource
import com.example.rokidsettingshub.data.deviceinfo.DeviceInfoSource
import com.example.rokidsettingshub.data.wifiinfo.WifiInfoSource
import com.example.rokidsettingshub.model.BatteryInfoSnapshot
import com.example.rokidsettingshub.model.BatteryInfoState
import com.example.rokidsettingshub.model.BluetoothFocusSection
import com.example.rokidsettingshub.model.BluetoothScanNotice
import com.example.rokidsettingshub.data.storage.StoredMainPhone
import com.example.rokidsettingshub.model.DeviceInfoSnapshot
import com.example.rokidsettingshub.model.DeviceInfoState
import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.HubSection
import com.example.rokidsettingshub.model.ManagedDevice
import com.example.rokidsettingshub.model.WifiInfoSnapshot
import com.example.rokidsettingshub.model.WifiInfoState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun hubSelectionStartsAtBluetooth() {
        val viewModel = HubViewModel()

        assertEquals(HubSection.Bluetooth, viewModel.selectedHubSection.value)
    }

    @Test
    fun movingHubSelectionChangesSelectedSection() {
        val viewModel = HubViewModel()

        viewModel.moveHubSelection(direction = 1)
        viewModel.moveHubSelection(direction = 1)

        assertEquals(HubSection.Battery, viewModel.selectedHubSection.value)
    }

    @Test
    fun movingHubSelectionStopsAtFirstSection() {
        val viewModel = HubViewModel()

        viewModel.moveHubSelection(direction = -1)

        assertEquals(HubSection.Bluetooth, viewModel.selectedHubSection.value)
    }

    @Test
    fun movingHubSelectionStopsAtLastSection() {
        val viewModel = HubViewModel()

        repeat(6) {
            viewModel.moveHubSelection(direction = 1)
        }

        assertEquals(HubSection.DeviceInfo, viewModel.selectedHubSection.value)
    }

    @Test
    fun activatingSelectedHubSectionOpensIt() {
        val viewModel = HubViewModel()

        viewModel.moveHubSelection(direction = 1)
        viewModel.activateSelectedHubSection()

        assertEquals(HubSection.WiFi, viewModel.currentSection.value)
    }

    @Test
    fun deviceInfoStateIsLoadedFromSource() {
        val expected = DeviceInfoState.fromSnapshot(
            DeviceInfoSnapshot(
                modelName = "Rokid Glasses",
                androidVersion = "12",
                totalStorageBytes = 32L * 1024L * 1024L * 1024L,
                freeStorageBytes = 12L * 1024L * 1024L * 1024L,
            ),
        )
        val viewModel = HubViewModel(
            deviceInfoSource = FakeDeviceInfoSource(expected),
        )

        assertEquals(expected, viewModel.deviceInfoState.value)
    }

    @Test
    fun batteryInfoStateIsLoadedFromSource() {
        val expected = BatteryInfoState.fromSnapshot(
            BatteryInfoSnapshot(
                levelPercent = 100,
                status = 5,
                health = 2,
                temperatureTenthsC = 225,
                technology = "Li-ion",
                cycleCount = 2,
            ),
        )
        val viewModel = HubViewModel(
            batteryInfoSource = FakeBatteryInfoSource(expected),
        )

        assertEquals(expected, viewModel.batteryInfoState.value)
    }

    @Test
    fun wifiInfoStateIsLoadedFromSource() {
        val expected = WifiInfoState.fromSnapshot(
            WifiInfoSnapshot(
                hardwarePresent = true,
                enabled = false,
                connected = false,
                ssid = null,
                ipAddress = null,
                interfaceName = "wlan0",
            ),
        )
        val viewModel = HubViewModel(
            wifiInfoSource = FakeWifiInfoSource(expected),
        )

        assertEquals(expected, viewModel.wifiInfoState.value)
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

    @Test
    fun bluetoothFocusStartsAtStatusSection() {
        val viewModel = HubViewModel()

        assertEquals(BluetoothFocusSection.Status, viewModel.bluetoothFocusState.value.selectedSection)
        assertNull(viewModel.bluetoothFocusState.value.detailSection)
    }

    @Test
    fun rapidBluetoothNavigationOnlyMovesOneSection() {
        val viewModel = HubViewModel()

        viewModel.moveBluetoothFocus(direction = 1, eventUptimeMs = 1_000L)
        viewModel.moveBluetoothFocus(direction = 1, eventUptimeMs = 1_080L)

        assertEquals(BluetoothFocusSection.MainPhone, viewModel.bluetoothFocusState.value.selectedSection)
    }

    @Test
    fun oppositeDirectionMovementIsNotDebounced() {
        val viewModel = HubViewModel()

        viewModel.moveBluetoothFocus(direction = 1, eventUptimeMs = 1_000L)
        viewModel.moveBluetoothFocus(direction = -1, eventUptimeMs = 1_080L)

        assertEquals(BluetoothFocusSection.Status, viewModel.bluetoothFocusState.value.selectedSection)
    }

    @Test
    fun activatingSelectedBluetoothSectionOpensDetail() {
        val viewModel = HubViewModel()

        viewModel.moveBluetoothFocus(direction = 1, eventUptimeMs = 1_000L)
        viewModel.moveBluetoothFocus(direction = 1, eventUptimeMs = 1_250L)
        viewModel.activateSelectedBluetoothSection()

        assertEquals(BluetoothFocusSection.MyDevices, viewModel.bluetoothFocusState.value.detailSection)
    }

    @Test
    fun bluetoothBackClosesDetailBeforeLeavingHub() {
        val viewModel = HubViewModel()

        viewModel.activateSelectedBluetoothSection()

        assertTrue(viewModel.handleBluetoothBack())
        assertNull(viewModel.bluetoothFocusState.value.detailSection)
        assertFalse(viewModel.handleBluetoothBack())
    }

    @Test
    fun startingBluetoothScanUpdatesBluetoothState() {
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = FakeBluetoothDeviceActions(),
            loadMainPhone = { null },
        )
        val viewModel = HubViewModel(bluetoothRepository = repository)

        viewModel.startBluetoothScan()

        assertTrue(viewModel.bluetoothScreenState.value.isScanning)
    }

    @Test
    fun stoppingBluetoothScanClearsBluetoothState() {
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = FakeBluetoothDeviceActions(),
            loadMainPhone = { null },
        )
        val viewModel = HubViewModel(bluetoothRepository = repository)

        viewModel.startBluetoothScan()
        viewModel.stopBluetoothScan()

        assertFalse(viewModel.bluetoothScreenState.value.isScanning)
    }

    @Test
    fun notingMissingBluetoothPermissionUpdatesBluetoothState() {
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = FakeBluetoothDeviceActions(),
            loadMainPhone = { null },
        )
        val viewModel = HubViewModel(bluetoothRepository = repository)

        viewModel.noteMissingBluetoothPermission()

        assertEquals(BluetoothScanNotice.PermissionRequired, viewModel.bluetoothScreenState.value.scanNotice)
    }

    @Test
    fun pairingBluetoothDeviceDispatchesRequest() {
        val deviceActions = FakeBluetoothDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = { null },
        )
        val viewModel = HubViewModel(bluetoothRepository = repository)

        viewModel.pairBluetoothDevice("AA:BB:CC:DD:EE:51")

        assertEquals(listOf("AA:BB:CC:DD:EE:51"), deviceActions.pairedAddresses)
    }

    @Test
    fun openingBluetoothDeviceDetailsDispatchesRequest() {
        val deviceActions = FakeBluetoothDeviceActions()
        val repository = BluetoothRepository(
            bondedDeviceSource = FakeBondedDeviceSource(emptyList()),
            scanner = FakeBluetoothScanner(),
            deviceActions = deviceActions,
            loadMainPhone = { null },
        )
        val viewModel = HubViewModel(bluetoothRepository = repository)

        viewModel.openBluetoothDeviceDetails("AA:BB:CC:DD:EE:52")

        assertEquals(listOf("AA:BB:CC:DD:EE:52"), deviceActions.detailAddresses)
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

        override fun setScanStateListener(listener: (Boolean) -> Unit) = Unit

        override fun setDeviceStateChangedListener(listener: () -> Unit) = Unit

        override fun startScan(): Boolean = true

        override fun stopScan() = Unit

        fun emit(devices: List<ManagedDevice>) {
            listener?.invoke(devices)
        }
    }

    private class FakeBluetoothDeviceActions : BluetoothDeviceActions {
        val pairedAddresses = mutableListOf<String>()
        val detailAddresses = mutableListOf<String>()

        override fun pair(address: String): Boolean {
            pairedAddresses += address
            return true
        }

        override fun connect(address: String) = Unit

        override fun disconnect(address: String) = Unit

        override fun openDetails(address: String) {
            detailAddresses += address
        }

        override fun forget(address: String) = Unit
    }

    private class FakeDeviceInfoSource(
        private val state: DeviceInfoState,
    ) : DeviceInfoSource {
        override fun load(): DeviceInfoState = state
    }

    private class FakeBatteryInfoSource(
        private val state: BatteryInfoState,
    ) : BatteryInfoSource {
        override fun load(): BatteryInfoState = state
    }

    private class FakeWifiInfoSource(
        private val state: WifiInfoState,
    ) : WifiInfoSource {
        override fun load(): WifiInfoState = state
    }
}
