package com.example.rokidsettingshub.ui.hub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.rokidsettingshub.model.BatteryInfoState

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

@Composable
fun BatteryInfoScreen(
    state: BatteryInfoState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pageIndex by rememberSaveable { mutableIntStateOf(0) }
    val currentPage = BatteryInfoPage.entries[pageIndex]
    val visualStyle = batteryInfoVisualStyle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BackHandler {
        if (currentPage == BatteryInfoPage.Details) {
            pageIndex = BatteryInfoPage.Overview.ordinal
        } else {
            onBack()
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
                    Key.DirectionDown,
                    Key.Enter,
                    Key.NumPadEnter,
                    Key.DirectionCenter -> {
                        val nextPage = nextBatteryInfoPage(currentPage)
                        if (nextPage != currentPage) {
                            pageIndex = nextPage.ordinal
                            true
                        } else {
                            false
                        }
                    }

                    Key.DirectionUp -> {
                        val previousPage = previousBatteryInfoPage(currentPage)
                        if (previousPage != currentPage) {
                            pageIndex = previousPage.ordinal
                            true
                        } else {
                            false
                        }
                    }

                    else -> false
                }
            }
            .then(
                if (currentPage == BatteryInfoPage.Overview) {
                    Modifier.clickable { pageIndex = BatteryInfoPage.Details.ordinal }
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
            text = stringResource(R.string.section_battery_title),
            style = MaterialTheme.typography.headlineSmall,
            color = visualStyle.titleColor,
        )
        Text(
            text = stringResource(R.string.section_battery_body),
            style = MaterialTheme.typography.bodyMedium,
            color = visualStyle.bodyColor,
        )
        Text(
            text = if (currentPage == BatteryInfoPage.Overview) {
                stringResource(R.string.section_battery_hint_next)
            } else {
                stringResource(R.string.section_battery_hint_previous)
            },
            style = MaterialTheme.typography.bodySmall,
            color = visualStyle.bodyColor,
        )

        batteryInfoEntries(state, currentPage).forEach { entry ->
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
        if (currentPage == BatteryInfoPage.Details) {
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
