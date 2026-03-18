package com.example.rokidsettingshub.data.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.rokidsettingshub.data.storage.MainPhoneStore
import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.ManagedDevice
import java.util.Locale

fun createBluetoothRepository(
    context: Context,
    mainPhoneStore: MainPhoneStore,
): BluetoothRepository {
    val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter = bluetoothManager?.adapter
        ?: return BluetoothRepository(
            bondedDeviceSource = EmptyBondedDeviceSource,
            scanner = NoOpBluetoothScanner,
            deviceActions = NoOpBluetoothDeviceActions,
            loadMainPhone = mainPhoneStore::load,
        )

    val stateTracker = AndroidBluetoothStateTracker(bluetoothManager)

    return BluetoothRepository(
        bondedDeviceSource = AndroidBondedDeviceSource(bluetoothAdapter, stateTracker),
        scanner = AndroidBluetoothScanner(
            context = context.applicationContext,
            bluetoothAdapter = bluetoothAdapter,
            stateTracker = stateTracker,
        ),
        deviceActions = AndroidBluetoothDeviceActions(
            context = context.applicationContext,
            bluetoothAdapter = bluetoothAdapter,
        ),
        loadMainPhone = mainPhoneStore::load,
        confirmForget = { true },
    )
}

private class AndroidBondedDeviceSource(
    private val bluetoothAdapter: BluetoothAdapter,
    private val stateTracker: AndroidBluetoothStateTracker,
) : BondedDeviceSource {
    override fun loadBondedDevices(): List<ManagedDevice> {
        return runCatching {
            bluetoothAdapter.bondedDevices.orEmpty()
                .map { device ->
                    device.toManagedDevice(
                        connectionState = if (stateTracker.isConnected(device.address)) {
                            DeviceConnectionState.Connected
                        } else {
                            DeviceConnectionState.Paired
                        },
                    )
                }
                .sortedBy { it.name.lowercase(Locale.ROOT) }
        }.getOrDefault(emptyList())
    }
}

private class AndroidBluetoothScanner(
    context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val stateTracker: AndroidBluetoothStateTracker,
) : BluetoothScanner {
    private val applicationContext = context.applicationContext
    private val discoveredDevices = linkedMapOf<String, ManagedDevice>()
    private var scanResultsListener: (List<ManagedDevice>) -> Unit = {}
    private var scanStateListener: (Boolean) -> Unit = {}
    private var deviceStateChangedListener: () -> Unit = {}
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    discoveredDevices.clear()
                    scanResultsListener(emptyList())
                    scanStateListener(true)
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    scanStateListener(false)
                }

                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.readBluetoothDevice() ?: return
                    if (device.safeBondState() == BluetoothDevice.BOND_BONDED) {
                        return
                    }

                    val managedDevice = device.toManagedDevice(
                        connectionState = DeviceConnectionState.Available,
                    )
                    discoveredDevices[managedDevice.address] = managedDevice
                    scanResultsListener(discoveredDevices.values.toList())
                }

                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device = intent.readBluetoothDevice()
                    if (device?.safeBondState() == BluetoothDevice.BOND_BONDED) {
                        discoveredDevices.remove(device.address)
                        scanResultsListener(discoveredDevices.values.toList())
                    }
                    deviceStateChangedListener()
                }

                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val device = intent.readBluetoothDevice() ?: return
                    stateTracker.markConnected(device.address)
                    deviceStateChangedListener()
                }

                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val device = intent.readBluetoothDevice() ?: return
                    stateTracker.markDisconnected(device.address)
                    deviceStateChangedListener()
                }

                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val nextState = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR,
                    )
                    if (nextState == BluetoothAdapter.STATE_OFF) {
                        discoveredDevices.clear()
                        stateTracker.clear()
                        scanResultsListener(emptyList())
                        scanStateListener(false)
                    }
                    deviceStateChangedListener()
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        ContextCompat.registerReceiver(
            applicationContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit) {
        scanResultsListener = listener
    }

    override fun setScanStateListener(listener: (Boolean) -> Unit) {
        scanStateListener = listener
    }

    override fun setDeviceStateChangedListener(listener: () -> Unit) {
        deviceStateChangedListener = listener
    }

    override fun startScan(): Boolean {
        discoveredDevices.clear()
        scanResultsListener(emptyList())
        if (runCatching { bluetoothAdapter.isDiscovering }.getOrDefault(false)) {
            runCatching { bluetoothAdapter.cancelDiscovery() }
        }
        return runCatching { bluetoothAdapter.startDiscovery() }.getOrDefault(false)
    }

    override fun stopScan() {
        if (runCatching { bluetoothAdapter.isDiscovering }.getOrDefault(false)) {
            runCatching { bluetoothAdapter.cancelDiscovery() }
        }
        scanStateListener(false)
    }
}

