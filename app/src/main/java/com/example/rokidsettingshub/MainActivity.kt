package com.example.rokidsettingshub

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.example.rokidsettingshub.data.bluetooth.BluetoothPermissionRequirements
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rokidsettingshub.data.bluetooth.createBluetoothRepository
import com.example.rokidsettingshub.data.batteryinfo.AndroidBatteryInfoSource
import com.example.rokidsettingshub.data.deviceinfo.AndroidDeviceInfoSource
import com.example.rokidsettingshub.data.storage.MainPhoneStore
import com.example.rokidsettingshub.data.wifiinfo.AndroidWifiInfoSource
import com.example.rokidsettingshub.ui.hub.HubScreen
import com.example.rokidsettingshub.viewmodel.HubViewModel

class MainActivity : ComponentActivity() {
    private var hardwareBackHandler: (() -> Boolean)? = null
    private var pendingBluetoothAction: (() -> Unit)? = null
    private var activeHubViewModel: HubViewModel? = null
    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val granted = results.values.all { it }
            val pendingAction = pendingBluetoothAction
            pendingBluetoothAction = null
            if (granted) {
                pendingAction?.invoke()
            } else {
                activeHubViewModel?.noteMissingBluetoothPermission()
            }
        }

    private val bluetoothRepository by lazy {
        createBluetoothRepository(
            context = applicationContext,
            mainPhoneStore = MainPhoneStore(
                sharedPreferences = getSharedPreferences(MAIN_PHONE_PREFS, MODE_PRIVATE),
            ),
        )
    }

    private val hubViewModelFactory by lazy {
        val wifiInfoSource = AndroidWifiInfoSource(
            context = applicationContext,
        )
        val batteryInfoSource = AndroidBatteryInfoSource(
            context = applicationContext,
        )
        val deviceInfoSource = AndroidDeviceInfoSource(
            dataDirectory = applicationContext.filesDir,
        )
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HubViewModel(
                    bluetoothRepository = bluetoothRepository,
                    wifiInfoSource = wifiInfoSource,
                    batteryInfoSource = batteryInfoSource,
                    deviceInfoSource = deviceInfoSource,
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        setContent {
            val hubViewModel: HubViewModel = viewModel(factory = hubViewModelFactory)
            SideEffect {
                activeHubViewModel = hubViewModel
            }
            val currentSection by hubViewModel.currentSection.collectAsState()
            val selectedHubSection by hubViewModel.selectedHubSection.collectAsState()
            val bluetoothScreenState by hubViewModel.bluetoothScreenState.collectAsState()
            val wifiInfoState by hubViewModel.wifiInfoState.collectAsState()
            val batteryInfoState by hubViewModel.batteryInfoState.collectAsState()
            val deviceInfoState by hubViewModel.deviceInfoState.collectAsState()
            val bluetoothFocusState by hubViewModel.bluetoothFocusState.collectAsState()

            HubScreen(
                currentSection = currentSection,
                selectedHubSection = selectedHubSection,
                bluetoothState = bluetoothScreenState,
                wifiInfoState = wifiInfoState,
                batteryInfoState = batteryInfoState,
                deviceInfoState = deviceInfoState,
                bluetoothFocusState = bluetoothFocusState,
                onMoveHubSelection = hubViewModel::moveHubSelection,
                onActivateHubSection = hubViewModel::activateSelectedHubSection,
                onMoveBluetoothFocus = hubViewModel::moveBluetoothFocus,
                onStartBluetoothScan = {
                    runWithBluetoothPermissions {
                        hubViewModel.startBluetoothScan()
                    }
                },
                onStopBluetoothScan = hubViewModel::stopBluetoothScan,
                onPairBluetoothDevice = { address ->
                    runWithBluetoothPermissions {
                        hubViewModel.pairBluetoothDevice(address)
                    }
                },
                onOpenBluetoothDeviceDetails = { address ->
                    runWithBluetoothPermissions {
                        hubViewModel.openBluetoothDeviceDetails(address)
                    }
                },
                onSelectBluetoothSection = hubViewModel::selectBluetoothSection,
                onActivateBluetoothSection = hubViewModel::activateSelectedBluetoothSection,
                onBackFromBluetoothDetail = hubViewModel::handleBluetoothBack,
                onSectionSelected = hubViewModel::selectSection,
                onBackFromSection = hubViewModel::returnToHub,
                onOpenSystemBluetoothSettings = ::openSystemBluetoothSettings,
                onOpenSystemWifiSettings = ::openSystemWifiSettings,
                onRegisterHardwareBackHandler = { handler -> hardwareBackHandler = handler },
            )
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (hardwareBackHandler?.invoke() == true) {
                return true
            }
        }

        return super.dispatchKeyEvent(event)
    }

    private fun runWithBluetoothPermissions(action: () -> Unit) {
        val requiredPermissions = BluetoothPermissionRequirements.runtimePermissionsFor(Build.VERSION.SDK_INT)
        if (requiredPermissions.isEmpty() || requiredPermissions.all(::hasPermission)) {
            action()
            return
        }

        pendingBluetoothAction = action
        bluetoothPermissionLauncher.launch(requiredPermissions.toTypedArray())
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun openSystemBluetoothSettings() {
        val bluetoothIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
        val targetIntent = if (bluetoothIntent.resolveActivity(packageManager) != null) {
            bluetoothIntent
        } else {
            fallbackIntent
        }

        runCatching {
            startActivity(targetIntent)
        }
    }

    private fun openSystemWifiSettings() {
        val wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
        val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
        val targetIntent = if (wifiIntent.resolveActivity(packageManager) != null) {
            wifiIntent
        } else {
            fallbackIntent
        }

        runCatching {
            startActivity(targetIntent)
        }
    }
}

private const val MAIN_PHONE_PREFS = "main_phone_prefs"
