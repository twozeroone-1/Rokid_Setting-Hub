package com.example.rokidsettingshub.data.bluetooth

import com.example.rokidsettingshub.data.storage.StoredMainPhone
import com.example.rokidsettingshub.model.BluetoothScanNotice
import com.example.rokidsettingshub.model.BluetoothScreenState
import com.example.rokidsettingshub.model.ManagedDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface BondedDeviceSource {
    fun loadBondedDevices(): List<ManagedDevice>
}

// Pairing confirmation and PIN entry remain system-owned, so pairing is excluded here.
interface BluetoothDeviceActions {
    fun pair(address: String): Boolean
    fun connect(address: String)
    fun disconnect(address: String)
    fun openDetails(address: String)
    fun forget(address: String)
}

class BluetoothRepository(
    private val bondedDeviceSource: BondedDeviceSource,
    private val scanner: BluetoothScanner,
    private val deviceActions: BluetoothDeviceActions,
    private val loadMainPhone: () -> StoredMainPhone?,
    private val confirmForget: (String) -> Boolean = { false },
) {
    private val _state = MutableStateFlow(
        BluetoothScreenState(
            myDevices = bondedDeviceSource.loadBondedDevices().markMainPhone(loadMainPhone()?.address),
        ),
    )
    val state: StateFlow<BluetoothScreenState> = _state.asStateFlow()

    init {
        refreshBondedDevices()

        scanner.setScanResultsListener { scannedDevices ->
            _state.value = _state.value.copy(
                availableDevices = scannedDevices.filterNot { scannedDevice ->
                    _state.value.myDevices.any { myDevice -> myDevice.address == scannedDevice.address }
                },
            )
        }
        scanner.setScanStateListener { isScanning ->
            _state.value = _state.value.copy(
                isScanning = isScanning,
                scanNotice = if (isScanning) null else _state.value.scanNotice,
            )
        }
        scanner.setDeviceStateChangedListener(::refreshBondedDevices)
    }

    fun startScan(): Boolean {
        val scanStarted = scanner.startScan()
        _state.value = _state.value.copy(
            isScanning = scanStarted,
            availableDevices = emptyList(),
            scanNotice = if (scanStarted) null else BluetoothScanNotice.StartFailed,
        )
        return scanStarted
    }

    fun stopScan() {
        scanner.stopScan()
        _state.value = _state.value.copy(isScanning = false, scanNotice = null)
    }

    fun noteMissingScanPermission() {
        _state.value = _state.value.copy(
            isScanning = false,
            scanNotice = BluetoothScanNotice.PermissionRequired,
        )
    }

    fun pair(address: String): Boolean {
        stopActiveScanIfNeeded()
        return deviceActions.pair(address)
    }

    fun connect(address: String) {
        stopActiveScanIfNeeded()
        deviceActions.connect(address)
    }

    fun disconnect(address: String) {
        stopActiveScanIfNeeded()
        deviceActions.disconnect(address)
    }

    fun openDeviceDetails(address: String) {
        stopActiveScanIfNeeded()
        deviceActions.openDetails(address)
    }

    fun forget(address: String): Boolean {
        if (loadMainPhone()?.address == address) {
            return false
        }

        if (!confirmForget(address)) {
            return false
        }

        stopActiveScanIfNeeded()
        deviceActions.forget(address)
        return true
    }

    private fun refreshBondedDevices() {
        val myDevices = bondedDeviceSource.loadBondedDevices().markMainPhone(loadMainPhone()?.address)
        val myAddresses = myDevices.mapTo(linkedSetOf()) { it.address }
        _state.value = _state.value.copy(
            mainPhone = myDevices.firstOrNull { it.isMainPhone },
            myDevices = myDevices,
            availableDevices = _state.value.availableDevices.filterNot { it.address in myAddresses },
        )
    }

    private fun stopActiveScanIfNeeded() {
        if (_state.value.isScanning) {
            scanner.stopScan()
            _state.value = _state.value.copy(isScanning = false)
        }
    }

    private fun List<ManagedDevice>.markMainPhone(mainPhoneAddress: String?): List<ManagedDevice> {
        return map { device ->
            device.copy(isMainPhone = device.address == mainPhoneAddress)
        }
    }
}
