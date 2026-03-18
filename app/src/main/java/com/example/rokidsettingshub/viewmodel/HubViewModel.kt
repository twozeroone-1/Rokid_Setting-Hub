package com.example.rokidsettingshub.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rokidsettingshub.data.bluetooth.BluetoothDeviceActions
import com.example.rokidsettingshub.data.bluetooth.BluetoothRepository
import com.example.rokidsettingshub.data.bluetooth.BluetoothScanner
import com.example.rokidsettingshub.data.bluetooth.BondedDeviceSource
import com.example.rokidsettingshub.model.BluetoothFocusSection
import com.example.rokidsettingshub.model.BluetoothFocusState
import com.example.rokidsettingshub.model.BluetoothScreenState
import com.example.rokidsettingshub.model.HubSection
import com.example.rokidsettingshub.model.ManagedDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HubViewModel(
    private val bluetoothRepository: BluetoothRepository = BluetoothRepository(
        bondedDeviceSource = EmptyBondedDeviceSource,
        scanner = NoOpBluetoothScanner,
        deviceActions = NoOpBluetoothDeviceActions,
        loadMainPhone = { null },
    ),
) : ViewModel() {
    private val _currentSection = MutableStateFlow<HubSection?>(null)
    val currentSection: StateFlow<HubSection?> = _currentSection.asStateFlow()
    private val _bluetoothFocusState = MutableStateFlow(BluetoothFocusState())
    val bluetoothFocusState: StateFlow<BluetoothFocusState> = _bluetoothFocusState.asStateFlow()
    val bluetoothScreenState: StateFlow<BluetoothScreenState> = bluetoothRepository.state
    private var lastBluetoothMoveUptimeMs = Long.MIN_VALUE
    private var lastBluetoothMoveDirection = 0

    fun selectSection(section: HubSection) {
        if (section == HubSection.Bluetooth) {
            _bluetoothFocusState.value = BluetoothFocusState()
            lastBluetoothMoveUptimeMs = Long.MIN_VALUE
            lastBluetoothMoveDirection = 0
        }
        _currentSection.value = section
    }

    fun returnToHub() {
        _currentSection.value = null
    }

    fun moveBluetoothFocus(direction: Int, eventUptimeMs: Long) {
        if (_bluetoothFocusState.value.detailSection != null) {
            return
        }
        if (
            lastBluetoothMoveUptimeMs != Long.MIN_VALUE &&
            direction == lastBluetoothMoveDirection &&
            eventUptimeMs - lastBluetoothMoveUptimeMs < BLUETOOTH_MOVE_DEBOUNCE_MS
        ) {
            return
        }

        val sections = BluetoothFocusSection.entries
        val currentIndex = sections.indexOf(_bluetoothFocusState.value.selectedSection)
        val nextIndex = (currentIndex + direction).coerceIn(0, sections.lastIndex)
        if (nextIndex == currentIndex) {
            return
        }

        _bluetoothFocusState.value = _bluetoothFocusState.value.copy(
            selectedSection = sections[nextIndex],
        )
        lastBluetoothMoveUptimeMs = eventUptimeMs
        lastBluetoothMoveDirection = direction
    }

    fun selectBluetoothSection(section: BluetoothFocusSection) {
        _bluetoothFocusState.value = _bluetoothFocusState.value.copy(selectedSection = section)
    }

    fun activateSelectedBluetoothSection() {
        _bluetoothFocusState.value = _bluetoothFocusState.value.copy(
            detailSection = _bluetoothFocusState.value.selectedSection,
        )
    }

    fun handleBluetoothBack(): Boolean {
        if (_bluetoothFocusState.value.detailSection == null) {
            return false
        }

        _bluetoothFocusState.value = _bluetoothFocusState.value.copy(detailSection = null)
        return true
    }

    fun startBluetoothScan() {
        bluetoothRepository.startScan()
    }

    fun stopBluetoothScan() {
        bluetoothRepository.stopScan()
    }

    fun pairBluetoothDevice(address: String) {
        bluetoothRepository.pair(address)
    }

    fun openBluetoothDeviceDetails(address: String) {
        bluetoothRepository.openDeviceDetails(address)
    }
}

private const val BLUETOOTH_MOVE_DEBOUNCE_MS = 180L

private object EmptyBondedDeviceSource : BondedDeviceSource {
    override fun loadBondedDevices(): List<ManagedDevice> = emptyList()
}

private object NoOpBluetoothScanner : BluetoothScanner {
    override fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit) = Unit

    override fun setScanStateListener(listener: (Boolean) -> Unit) = Unit

    override fun setDeviceStateChangedListener(listener: () -> Unit) = Unit

    override fun startScan(): Boolean = false

    override fun stopScan() = Unit
}

private object NoOpBluetoothDeviceActions : BluetoothDeviceActions {
    override fun pair(address: String): Boolean = false

    override fun connect(address: String) = Unit

    override fun disconnect(address: String) = Unit

    override fun openDetails(address: String) = Unit

    override fun forget(address: String) = Unit
}
