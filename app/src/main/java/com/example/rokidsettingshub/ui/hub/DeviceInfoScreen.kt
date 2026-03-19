package com.example.rokidsettingshub.ui.hub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rokidsettingshub.R
import com.example.rokidsettingshub.model.DeviceInfoState
import com.example.rokidsettingshub.ui.common.SubmenuCarousel
import com.example.rokidsettingshub.ui.common.SubmenuCarouselItem
import com.example.rokidsettingshub.ui.common.submenuCarouselPageTarget

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

internal fun deviceInfoSelectionItems(state: DeviceInfoState): List<SubmenuCarouselItem> = listOf(
    SubmenuCarouselItem(
        key = DeviceInfoPage.Overview.name,
        title = "Overview",
        supportingText = "${state.modelName} | ${state.androidVersion}",
    ),
    SubmenuCarouselItem(
        key = DeviceInfoPage.Storage.name,
        title = "Storage",
        supportingText = "${state.freeStorage} free",
    ),
)

@Composable
fun DeviceInfoScreen(
    state: DeviceInfoState,
    onBack: () -> Unit,
    registerHardwareBackHandler: ((() -> Boolean)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPageIndex by rememberSaveable { mutableIntStateOf(0) }
    var activePageIndex by rememberSaveable { mutableIntStateOf(-1) }
    val currentPage = DeviceInfoPage.entries.getOrNull(activePageIndex)
    val visualStyle = deviceInfoVisualStyle()

    BackHandler {
        if (currentPage == null) {
            onBack()
        } else {
            activePageIndex = -1
        }
    }

    DisposableEffect(currentPage, onBack, registerHardwareBackHandler) {
        registerHardwareBackHandler {
            if (currentPage == null) {
                onBack()
            } else {
                activePageIndex = -1
            }
            true
        }
        onDispose {
            registerHardwareBackHandler(null)
        }
    }

    if (currentPage == null) {
        SubmenuCarousel(
            title = stringResource(R.string.section_device_info_title),
            subtitle = stringResource(R.string.submenu_focus_hint),
            items = deviceInfoSelectionItems(state),
            selectedIndex = submenuCarouselPageTarget(
                selectedIndex = selectedPageIndex,
                itemCount = DeviceInfoPage.entries.size,
            ),
            onMoveSelection = { direction ->
                selectedPageIndex = submenuCarouselPageTarget(
                    selectedIndex = selectedPageIndex + direction,
                    itemCount = DeviceInfoPage.entries.size,
                )
            },
            onActivateSelection = {
                activePageIndex = selectedPageIndex
            },
            onSelectItem = { index ->
                selectedPageIndex = index
            },
            onBack = onBack,
            modifier = modifier,
        )
    } else {
        DeviceInfoDetailScreen(
            state = state,
            page = currentPage,
            visualStyle = visualStyle,
            onBackToSelection = { activePageIndex = -1 },
            modifier = modifier,
        )
    }
}

@Composable
private fun DeviceInfoDetailScreen(
    state: DeviceInfoState,
    page: DeviceInfoPage,
    visualStyle: DeviceInfoVisualStyle,
    onBackToSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) {
                    return@onPreviewKeyEvent false
                }

                when (event.key) {
                    Key.DirectionLeft,
                    Key.Back -> {
                        onBackToSelection()
                        true
                    }

                    else -> false
                }
            }
            .fillMaxWidth()
            .padding(20.dp),
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

        deviceInfoEntries(state = state, page = page).forEach { entry ->
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
        OutlinedButton(
            onClick = onBackToSelection,
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
