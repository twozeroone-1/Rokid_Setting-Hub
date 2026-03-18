package com.example.rokidsettingshub.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rokidsettingshub.data.bluetooth.BluetoothDeviceActions
import com.example.rokidsettingshub.data.bluetooth.BluetoothRepository
import com.example.rokidsettingshub.data.bluetooth.BluetoothScanner
import com.example.rokidsettingshub.data.bluetooth.BondedDeviceSource
import com.example.rokidsettingshub.model.BluetoothScreenState
import com.example.rokidsettingshub.model.HubSection
import com.example.rokidsettingshub.model.ManagedDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HubViewModel(
    bluetoothRepository: BluetoothRepository = BluetoothRepository(
        bondedDeviceSource = EmptyBondedDeviceSource,
        scanner = NoOpBluetoothScanner,
        deviceActions = NoOpBluetoothDeviceActions,
        loadMainPhone = { null },
    ),
) : ViewModel() {
    private val _currentSection = MutableStateFlow<HubSection?>(null)
    val currentSection: StateFlow<HubSection?> = _currentSection.asStateFlow()
    val bluetoothScreenState: StateFlow<BluetoothScreenState> = bluetoothRepository.state

    fun selectSection(section: HubSection) {
        _currentSection.value = section
    }

    fun returnToHub() {
        _currentSection.value = null
    }
}

private object EmptyBondedDeviceSource : BondedDeviceSource {
    override fun loadBondedDevices(): List<ManagedDevice> = emptyList()
}

private object NoOpBluetoothScanner : BluetoothScanner {
    override fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit) = Unit
}

private object NoOpBluetoothDeviceActions : BluetoothDeviceActions {
    override fun connect(address: String) = Unit

    override fun disconnect(address: String) = Unit

    override fun forget(address: String) = Unit
}
