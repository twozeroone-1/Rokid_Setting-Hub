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
import com.example.rokidsettingshub.model.WifiInfoState

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

@Composable
fun WifiInfoScreen(
    state: WifiInfoState,
    onBack: () -> Unit,
    registerHardwareBackHandler: ((() -> Boolean)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pageIndex by rememberSaveable { mutableIntStateOf(0) }
    val currentPage = WifiInfoPage.entries[pageIndex]
    val visualStyle = wifiInfoVisualStyle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BackHandler {
        val backTarget = backFromWifiInfoPage(currentPage)
        if (backTarget == null) {
            onBack()
        } else {
            pageIndex = backTarget.ordinal
        }
    }

    DisposableEffect(currentPage, onBack, registerHardwareBackHandler) {
        registerHardwareBackHandler {
            val backTarget = backFromWifiInfoPage(currentPage)
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
                        val nextPage = nextWifiInfoPage(currentPage)
                        if (nextPage != currentPage) {
                            pageIndex = nextPage.ordinal
                            true
                        } else {
                            false
                        }
                    }

                    Key.DirectionUp -> {
                        val previousPage = previousWifiInfoPage(currentPage)
                        if (previousPage != currentPage) {
                            pageIndex = previousPage.ordinal
                            true
                        } else {
                            false
                        }
                    }

                    Key.DirectionLeft,
                    Key.Back -> {
                        val backTarget = backFromWifiInfoPage(currentPage)
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
                if (currentPage == WifiInfoPage.Overview) {
                    Modifier.clickable { pageIndex = WifiInfoPage.Details.ordinal }
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
            text = stringResource(R.string.section_wifi_title),
            style = MaterialTheme.typography.headlineSmall,
            color = visualStyle.titleColor,
        )
        Text(
            text = stringResource(R.string.section_wifi_body),
            style = MaterialTheme.typography.bodyMedium,
            color = visualStyle.bodyColor,
        )
        Text(
            text = if (currentPage == WifiInfoPage.Overview) {
                stringResource(R.string.section_wifi_hint_next)
            } else {
                stringResource(R.string.section_wifi_hint_previous)
            },
            style = MaterialTheme.typography.bodySmall,
            color = visualStyle.bodyColor,
        )

        wifiInfoEntries(state, currentPage).forEach { entry ->
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
        if (currentPage == WifiInfoPage.Details) {
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
