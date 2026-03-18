package com.example.rokidsettingshub.ui.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rokidsettingshub.R
import com.example.rokidsettingshub.model.BluetoothFocusState
import com.example.rokidsettingshub.model.BluetoothScreenState
import com.example.rokidsettingshub.model.HubSection
import com.example.rokidsettingshub.model.BluetoothFocusSection
import com.example.rokidsettingshub.ui.bluetooth.BluetoothScreen
import com.example.rokidsettingshub.ui.common.SectionCard

@Composable
fun HubScreen(
    currentSection: HubSection?,
    bluetoothState: BluetoothScreenState,
    bluetoothFocusState: BluetoothFocusState,
    onMoveBluetoothFocus: (Int, Long) -> Unit,
    onStartBluetoothScan: () -> Unit,
    onStopBluetoothScan: () -> Unit,
    onPairBluetoothDevice: (String) -> Unit,
    onOpenBluetoothDeviceDetails: (String) -> Unit,
    onSelectBluetoothSection: (BluetoothFocusSection) -> Unit,
    onActivateBluetoothSection: () -> Unit,
    onBackFromBluetoothDetail: () -> Unit,
    onSectionSelected: (HubSection) -> Unit,
    onBackFromSection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (currentSection) {
        HubSection.Bluetooth -> BluetoothScreen(
            state = bluetoothState,
            navigationState = bluetoothFocusState,
            onMoveFocus = onMoveBluetoothFocus,
            onStartScan = onStartBluetoothScan,
            onStopScan = onStopBluetoothScan,
            onPairDevice = onPairBluetoothDevice,
            onOpenDeviceDetails = onOpenBluetoothDeviceDetails,
            onSelectSection = onSelectBluetoothSection,
            onActivateSelectedSection = onActivateBluetoothSection,
            onBackFromDetail = onBackFromBluetoothDetail,
            onBack = onBackFromSection,
            modifier = modifier,
        )
        HubSection.WiFi,
        HubSection.Battery,
        HubSection.DeviceInfo -> PlaceholderSectionScreen(
            section = currentSection,
            onBack = onBackFromSection,
            modifier = modifier,
        )
        null -> HubHome(
            onSectionSelected = onSectionSelected,
            modifier = modifier,
        )
    }
}

@Composable
private fun HubHome(
    onSectionSelected: (HubSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_hub_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.settings_hub_subtitle),
            style = MaterialTheme.typography.bodyMedium,
        )

        HubSection.entries.forEach { section ->
            SectionCard(
                title = stringResource(section.titleResId()),
                supportingText = stringResource(sectionCopyFor(section).cardBodyResId),
                enabled = true,
                onClick = { onSectionSelected(section) },
            )
        }
    }
}

private fun HubSection.titleResId(): Int = when (this) {
    HubSection.Bluetooth -> R.string.section_bluetooth_title
    HubSection.WiFi -> R.string.section_wifi_title
    HubSection.Battery -> R.string.section_battery_title
    HubSection.DeviceInfo -> R.string.section_device_info_title
}
