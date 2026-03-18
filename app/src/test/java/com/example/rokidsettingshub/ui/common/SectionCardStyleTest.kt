package com.example.rokidsettingshub.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SectionCardStyleTest {

    @Test
    fun selectedCardUsesTransparentBackgroundAndEmphasizedBorder() {
        val style = sectionCardVisualStyle(selected = true)

        assertEquals(Color.Transparent, style.containerColor)
        assertEquals(3.dp, style.borderWidth)
        assertEquals(Color.White, style.titleColor)
        assertEquals(Color.White.copy(alpha = 0.82f), style.supportingTextColor)
        assertTrue(style.isSelected)
    }

    @Test
    fun unselectedCardAlsoUsesTransparentBackgroundWithThinBorder() {
        val style = sectionCardVisualStyle(selected = false)

        assertEquals(Color.Transparent, style.containerColor)
        assertEquals(1.dp, style.borderWidth)
        assertEquals(Color.White, style.titleColor)
        assertEquals(Color.White.copy(alpha = 0.82f), style.supportingTextColor)
        assertTrue(!style.isSelected)
    }
}
