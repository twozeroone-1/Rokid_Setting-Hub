package com.example.rokidsettingshub.ui.hub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.rokidsettingshub.R
import com.example.rokidsettingshub.model.DeviceInfoState

data class DeviceInfoEntry(
    val label: String,
    val value: String,
)

enum class DeviceInfoPage {
    Overview,
    Storage,
}

internal data class DeviceInfoVisualStyle(
    val titleColor: Color,
    val bodyColor: Color,
    val labelColor: Color,
    val valueColor: Color,
)

internal fun deviceInfoVisualStyle(): DeviceInfoVisualStyle = DeviceInfoVisualStyle(
    titleColor = Color.White,
    bodyColor = Color.White.copy(alpha = 0.82f),
    labelColor = Color.White.copy(alpha = 0.72f),
    valueColor = Color.White,
)

internal fun nextDeviceInfoPage(page: DeviceInfoPage): DeviceInfoPage = when (page) {
    DeviceInfoPage.Overview -> DeviceInfoPage.Storage
    DeviceInfoPage.Storage -> DeviceInfoPage.Storage
}

internal fun previousDeviceInfoPage(page: DeviceInfoPage): DeviceInfoPage = when (page) {
    DeviceInfoPage.Overview -> DeviceInfoPage.Overview
    DeviceInfoPage.Storage -> DeviceInfoPage.Overview
}

internal fun backFromDeviceInfoPage(page: DeviceInfoPage): DeviceInfoPage? = when (page) {
    DeviceInfoPage.Overview -> null
    DeviceInfoPage.Storage -> DeviceInfoPage.Overview
}

internal fun deviceInfoEntries(
    state: DeviceInfoState,
    page: DeviceInfoPage,
): List<DeviceInfoEntry> = when (page) {
    DeviceInfoPage.Overview -> listOf(
        DeviceInfoEntry(label = "Model", value = state.modelName),
        DeviceInfoEntry(label = "Android Version", value = state.androidVersion),
        DeviceInfoEntry(label = "Total Storage", value = state.totalStorage),
    )

    DeviceInfoPage.Storage -> listOf(
        DeviceInfoEntry(label = "Used Storage", value = state.usedStorage),
        DeviceInfoEntry(label = "Free Storage", value = state.freeStorage),
    )
}

@Composable
fun DeviceInfoScreen(
    state: DeviceInfoState,
    onBack: () -> Unit,
    registerHardwareBackHandler: ((() -> Boolean)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pageIndex by rememberSaveable { mutableIntStateOf(0) }
    val currentPage = DeviceInfoPage.entries[pageIndex]
    val visualStyle = deviceInfoVisualStyle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BackHandler {
        val backTarget = backFromDeviceInfoPage(currentPage)
        if (backTarget == null) {
            onBack()
        } else {
            pageIndex = backTarget.ordinal
        }
    }

    DisposableEffect(currentPage, onBack, registerHardwareBackHandler) {
        registerHardwareBackHandler {
            val backTarget = backFromDeviceInfoPage(currentPage)
            if (backTarget == null) {
                onBack()
            } else {
                pageIndex = backTarget.ordinal
            }
            true
        }
        onDispose {
            registerHardwareBackHandler(null)
        }
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
                    Key.DirectionDown,
                    Key.Enter,
                    Key.NumPadEnter,
                    Key.DirectionCenter -> {
                        val nextPage = nextDeviceInfoPage(currentPage)
                        if (nextPage != currentPage) {
                            pageIndex = nextPage.ordinal
                            true
                        } else {
                            false
                        }
                    }

                    Key.DirectionUp -> {
                        val previousPage = previousDeviceInfoPage(currentPage)
                        if (previousPage != currentPage) {
                            pageIndex = previousPage.ordinal
                            true
                        } else {
                            false
                        }
                    }

                    Key.DirectionLeft,
                    Key.Back -> {
                        val backTarget = backFromDeviceInfoPage(currentPage)
                        if (backTarget == null) {
                            onBack()
                        } else {
                            pageIndex = backTarget.ordinal
                        }
                        true
                    }

                    else -> false
                }
            }
            .then(
                if (currentPage == DeviceInfoPage.Overview) {
                    Modifier.clickable { pageIndex = DeviceInfoPage.Storage.ordinal }
                } else {
                    Modifier
                },
            )
            .fillMaxWidth()
            .padding(20.dp)
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.section_device_info_title),
            style = MaterialTheme.typography.headlineSmall,
            color = visualStyle.titleColor,
        )
        Text(
            text = stringResource(R.string.section_device_info_body),
            style = MaterialTheme.typography.bodyMedium,
            color = visualStyle.bodyColor,
        )
        Text(
            text = if (currentPage == DeviceInfoPage.Overview) {
                stringResource(R.string.section_device_info_hint_next)
            } else {
                stringResource(R.string.section_device_info_hint_previous)
            },
            style = MaterialTheme.typography.bodySmall,
            color = visualStyle.bodyColor,
        )

        deviceInfoEntries(state = state, page = currentPage).forEach { entry ->
            Text(
                text = entry.label,
                style = MaterialTheme.typography.titleSmall,
                color = visualStyle.labelColor,
            )
            Text(
                text = entry.value,
                style = MaterialTheme.typography.bodyLarge,
                color = visualStyle.valueColor,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (currentPage == DeviceInfoPage.Storage) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.White.copy(alpha = 0.4f)),
            ) {
                Text(
                    text = stringResource(R.string.back),
                    color = Color.White,
                )
            }
        }
    }
}
