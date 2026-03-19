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
import com.example.rokidsettingshub.model.BatteryInfoState
import com.example.rokidsettingshub.ui.common.SubmenuCarousel
import com.example.rokidsettingshub.ui.common.SubmenuCarouselItem
import com.example.rokidsettingshub.ui.common.submenuCarouselPageTarget

data class BatteryInfoEntry(
    val label: String,
    val value: String,
)

enum class BatteryInfoPage {
    Overview,
    Details,
}

internal data class BatteryInfoVisualStyle(
    val titleColor: Color,
    val bodyColor: Color,
    val labelColor: Color,
    val valueColor: Color,
)

internal fun batteryInfoVisualStyle(): BatteryInfoVisualStyle = BatteryInfoVisualStyle(
    titleColor = Color.White,
    bodyColor = Color.White.copy(alpha = 0.82f),
    labelColor = Color.White.copy(alpha = 0.72f),
    valueColor = Color.White,
)

internal fun nextBatteryInfoPage(page: BatteryInfoPage): BatteryInfoPage = when (page) {
    BatteryInfoPage.Overview -> BatteryInfoPage.Details
    BatteryInfoPage.Details -> BatteryInfoPage.Details
}

internal fun previousBatteryInfoPage(page: BatteryInfoPage): BatteryInfoPage = when (page) {
    BatteryInfoPage.Overview -> BatteryInfoPage.Overview
    BatteryInfoPage.Details -> BatteryInfoPage.Overview
}

internal fun backFromBatteryInfoPage(page: BatteryInfoPage): BatteryInfoPage? = when (page) {
    BatteryInfoPage.Overview -> null
    BatteryInfoPage.Details -> BatteryInfoPage.Overview
}

internal fun batteryInfoEntries(
    state: BatteryInfoState,
    page: BatteryInfoPage,
): List<BatteryInfoEntry> = when (page) {
    BatteryInfoPage.Overview -> listOf(
        BatteryInfoEntry("Battery Level", state.level),
        BatteryInfoEntry("Status", state.status),
        BatteryInfoEntry("Health", state.health),
    )

    BatteryInfoPage.Details -> listOf(
        BatteryInfoEntry("Temperature", state.temperature),
        BatteryInfoEntry("Technology", state.technology),
        BatteryInfoEntry("Charge Cycles", state.cycleCount),
    )
}

internal fun batteryInfoSelectionItems(state: BatteryInfoState): List<SubmenuCarouselItem> = listOf(
    SubmenuCarouselItem(
        key = BatteryInfoPage.Overview.name,
        title = "Overview",
        supportingText = "${state.level} | ${state.status}",
    ),
    SubmenuCarouselItem(
        key = BatteryInfoPage.Details.name,
        title = "Details",
        supportingText = "${state.temperature} | ${state.cycleCount} cycles",
    ),
)

@Composable
fun BatteryInfoScreen(
    state: BatteryInfoState,
    onBack: () -> Unit,
    registerHardwareBackHandler: ((() -> Boolean)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPageIndex by rememberSaveable { mutableIntStateOf(0) }
    var activePageIndex by rememberSaveable { mutableIntStateOf(-1) }
    val currentPage = BatteryInfoPage.entries.getOrNull(activePageIndex)
    val visualStyle = batteryInfoVisualStyle()

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
            title = stringResource(R.string.section_battery_title),
            subtitle = stringResource(R.string.submenu_focus_hint),
            items = batteryInfoSelectionItems(state),
            selectedIndex = submenuCarouselPageTarget(
                selectedIndex = selectedPageIndex,
                itemCount = BatteryInfoPage.entries.size,
            ),
            onMoveSelection = { direction ->
                selectedPageIndex = submenuCarouselPageTarget(
                    selectedIndex = selectedPageIndex + direction,
                    itemCount = BatteryInfoPage.entries.size,
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
        BatteryInfoDetailScreen(
            state = state,
            page = currentPage,
            visualStyle = visualStyle,
            onBackToSelection = { activePageIndex = -1 },
            modifier = modifier,
        )
    }
}

@Composable
private fun BatteryInfoDetailScreen(
    state: BatteryInfoState,
    page: BatteryInfoPage,
    visualStyle: BatteryInfoVisualStyle,
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
            text = stringResource(R.string.section_battery_title),
            style = MaterialTheme.typography.headlineSmall,
            color = visualStyle.titleColor,
        )
        Text(
            text = stringResource(R.string.section_battery_body),
            style = MaterialTheme.typography.bodyMedium,
            color = visualStyle.bodyColor,
        )

        batteryInfoEntries(state, page).forEach { entry ->
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
