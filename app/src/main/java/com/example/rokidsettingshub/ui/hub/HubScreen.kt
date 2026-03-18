package com.example.rokidsettingshub.ui.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.rokidsettingshub.R
import com.example.rokidsettingshub.model.HubSection
import com.example.rokidsettingshub.ui.common.SectionCard

@Composable
fun HubScreen(
    currentSection: HubSection?,
    onSectionSelected: (HubSection) -> Unit,
    onBackFromSection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        when (currentSection) {
            HubSection.Bluetooth -> BluetoothPlaceholderScreen(onBack = onBackFromSection)
            HubSection.WiFi,
            HubSection.Battery,
            HubSection.DeviceInfo -> PlaceholderSectionScreen(
                section = currentSection,
                onBack = onBackFromSection,
            )
            null -> HubHome(onSectionSelected = onSectionSelected)
        }
    }
}

@Composable
private fun HubHome(
    onSectionSelected: (HubSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
                supportingText = stringResource(section.cardSupportingTextResId()),
                enabled = true,
                onClick = { onSectionSelected(section) },
            )
        }
    }
}

@Composable
private fun BluetoothPlaceholderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.section_bluetooth_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.bluetooth_placeholder_body),
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

private fun HubSection.titleResId(): Int = when (this) {
    HubSection.Bluetooth -> R.string.section_bluetooth_title
    HubSection.WiFi -> R.string.section_wifi_title
    HubSection.Battery -> R.string.section_battery_title
    HubSection.DeviceInfo -> R.string.section_device_info_title
}

private fun HubSection.cardSupportingTextResId(): Int = when (this) {
    HubSection.Bluetooth -> R.string.section_bluetooth_card_body
    HubSection.WiFi,
    HubSection.Battery,
    HubSection.DeviceInfo -> R.string.section_placeholder_card_body
}
