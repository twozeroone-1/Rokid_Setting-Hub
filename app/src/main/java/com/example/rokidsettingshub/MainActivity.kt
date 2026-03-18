package com.example.rokidsettingshub

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rokidsettingshub.data.bluetooth.createBluetoothRepository
import com.example.rokidsettingshub.data.storage.MainPhoneStore
import com.example.rokidsettingshub.ui.hub.HubScreen
import com.example.rokidsettingshub.viewmodel.HubViewModel

class MainActivity : ComponentActivity() {
    private val bluetoothRepository by lazy {
        createBluetoothRepository(
            context = applicationContext,
            mainPhoneStore = MainPhoneStore(
                sharedPreferences = getSharedPreferences(MAIN_PHONE_PREFS, MODE_PRIVATE),
            ),
        )
    }

    private val hubViewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HubViewModel(bluetoothRepository = bluetoothRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        setContent {
            val hubViewModel: HubViewModel = viewModel(factory = hubViewModelFactory)
            val currentSection by hubViewModel.currentSection.collectAsState()
            val bluetoothScreenState by hubViewModel.bluetoothScreenState.collectAsState()
            val bluetoothFocusState by hubViewModel.bluetoothFocusState.collectAsState()

            HubScreen(
                currentSection = currentSection,
                bluetoothState = bluetoothScreenState,
                bluetoothFocusState = bluetoothFocusState,
                onMoveBluetoothFocus = hubViewModel::moveBluetoothFocus,
                onStartBluetoothScan = hubViewModel::startBluetoothScan,
                onStopBluetoothScan = hubViewModel::stopBluetoothScan,
                onPairBluetoothDevice = hubViewModel::pairBluetoothDevice,
                onOpenBluetoothDeviceDetails = hubViewModel::openBluetoothDeviceDetails,
                onSelectBluetoothSection = hubViewModel::selectBluetoothSection,
                onActivateBluetoothSection = hubViewModel::activateSelectedBluetoothSection,
                onBackFromBluetoothDetail = hubViewModel::handleBluetoothBack,
                onSectionSelected = hubViewModel::selectSection,
                onBackFromSection = hubViewModel::returnToHub,
            )
        }
    }
}

private const val MAIN_PHONE_PREFS = "main_phone_prefs"
