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
import com.example.rokidsettingshub.model.WifiInfoState
import com.example.rokidsettingshub.ui.common.SubmenuCarousel
import com.example.rokidsettingshub.ui.common.SubmenuCarouselItem
import com.example.rokidsettingshub.ui.common.submenuCarouselPageTarget

data class WifiInfoEntry(
    val label: String,
    val value: String,
)

enum class WifiInfoPage {
    Overview,
    Details,
}

internal data class WifiInfoVisualStyle(
    val titleColor: Color,
    val bodyColor: Color,
    val labelColor: Color,
    val valueColor: Color,
)

internal fun wifiInfoVisualStyle(): WifiInfoVisualStyle = WifiInfoVisualStyle(
    titleColor = Color.White,
    bodyColor = Color.White.copy(alpha = 0.82f),
    labelColor = Color.White.copy(alpha = 0.72f),
    valueColor = Color.White,
)

internal fun nextWifiInfoPage(page: WifiInfoPage): WifiInfoPage = when (page) {
    WifiInfoPage.Overview -> WifiInfoPage.Details
    WifiInfoPage.Details -> WifiInfoPage.Details
}

internal fun previousWifiInfoPage(page: WifiInfoPage): WifiInfoPage = when (page) {
    WifiInfoPage.Overview -> WifiInfoPage.Overview
    WifiInfoPage.Details -> WifiInfoPage.Overview
}

internal fun backFromWifiInfoPage(page: WifiInfoPage): WifiInfoPage? = when (page) {
    WifiInfoPage.Overview -> null
    WifiInfoPage.Details -> WifiInfoPage.Overview
}

internal fun canLaunchWifiSettings(page: WifiInfoPage): Boolean = page == WifiInfoPage.Details

internal fun wifiInfoEntries(
    state: WifiInfoState,
    page: WifiInfoPage,
): List<WifiInfoEntry> = when (page) {
    WifiInfoPage.Overview -> listOf(
        WifiInfoEntry("Hardware", state.hardware),
        WifiInfoEntry("Wi-Fi State", state.wifiState),
        WifiInfoEntry("Connection", state.connection),
    )

    WifiInfoPage.Details -> listOf(
        WifiInfoEntry("Network Name", state.networkName),
        WifiInfoEntry("IP Address", state.ipAddress),
        WifiInfoEntry("Interface", state.interfaceName),
    )
}

internal fun wifiInfoSelectionItems(state: WifiInfoState): List<SubmenuCarouselItem> = listOf(
    SubmenuCarouselItem(
        key = WifiInfoPage.Overview.name,
        title = "Overview",
        supportingText = "${state.wifiState} | ${state.connection}",
    ),
    SubmenuCarouselItem(
        key = WifiInfoPage.Details.name,
        title = "Details",
        supportingText = "${state.networkName} | ${state.ipAddress}",
    ),
)

@Composable
fun WifiInfoScreen(
    state: WifiInfoState,
    onBack: () -> Unit,
    onOpenSystemWifiSettings: () -> Unit,
    registerHardwareBackHandler: ((() -> Boolean)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPageIndex by rememberSaveable { mutableIntStateOf(0) }
    var activePageIndex by rememberSaveable { mutableIntStateOf(-1) }
    val currentPage = WifiInfoPage.entries.getOrNull(activePageIndex)
    val visualStyle = wifiInfoVisualStyle()

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
            title = stringResource(R.string.section_wifi_title),
            subtitle = stringResource(R.string.submenu_focus_hint),
            items = wifiInfoSelectionItems(state),
            selectedIndex = submenuCarouselPageTarget(
                selectedIndex = selectedPageIndex,
                itemCount = WifiInfoPage.entries.size,
            ),
            onMoveSelection = { direction ->
                selectedPageIndex = submenuCarouselPageTarget(
                    selectedIndex = selectedPageIndex + direction,
                    itemCount = WifiInfoPage.entries.size,
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
        WifiInfoDetailScreen(
            state = state,
            page = currentPage,
            visualStyle = visualStyle,
            onBackToSelection = { activePageIndex = -1 },
            onOpenSystemWifiSettings = onOpenSystemWifiSettings,
            modifier = modifier,
        )
    }
}

@Composable
private fun WifiInfoDetailScreen(
    state: WifiInfoState,
    page: WifiInfoPage,
    visualStyle: WifiInfoVisualStyle,
    onBackToSelection: () -> Unit,
    onOpenSystemWifiSettings: () -> Unit,
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
                    Key.DirectionRight,
                    Key.Enter,
                    Key.NumPadEnter,
                    Key.DirectionCenter -> {
                        if (canLaunchWifiSettings(page)) {
                            onOpenSystemWifiSettings()
                            true
                        } else {
                            false
                        }
                    }

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
            text = stringResource(R.string.section_wifi_title),
            style = MaterialTheme.typography.headlineSmall,
            color = visualStyle.titleColor,
        )
        Text(
            text = stringResource(R.string.section_wifi_body),
            style = MaterialTheme.typography.bodyMedium,
            color = visualStyle.bodyColor,
        )

        wifiInfoEntries(state, page).forEach { entry ->
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
        if (page == WifiInfoPage.Details) {
            OutlinedButton(
                onClick = onOpenSystemWifiSettings,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.section_wifi_open_system_settings))
            }
        }
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
