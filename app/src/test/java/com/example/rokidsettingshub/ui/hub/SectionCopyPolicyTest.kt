package com.example.rokidsettingshub.ui.hub

import com.example.rokidsettingshub.R
import com.example.rokidsettingshub.model.HubSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SectionCopyPolicyTest {

    @Test
    fun wifiUsesDedicatedCardAndPlaceholderCopy() {
        val copy = sectionCopyFor(HubSection.WiFi)

        assertEquals(R.string.section_wifi_card_body, copy.cardBodyResId)
        assertEquals(R.string.section_wifi_placeholder_body, copy.placeholderBodyResId)
        assertNotEquals(R.string.section_placeholder_card_body, copy.cardBodyResId)
    }
}
