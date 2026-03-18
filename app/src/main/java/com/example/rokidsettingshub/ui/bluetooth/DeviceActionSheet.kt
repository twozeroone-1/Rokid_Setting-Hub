package com.example.rokidsettingshub.ui.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.ManagedDevice

enum class DeviceAction {
    Pair,
    Connect,
    Disconnect,
    SetAsMainPhone,
    Forget,
}

fun visibleActionsFor(device: ManagedDevice): List<DeviceAction> {
    return buildList {
        if (device.connectionState == DeviceConnectionState.Available) {
            add(DeviceAction.Pair)
            return@buildList
        }

        if (device.canConnect) {
            add(DeviceAction.Connect)
        }
        if (device.canDisconnect) {
            add(DeviceAction.Disconnect)
        }
        if (device.canBeMainPhone && !device.isMainPhone) {
            add(DeviceAction.SetAsMainPhone)
        }
        if (device.canForget) {
            add(DeviceAction.Forget)
        }
    }
}

@Composable
fun DeviceActionSheet(
    device: ManagedDevice,
    onActionSelected: (DeviceAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        visibleActionsFor(device).forEach { action ->
            Button(
                onClick = { onActionSelected(action) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(action.label())
            }
        }
    }
}

@Composable
private fun DeviceAction.label(): String = when (this) {
    DeviceAction.Pair -> "Pair"
    DeviceAction.Connect -> "Connect"
    DeviceAction.Disconnect -> "Disconnect"
    DeviceAction.SetAsMainPhone -> "Set as Main Phone"
    DeviceAction.Forget -> "Forget"
}
