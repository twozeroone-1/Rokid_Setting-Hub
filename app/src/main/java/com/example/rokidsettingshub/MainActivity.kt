package com.example.rokidsettingshub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rokidsettingshub.ui.hub.HubScreen
import com.example.rokidsettingshub.viewmodel.HubViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val hubViewModel: HubViewModel = viewModel()
            val currentSection by hubViewModel.currentSection.collectAsState()

            HubScreen(
                currentSection = currentSection,
                onSectionSelected = hubViewModel::selectSection,
                onBackFromSection = hubViewModel::returnToHub,
            )
        }
    }
}
