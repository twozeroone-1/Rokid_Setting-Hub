package com.example.rokidsettingshub.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rokidsettingshub.model.HubSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HubViewModel : ViewModel() {
    private val _currentSection = MutableStateFlow<HubSection?>(null)
    val currentSection: StateFlow<HubSection?> = _currentSection.asStateFlow()

    fun selectSection(section: HubSection) {
        _currentSection.value = section
    }

    fun returnToHub() {
        _currentSection.value = null
    }
}
