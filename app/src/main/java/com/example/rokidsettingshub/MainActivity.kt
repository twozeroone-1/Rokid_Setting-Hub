package com.example.rokidsettingshub

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
        window.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        setContent {
            val hubViewModel: HubViewModel = viewModel()
            val currentSection by hubViewModel.currentSection.collectAsState()
            val bluetoothScreenState by hubViewModel.bluetoothScreenState.collectAsState()
            val bluetoothFocusState by hubViewModel.bluetoothFocusState.collectAsState()

            HubScreen(
                currentSection = currentSection,
                bluetoothState = bluetoothScreenState,
                bluetoothFocusState = bluetoothFocusState,
                onMoveBluetoothFocus = hubViewModel::moveBluetoothFocus,
                onSelectBluetoothSection = hubViewModel::selectBluetoothSection,
                onActivateBluetoothSection = hubViewModel::activateSelectedBluetoothSection,
                onBackFromBluetoothDetail = hubViewModel::handleBluetoothBack,
                onSectionSelected = hubViewModel::selectSection,
                onBackFromSection = hubViewModel::returnToHub,
            )
        }
    }
}
