package com.example.rokidsettingshub.ui.bluetooth

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rokidsettingshub.R
import com.example.rokidsettingshub.model.BluetoothFocusSection
import com.example.rokidsettingshub.model.BluetoothFocusState
import com.example.rokidsettingshub.model.BluetoothScreenState
import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.ManagedDevice
import com.example.rokidsettingshub.ui.common.SectionCard

@Composable
fun BluetoothScreen(
    state: BluetoothScreenState,
    navigationState: BluetoothFocusState,
    onMoveFocus: (Int, Long) -> Unit,
    onSelectSection: (BluetoothFocusSection) -> Unit,
    onActivateSelectedSection: () -> Unit,
    onBackFromDetail: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        if (navigationState.detailSection != null) {
            onBackFromDetail()
        } else {
            onBack()
        }
    }

    if (navigationState.detailSection == null) {
        BluetoothOverviewScreen(
            state = state,
            navigationState = navigationState,
            onMoveFocus = onMoveFocus,
            onSelectSection = onSelectSection,
            onActivateSelectedSection = onActivateSelectedSection,
            onBack = onBack,
            modifier = modifier,
        )
    } else {
        BluetoothDetailScreen(
            state = state,
            section = navigationState.detailSection,
            onBack = onBackFromDetail,
            modifier = modifier,
        )
    }
}

@Composable
private fun BluetoothOverviewScreen(
    state: BluetoothScreenState,
    navigationState: BluetoothFocusState,
    onMoveFocus: (Int, Long) -> Unit,
    onSelectSection: (BluetoothFocusSection) -> Unit,
    onActivateSelectedSection: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(navigationState.detailSection) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) {
                    return@onPreviewKeyEvent false
                }

                when (event.key) {
                    Key.DirectionDown -> {
                        onMoveFocus(1, SystemClock.uptimeMillis())
                        true
                    }
                    Key.DirectionUp -> {
                        onMoveFocus(-1, SystemClock.uptimeMillis())
                        true
                    }
                    Key.Enter,
                    Key.NumPadEnter,
                    Key.DirectionCenter -> {
                        onActivateSelectedSection()
                        true
                    }
                    else -> false
                }
            }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.section_bluetooth_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.bluetooth_focus_hint),
            style = MaterialTheme.typography.bodyMedium,
        )

        BluetoothFocusSection.entries.forEach { section ->
            SectionCard(
                title = section.title(),
                supportingText = section.summary(state),
                enabled = true,
                selected = navigationState.selectedSection == section,
                onClick = {
                    if (navigationState.selectedSection == section) {
                        onActivateSelectedSection()
                    } else {
                        onSelectSection(section)
                    }
                },
            )
        }

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
private fun BluetoothDetailScreen(
    state: BluetoothScreenState,
    section: BluetoothFocusSection,
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
            text = section.title(),
            style = MaterialTheme.typography.headlineSmall,
        )

        when (section) {
            BluetoothFocusSection.Status -> {
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
            }
            BluetoothFocusSection.MainPhone -> {
                SectionCard(
                    title = stringResource(R.string.bluetooth_main_phone_title),
                    supportingText = state.mainPhone?.name ?: stringResource(R.string.bluetooth_main_phone_empty),
                    enabled = false,
                    onClick = {},
                )
            }
            BluetoothFocusSection.MyDevices -> {
                BluetoothDeviceSection(
                    title = stringResource(R.string.bluetooth_my_devices_title),
                    devices = state.myDevices,
                    emptyText = stringResource(R.string.bluetooth_my_devices_empty),
                )
            }
            BluetoothFocusSection.AvailableDevices -> {
                BluetoothDeviceSection(
                    title = stringResource(R.string.bluetooth_available_devices_title),
                    devices = state.availableDevices,
                    emptyText = stringResource(R.string.bluetooth_available_devices_empty),
                )
            }
            BluetoothFocusSection.Scan -> {
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
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.bluetooth_back_to_sections))
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
private fun BluetoothFocusSection.title(): String = when (this) {
    BluetoothFocusSection.Status -> stringResource(R.string.bluetooth_status_title)
    BluetoothFocusSection.MainPhone -> stringResource(R.string.bluetooth_main_phone_title)
    BluetoothFocusSection.MyDevices -> stringResource(R.string.bluetooth_my_devices_title)
    BluetoothFocusSection.AvailableDevices -> stringResource(R.string.bluetooth_available_devices_title)
    BluetoothFocusSection.Scan -> stringResource(R.string.bluetooth_scan_action_title)
}

@Composable
private fun BluetoothFocusSection.summary(state: BluetoothScreenState): String = when (this) {
    BluetoothFocusSection.Status -> stringResource(
        if (state.isBluetoothEnabled) {
            R.string.bluetooth_status_on
        } else {
            R.string.bluetooth_status_off
        },
    )
    BluetoothFocusSection.MainPhone -> state.mainPhone?.name ?: stringResource(R.string.bluetooth_main_phone_empty)
    BluetoothFocusSection.MyDevices -> stringResource(
        R.string.bluetooth_my_devices_summary,
        state.myDevices.size,
    )
    BluetoothFocusSection.AvailableDevices -> stringResource(
        R.string.bluetooth_available_devices_summary,
        state.availableDevices.size,
    )
    BluetoothFocusSection.Scan -> stringResource(
        if (state.isScanning) {
            R.string.bluetooth_scan_action_scanning
        } else {
            R.string.bluetooth_scan_action_idle
        },
    )
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
