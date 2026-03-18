package com.example.rokidsettingshub.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class SectionCardVisualStyle(
    val containerColor: Color,
    val borderWidth: Dp,
    val titleColor: Color,
    val supportingTextColor: Color,
    val isSelected: Boolean,
)

internal fun sectionCardVisualStyle(selected: Boolean): SectionCardVisualStyle =
    SectionCardVisualStyle(
        containerColor = Color.Transparent,
        borderWidth = if (selected) 3.dp else 1.dp,
        titleColor = Color.White,
        supportingTextColor = Color.White.copy(alpha = 0.82f),
        isSelected = selected,
    )

@Composable
fun SectionCard(
    title: String,
    supportingText: String,
    enabled: Boolean,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visualStyle = sectionCardVisualStyle(selected = selected)

    Card(
        modifier = modifier
            .border(
                width = visualStyle.borderWidth,
                color = if (visualStyle.isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                },
                shape = RoundedCornerShape(12.dp),
            )
            .defaultMinSize(minHeight = 220.dp)
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = visualStyle.containerColor,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = visualStyle.titleColor,
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = visualStyle.supportingTextColor,
            )
        }
    }
}
