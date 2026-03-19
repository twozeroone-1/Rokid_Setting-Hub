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
import androidx.compose.runtime.DisposableEffect
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
import com.example.rokidsettingshub.ui.common.SubmenuCarousel
import com.example.rokidsettingshub.ui.common.SubmenuCarouselItem
import com.example.rokidsettingshub.ui.common.submenuCarouselPageTarget

@Composable
fun BluetoothScreen(
    state: BluetoothScreenState,
    navigationState: BluetoothFocusState,
    onMoveFocus: (Int, Long) -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onPairDevice: (String) -> Unit,
    onOpenDeviceDetails: (String) -> Unit,
    onSelectSection: (BluetoothFocusSection) -> Unit,
    onActivateSelectedSection: () -> Unit,
    onBackFromDetail: () -> Unit,
    onBack: () -> Unit,
    registerHardwareBackHandler: ((() -> Boolean)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        if (navigationState.detailSection != null) {
            onBackFromDetail()
        } else {
            onBack()
        }
    }

    DisposableEffect(
        navigationState.detailSection,
        onBack,
        onBackFromDetail,
        registerHardwareBackHandler,
    ) {
        registerHardwareBackHandler {
            if (navigationState.detailSection != null) {
                onBackFromDetail()
            } else {
                onBack()
            }
            true
        }
        onDispose {
            registerHardwareBackHandler(null)
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
            onStartScan = onStartScan,
            onStopScan = onStopScan,
            onPairDevice = onPairDevice,
            onOpenDeviceDetails = onOpenDeviceDetails,
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
    val items = BluetoothFocusSection.entries.map { section ->
        SubmenuCarouselItem(
            key = section.name,
            title = section.title(),
            supportingText = section.summary(state),
        )
    }

    SubmenuCarousel(
        title = stringResource(R.string.section_bluetooth_title),
        subtitle = stringResource(R.string.submenu_focus_hint),
        items = items,
        selectedIndex = bluetoothOverviewPageTarget(navigationState.selectedSection),
        onMoveSelection = { direction ->
            onMoveFocus(direction, SystemClock.uptimeMillis())
        },
        onActivateSelection = onActivateSelectedSection,
        onSelectItem = { index ->
            onSelectSection(BluetoothFocusSection.entries[index])
        },
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun BluetoothDetailScreen(
    state: BluetoothScreenState,
    section: BluetoothFocusSection,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onPairDevice: (String) -> Unit,
    onOpenDeviceDetails: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(section) {
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
                    Key.DirectionLeft,
                    Key.Back -> {
                        onBack()
                        true
                    }

                    else -> false
                }
            }
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
                    enabled = state.mainPhone != null,
                    onClick = {
                        state.mainPhone?.let { onOpenDeviceDetails(it.address) }
                    },
                )
            }
            BluetoothFocusSection.MyDevices -> {
                BluetoothDeviceSection(
                    title = stringResource(R.string.bluetooth_my_devices_title),
                    devices = state.myDevices,
                    emptyText = stringResource(R.string.bluetooth_my_devices_empty),
                    onDeviceSelected = { device -> onOpenDeviceDetails(device.address) },
                )
            }
            BluetoothFocusSection.AvailableDevices -> {
                BluetoothDeviceSection(
                    title = stringResource(R.string.bluetooth_available_devices_title),
                    devices = state.availableDevices,
                    emptyText = stringResource(R.string.bluetooth_available_devices_empty),
                    onDeviceSelected = { device -> onPairDevice(device.address) },
                )
            }
            BluetoothFocusSection.Scan -> {
                Button(
                    onClick = {
                        if (state.isScanning) {
                            onStopScan()
                        } else {
                            onStartScan()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        stringResource(
                            if (state.isScanning) {
                                R.string.bluetooth_stop_scan_button
                            } else {
                                R.string.bluetooth_scan_button
                            },
                        ),
                    )
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
    onDeviceSelected: (ManagedDevice) -> Unit,
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
                    enabled = true,
                    onClick = { onDeviceSelected(device) },
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

internal fun bluetoothOverviewPageTarget(selectedSection: BluetoothFocusSection): Int =
    submenuCarouselPageTarget(
        selectedIndex = BluetoothFocusSection.entries.indexOf(selectedSection),
        itemCount = BluetoothFocusSection.entries.size,
    )

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
