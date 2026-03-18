package com.example.rokidsettingshub.ui.hub

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
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
import com.example.rokidsettingshub.model.BluetoothFocusState
import com.example.rokidsettingshub.model.BatteryInfoState
import com.example.rokidsettingshub.model.DeviceInfoState
import com.example.rokidsettingshub.model.BluetoothScreenState
import com.example.rokidsettingshub.model.HubSection
import com.example.rokidsettingshub.model.BluetoothFocusSection
import com.example.rokidsettingshub.model.WifiInfoState
import com.example.rokidsettingshub.ui.bluetooth.BluetoothScreen
import com.example.rokidsettingshub.ui.common.SectionCard

internal fun hubHomePageTarget(selectedSection: HubSection): Int =
    HubSection.entries.indexOf(selectedSection).coerceAtLeast(0)

@Composable
fun HubScreen(
    currentSection: HubSection?,
    selectedHubSection: HubSection,
    bluetoothState: BluetoothScreenState,
    wifiInfoState: WifiInfoState,
    batteryInfoState: BatteryInfoState,
    deviceInfoState: DeviceInfoState,
    bluetoothFocusState: BluetoothFocusState,
    onMoveHubSelection: (Int) -> Unit,
    onActivateHubSection: () -> Unit,
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
    onRegisterHardwareBackHandler: ((() -> Boolean)?) -> Unit,
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
            registerHardwareBackHandler = onRegisterHardwareBackHandler,
            modifier = modifier,
        )
        HubSection.WiFi -> WifiInfoScreen(
            state = wifiInfoState,
            onBack = onBackFromSection,
            registerHardwareBackHandler = onRegisterHardwareBackHandler,
            modifier = modifier,
        )
        HubSection.Battery -> BatteryInfoScreen(
            state = batteryInfoState,
            onBack = onBackFromSection,
            registerHardwareBackHandler = onRegisterHardwareBackHandler,
            modifier = modifier,
        )
        HubSection.DeviceInfo -> DeviceInfoScreen(
            state = deviceInfoState,
            onBack = onBackFromSection,
            registerHardwareBackHandler = onRegisterHardwareBackHandler,
            modifier = modifier,
        )
        null -> HubHome(
            selectedSection = selectedHubSection,
            onMoveSelection = onMoveHubSelection,
            onActivateSelection = onActivateHubSection,
            onSectionSelected = onSectionSelected,
            onRegisterHardwareBackHandler = onRegisterHardwareBackHandler,
            modifier = modifier,
        )
    }
}

@Composable
private fun HubHome(
    selectedSection: HubSection,
    onMoveSelection: (Int) -> Unit,
    onActivateSelection: () -> Unit,
    onSectionSelected: (HubSection) -> Unit,
    onRegisterHardwareBackHandler: ((() -> Boolean)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    SideEffect {
        onRegisterHardwareBackHandler(null)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(selectedSection) {
        listState.animateScrollToItem(hubHomePageTarget(selectedSection))
    }

    Column(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) {
                    return@onPreviewKeyEvent false
                }

                when (event.key) {
                    Key.DirectionRight,
                    Key.DirectionDown -> {
                        onMoveSelection(1)
                        true
                    }
                    Key.DirectionLeft,
                    Key.DirectionUp -> {
                        onMoveSelection(-1)
                        true
                    }
                    Key.Enter,
                    Key.NumPadEnter,
                    Key.DirectionCenter -> {
                        onActivateSelection()
                        true
                    }
                    else -> false
                }
            }
            .fillMaxWidth()
            .fillMaxHeight()
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                userScrollEnabled = false,
            ) {
                itemsIndexed(HubSection.entries, key = { _, section -> section.name }) { index, section ->
                    val horizontalPadding = if (index == 0 || index == HubSection.entries.lastIndex) {
                        40.dp
                    } else {
                        0.dp
                    }

                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = horizontalPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        SectionCard(
                            title = stringResource(section.titleResId()),
                            supportingText = stringResource(sectionCopyFor(section).cardBodyResId),
                            enabled = true,
                            selected = selectedSection == section,
                            onClick = { onSectionSelected(section) },
                        )
                    }
                }
            }
        }
    }
}

private fun HubSection.titleResId(): Int = when (this) {
    HubSection.Bluetooth -> R.string.section_bluetooth_title
    HubSection.WiFi -> R.string.section_wifi_title
    HubSection.Battery -> R.string.section_battery_title
    HubSection.DeviceInfo -> R.string.section_device_info_title
}
