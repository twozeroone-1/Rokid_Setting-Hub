package com.example.rokidsettingshub.ui.hub

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
import com.example.rokidsettingshub.model.HubSection

@Composable
fun PlaceholderSectionScreen(
    section: HubSection,
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
            text = stringResource(section.placeholderTitleResId()),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(section.placeholderBodyResId()),
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

private fun HubSection.placeholderTitleResId(): Int = when (this) {
    HubSection.Bluetooth -> R.string.section_bluetooth_title
    HubSection.WiFi -> R.string.section_wifi_title
    HubSection.Battery -> R.string.section_battery_title
    HubSection.DeviceInfo -> R.string.section_device_info_title
}

private fun HubSection.placeholderBodyResId(): Int = when (this) {
    HubSection.Bluetooth -> R.string.bluetooth_placeholder_body
    HubSection.WiFi -> R.string.section_wifi_placeholder_body
    HubSection.Battery -> R.string.section_battery_placeholder_body
    HubSection.DeviceInfo -> R.string.section_device_info_placeholder_body
}
