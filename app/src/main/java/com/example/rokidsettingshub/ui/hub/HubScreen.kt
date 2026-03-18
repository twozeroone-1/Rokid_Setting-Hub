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
import com.example.rokidsettingshub.model.HubSection
import com.example.rokidsettingshub.ui.common.SectionCard

@Composable
fun HubScreen(
    currentSection: HubSection?,
    onSectionSelected: (HubSection) -> Unit,
    onBackFromBluetooth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        if (currentSection == HubSection.Bluetooth) {
            BluetoothPlaceholderScreen(onBack = onBackFromBluetooth)
        } else {
            HubHome(onSectionSelected = onSectionSelected)
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
            text = "Settings Hub",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Choose a section to continue.",
            style = MaterialTheme.typography.bodyMedium,
        )

        SectionCard(
            title = HubSection.Bluetooth.title,
            supportingText = "Open Bluetooth settings.",
            enabled = true,
            onClick = { onSectionSelected(HubSection.Bluetooth) },
        )
        SectionCard(
            title = HubSection.WiFi.title,
            supportingText = "Placeholder for a later task.",
            enabled = false,
            onClick = {},
        )
        SectionCard(
            title = HubSection.Battery.title,
            supportingText = "Placeholder for a later task.",
            enabled = false,
            onClick = {},
        )
        SectionCard(
            title = HubSection.DeviceInfo.title,
            supportingText = "Placeholder for a later task.",
            enabled = false,
            onClick = {},
        )
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
            text = "Bluetooth",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Bluetooth details will be implemented in a later task.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Back")
        }
    }
}
