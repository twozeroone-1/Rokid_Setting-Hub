package com.example.rokidsettingshub.debug

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import android.util.Log
import java.util.Locale

class DebugBluetoothCommandReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PAIR_BLUETOOTH_DEVICE -> {
                val command = PairDeviceCommand { address ->
                    pairDevice(context = context, address = address)
                }
                val paired = command.execute(intent.getStringExtra(EXTRA_ADDRESS))
                Log.i(TAG, "pair request handled=$paired")
            }

            ACTION_SCAN_AND_PAIR_BLUETOOTH_DEVICE -> {
                val pendingResult = goAsync()
                val command = ScanAndPairDeviceCommand { query ->
                    scanAndPairDevice(
                        context = context,
                        query = query,
                        pendingResult = pendingResult,
                    )
                }
                val started = command.execute(intent.getStringExtra(EXTRA_QUERY))
                if (!started) {
                    Log.i(TAG, "scan-and-pair request handled=false")
                    pendingResult.finish()
                }
            }
        }
    }

    private fun pairDevice(context: Context, address: String): Boolean {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter ?: return false

        return runCatching {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }
            bluetoothAdapter.getRemoteDevice(address).createBond()
        }.getOrDefault(false)
    }

    private fun scanAndPairDevice(
        context: Context,
        query: String,
        pendingResult: PendingResult,
    ) {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.i(TAG, "scan-and-pair skipped because adapter unavailable")
            pendingResult.finish()
            return
        }

        val matcher = BluetoothDebugMatcher(query)
        val applicationContext = context.applicationContext
        val mainHandler = Handler(Looper.getMainLooper())
        val discoveredDevices = linkedMapOf<String, String>()
        var finished = false
        lateinit var receiver: BroadcastReceiver

        fun finish(reason: String) {
            if (finished) {
                return
            }
            finished = true
            runCatching { applicationContext.unregisterReceiver(receiver) }
            runCatching {
                if (bluetoothAdapter.isDiscovering) {
                    bluetoothAdapter.cancelDiscovery()
                }
            }
            mainHandler.removeCallbacksAndMessages(null)
            val summary = discoveredDevices.entries.joinToString { "${it.value}(${it.key})" }
            Log.i(TAG, "scan-and-pair finished reason=$reason discovered=[$summary]")
            pendingResult.finish()
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device = intent.readBluetoothDevice() ?: return
                        val name = device.name?.takeIf { it.isNotBlank() } ?: "Unknown"
                        discoveredDevices[device.address] = name
                        Log.i(TAG, "scan found name=$name address=${device.address}")
                        if (matcher.matches(name = name, address = device.address)) {
                            val paired = runCatching {
                                if (bluetoothAdapter.isDiscovering) {
                                    bluetoothAdapter.cancelDiscovery()
                                }
                                device.createBond()
                            }.getOrDefault(false)
                            Log.i(
                                TAG,
                                "scan-and-pair matched query=$query name=$name address=${device.address} pairStarted=$paired",
                            )
                            finish(reason = if (paired) "pair-started" else "pair-failed")
                        }
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        finish(reason = "discovery-finished-no-match")
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        ContextCompat.registerReceiver(
            applicationContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        runCatching {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }
        }
        val started = runCatching { bluetoothAdapter.startDiscovery() }.getOrDefault(false)
        if (!started) {
            finish(reason = "discovery-start-failed")
            return
        }

        mainHandler.postDelayed(
            {
                finish(reason = "timeout")
            },
            SCAN_TIMEOUT_MS,
        )
        Log.i(TAG, "scan-and-pair started query=$query")
    }

    companion object {
        const val ACTION_PAIR_BLUETOOTH_DEVICE =
            "com.example.rokidsettingshub.action.PAIR_BLUETOOTH_DEVICE"
        const val ACTION_SCAN_AND_PAIR_BLUETOOTH_DEVICE =
            "com.example.rokidsettingshub.action.SCAN_AND_PAIR_BLUETOOTH_DEVICE"
        const val EXTRA_ADDRESS = "address"
        const val EXTRA_QUERY = "query"

        private const val TAG = "DebugBtCommand"
        private const val SCAN_TIMEOUT_MS = 8_000L
    }
}

internal class PairDeviceCommand(
    private val pairByAddress: (String) -> Boolean,
) {
    fun execute(address: String?): Boolean {
        val normalizedAddress = address?.trim()?.takeIf { it.isNotEmpty() } ?: return false
        return pairByAddress(normalizedAddress)
    }
}

internal class ScanAndPairDeviceCommand(
    private val scanAndPairByQuery: (String) -> Unit,
) {
    fun execute(query: String?): Boolean {
        val normalizedQuery = query?.trim()?.takeIf { it.isNotEmpty() } ?: return false
        scanAndPairByQuery(normalizedQuery)
        return true
    }
}

internal class BluetoothDebugMatcher(
    query: String,
) {
    private val normalizedQuery = query.trim().lowercase(Locale.ROOT)

    fun matches(name: String, address: String): Boolean {
        return name.lowercase(Locale.ROOT).contains(normalizedQuery) ||
            address.lowercase(Locale.ROOT).contains(normalizedQuery)
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