private class AndroidBluetoothDeviceActions(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
) : BluetoothDeviceActions {
    override fun pair(address: String): Boolean {
        return runCatching {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }
            bluetoothAdapter.getRemoteDevice(address).createBond()
        }.getOrDefault(false)
    }

    override fun connect(address: String) {
        openBluetoothDeviceDetails(address)
    }

    override fun disconnect(address: String) {
        openBluetoothDeviceDetails(address)
    }

    override fun openDetails(address: String) {
        openBluetoothDeviceDetails(address)
    }

    override fun forget(address: String) {
        openBluetoothDeviceDetails(address)
    }

    private fun openBluetoothDeviceDetails(address: String) {
        val detailIntent = Intent(ACTION_BLUETOOTH_DEVICE_DETAIL_SETTINGS).apply {
            setPackage(ANDROID_SETTINGS_PACKAGE)
            putExtra(
                EXTRA_SHOW_FRAGMENT_ARGUMENTS,
                Bundle().apply {
                    putString(KEY_BLUETOOTH_ADDRESS, address)
                },
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val fallbackIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val intentToLaunch = when {
            detailIntent.resolveActivity(context.packageManager) != null -> detailIntent
            fallbackIntent.resolveActivity(context.packageManager) != null -> fallbackIntent
            else -> null
        } ?: return

        runCatching {
            context.startActivity(intentToLaunch)
        }
    }
}

private class AndroidBluetoothStateTracker(
    private val bluetoothManager: BluetoothManager,
) {
    private val connectedAddresses = linkedSetOf<String>()

    init {
        seedFromPublicProfiles()
    }

    fun isConnected(address: String): Boolean = connectedAddresses.contains(address)

    fun markConnected(address: String) {
        connectedAddresses += address
    }

    fun markDisconnected(address: String) {
        connectedAddresses -= address
    }

    fun clear() {
        connectedAddresses.clear()
    }

    private fun seedFromPublicProfiles() {
        PUBLIC_PROFILE_IDS.forEach { profileId ->
            runCatching { bluetoothManager.getConnectedDevices(profileId) }
                .getOrDefault(emptyList())
                .forEach { device -> connectedAddresses += device.address }
        }
    }
}

private object EmptyBondedDeviceSource : BondedDeviceSource {
    override fun loadBondedDevices(): List<ManagedDevice> = emptyList()
}

private object NoOpBluetoothScanner : BluetoothScanner {
    override fun setScanResultsListener(listener: (List<ManagedDevice>) -> Unit) = Unit

    override fun setScanStateListener(listener: (Boolean) -> Unit) = Unit

    override fun setDeviceStateChangedListener(listener: () -> Unit) = Unit

    override fun startScan(): Boolean = false

    override fun stopScan() = Unit
}

private object NoOpBluetoothDeviceActions : BluetoothDeviceActions {
    override fun pair(address: String): Boolean = false

    override fun connect(address: String) = Unit

    override fun disconnect(address: String) = Unit

    override fun openDetails(address: String) = Unit

    override fun forget(address: String) = Unit
}

private fun BluetoothDevice.toManagedDevice(connectionState: DeviceConnectionState): ManagedDevice {
    val safeName = runCatching { name }
        .getOrNull()
        .takeUnless { it.isNullOrBlank() }
        ?: address
    return ManagedDevice(
        address = address,
        name = safeName,
        deviceType = bluetoothClass.toDeviceType(safeName),
        connectionState = connectionState,
    )
}

private fun BluetoothClass?.toDeviceType(deviceName: String): DeviceType {
    return when (this?.majorDeviceClass) {
        BluetoothClass.Device.Major.PHONE -> DeviceType.Phone
        BluetoothClass.Device.Major.AUDIO_VIDEO -> DeviceType.Audio
        BluetoothClass.Device.Major.PERIPHERAL -> {
            if (deviceName.contains("remote", ignoreCase = true)) {
                DeviceType.RemoteHid
            } else {
                DeviceType.KeyboardMouse
            }
        }

        else -> {
            if (deviceName.contains("remote", ignoreCase = true)) {
                DeviceType.RemoteHid
            } else {
                DeviceType.Unknown
            }
        }
    }
}

private fun Intent.readBluetoothDevice(): BluetoothDevice? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
    }
}

private fun BluetoothDevice.safeBondState(): Int {
    return runCatching { bondState }.getOrDefault(BluetoothDevice.BOND_NONE)
}

private val PUBLIC_PROFILE_IDS = intArrayOf(
    BluetoothProfile.A2DP,
    BluetoothProfile.HEADSET,
    BluetoothProfile.HID_DEVICE,
    BluetoothProfile.GATT,
    BluetoothProfile.GATT_SERVER,
)

private const val ACTION_BLUETOOTH_DEVICE_DETAIL_SETTINGS =
    "com.android.settings.BLUETOOTH_DEVICE_DETAIL_SETTINGS"
private const val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"
private const val KEY_BLUETOOTH_ADDRESS = "device_address"
private const val ANDROID_SETTINGS_PACKAGE = "com.android.settings"
