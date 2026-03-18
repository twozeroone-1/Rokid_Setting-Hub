package com.example.rokidsettingshub.viewmodel

import com.example.rokidsettingshub.model.HubSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HubViewModelTest {

    @Test
    fun selectingBluetoothUpdatesCurrentSection() {
        val viewModel = HubViewModel()

        assertNull(viewModel.currentSection.value)

        viewModel.selectSection(HubSection.Bluetooth)

        assertEquals(HubSection.Bluetooth, viewModel.currentSection.value)
    }
}
