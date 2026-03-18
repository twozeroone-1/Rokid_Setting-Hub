package com.example.rokidsettingshub.data.bluetooth

import com.example.rokidsettingshub.data.storage.StoredMainPhone
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
    fun connect(address: String)
    fun disconnect(address: String)
    fun forget(address: String)
}

class BluetoothRepository(
    bondedDeviceSource: BondedDeviceSource,
    scanner: BluetoothScanner,
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
        val myDevices = _state.value.myDevices
        _state.value = _state.value.copy(
            mainPhone = myDevices.firstOrNull { it.isMainPhone },
        )

        scanner.setScanResultsListener { scannedDevices ->
            _state.value = _state.value.copy(availableDevices = scannedDevices)
        }
    }

    fun connect(address: String) {
        deviceActions.connect(address)
    }

    fun disconnect(address: String) {
        deviceActions.disconnect(address)
    }

    fun forget(address: String): Boolean {
        if (loadMainPhone()?.address == address) {
            return false
        }

        if (!confirmForget(address)) {
            return false
        }

        deviceActions.forget(address)
        return true
    }

    private fun List<ManagedDevice>.markMainPhone(mainPhoneAddress: String?): List<ManagedDevice> {
        return map { device ->
            device.copy(isMainPhone = device.address == mainPhoneAddress)
        }
    }
}
