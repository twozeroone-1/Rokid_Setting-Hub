package com.example.rokidsettingshub.ui.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rokidsettingshub.R
import com.example.rokidsettingshub.model.BluetoothScreenState
import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.ManagedDevice
import com.example.rokidsettingshub.ui.common.SectionCard

@Composable
fun BluetoothScreen(
    state: BluetoothScreenState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.section_bluetooth_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        SectionCard(
            title = stringResource(R.string.bluetooth_status_title),
            supportingText = stringResource(
                if (state.isBluetoothEnabled) {
                    R.string.bluetooth_status_on
                } else {
                    R.string.bluetooth_status_off
                },
            ),
            enabled = false,
            onClick = {},
        )

        SectionCard(
            title = stringResource(R.string.bluetooth_main_phone_title),
            supportingText = state.mainPhone?.name ?: stringResource(R.string.bluetooth_main_phone_empty),
            enabled = false,
            onClick = {},
        )

        BluetoothDeviceSection(
            title = stringResource(R.string.bluetooth_my_devices_title),
            devices = state.myDevices,
            emptyText = stringResource(R.string.bluetooth_my_devices_empty),
        )

        BluetoothDeviceSection(
            title = stringResource(R.string.bluetooth_available_devices_title),
            devices = state.availableDevices,
            emptyText = stringResource(R.string.bluetooth_available_devices_empty),
        )

        Text(
            text = stringResource(R.string.bluetooth_scan_action_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Button(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.bluetooth_scan_button))
        }
        Text(
            text = stringResource(
                if (state.isScanning) {
                    R.string.bluetooth_scan_action_scanning
                } else {
                    R.string.bluetooth_scan_action_idle
                },
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.back))
        }
    }
}

@Composable
private fun BluetoothDeviceSection(
    title: String,
    devices: List<ManagedDevice>,
    emptyText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )

        if (devices.isEmpty()) {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            devices.forEach { device ->
                SectionCard(
                    title = device.name,
                    supportingText = device.supportingText(),
                    enabled = false,
                    onClick = {},
                )
            }
        }
    }
}

@Composable
private fun ManagedDevice.supportingText(): String {
    val typeLabel = when (deviceType) {
        DeviceType.Phone -> stringResource(R.string.bluetooth_device_type_phone)
        DeviceType.Audio -> stringResource(R.string.bluetooth_device_type_audio)
        DeviceType.KeyboardMouse -> stringResource(R.string.bluetooth_device_type_keyboard_mouse)
        DeviceType.RemoteHid -> stringResource(R.string.bluetooth_device_type_remote_hid)
        DeviceType.Unknown -> stringResource(R.string.bluetooth_device_type_unknown)
    }
    val connectionLabel = when (connectionState) {
        DeviceConnectionState.Connected -> stringResource(R.string.bluetooth_connection_connected)
        DeviceConnectionState.Paired -> stringResource(R.string.bluetooth_connection_paired)
        DeviceConnectionState.Available -> stringResource(R.string.bluetooth_connection_available)
    }

    return buildString {
        append(typeLabel)
        append(" | ")
        append(connectionLabel)
        if (isMainPhone) {
            append(" | ")
            append(stringResource(R.string.bluetooth_label_main_phone))
        }
    }
}
