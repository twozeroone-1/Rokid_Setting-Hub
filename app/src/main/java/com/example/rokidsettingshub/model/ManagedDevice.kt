package com.example.rokidsettingshub.model

data class ManagedDevice(
    val address: String,
    val name: String,
    val deviceType: DeviceType,
    val connectionState: DeviceConnectionState,
    val isMainPhone: Boolean = false,
) {
    val canForget: Boolean
        get() = !isMainPhone

    val canConnect: Boolean
        get() = connectionState == DeviceConnectionState.Paired

    val canDisconnect: Boolean
        get() = connectionState == DeviceConnectionState.Connected

    val canBeMainPhone: Boolean
        get() = deviceType == DeviceType.Phone && connectionState != DeviceConnectionState.Available

    companion object {
        fun assignMainPhone(devices: List<ManagedDevice>, address: String): List<ManagedDevice> {
            val candidate = devices.firstOrNull { it.address == address } ?: return devices
            require(candidate.canBeMainPhone) { "Only paired or connected phones can become the main phone." }

            return devices.map { device ->
                device.copy(isMainPhone = device.address == address)
            }
        }
    }
}

enum class DeviceConnectionState {
    Connected,
    Paired,
    Available,
}
