package com.example.rokidsettingshub.ui.common

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp

internal data class SubmenuCarouselGeometry(
    val centerCardFraction: Float,
    val sidePeekFraction: Float,
)

internal data class SubmenuCarouselItem(
    val key: String,
    val title: String,
    val supportingText: String,
)

internal fun submenuCarouselGeometry(): SubmenuCarouselGeometry = SubmenuCarouselGeometry(
    centerCardFraction = 0.56f,
    sidePeekFraction = 0.22f,
)

internal fun submenuCarouselPageTarget(selectedIndex: Int, itemCount: Int): Int {
    if (itemCount <= 0) {
        return 0
    }

    return selectedIndex.coerceIn(0, itemCount - 1)
}

@Composable
internal fun SubmenuCarousel(
    title: String,
    subtitle: String,
    items: List<SubmenuCarouselItem>,
    selectedIndex: Int,
    onMoveSelection: (Int) -> Unit,
    onActivateSelection: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val geometry = submenuCarouselGeometry()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(selectedIndex, items.size) {
        listState.animateScrollToItem(
            submenuCarouselPageTarget(
                selectedIndex = selectedIndex,
                itemCount = items.size,
            ),
        )
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
                    Key.DirectionDown -> {
                        onMoveSelection(1)
                        true
                    }

                    Key.DirectionLeft,
                    Key.DirectionUp -> {
                        onMoveSelection(-1)
                        true
                    }

                    Key.Enter,
                    Key.NumPadEnter,
                    Key.DirectionCenter -> {
                        onActivateSelection()
                        true
                    }

                    Key.Back -> {
                        onBack()
                        true
                    }

                    else -> false
                }
            }
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
            ) {
                val edgePadding = (maxWidth * geometry.sidePeekFraction) - 8.dp
                val cardWidth = maxWidth * geometry.centerCardFraction

                LazyRow(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentPadding = PaddingValues(horizontal = edgePadding),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    userScrollEnabled = false,
                ) {
                    itemsIndexed(items, key = { _, item -> item.key }) { index, item ->
                        Box(
                            modifier = Modifier.width(cardWidth),
                            contentAlignment = Alignment.Center,
                        ) {
                            SectionCard(
                                title = item.title,
                                supportingText = item.supportingText,
                                enabled = true,
                                selected = index == selectedIndex,
                                onClick = {
                                    if (index == selectedIndex) {
                                        onActivateSelection()
                                    } else {
                                        onSelectItem(index)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
