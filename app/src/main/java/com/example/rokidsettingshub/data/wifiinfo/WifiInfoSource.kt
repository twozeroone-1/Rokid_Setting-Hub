package com.example.rokidsettingshub.data.wifiinfo

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import com.example.rokidsettingshub.model.WifiInfoSnapshot
import com.example.rokidsettingshub.model.WifiInfoState
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale

interface WifiInfoSource {
    fun load(): WifiInfoState
}

class AndroidWifiInfoSource(
    private val context: Context,
) : WifiInfoSource {
    private val appContext = context.applicationContext

    @Suppress("DEPRECATION")
    override fun load(): WifiInfoState {
        val packageManager = appContext.packageManager
        val wifiManager = appContext.getSystemService(WifiManager::class.java)
        val connectivityManager = appContext.getSystemService(ConnectivityManager::class.java)
        val activeNetwork = connectivityManager?.activeNetwork
        val linkProperties = activeNetwork?.let(connectivityManager::getLinkProperties)
        val networkCapabilities = activeNetwork?.let(connectivityManager::getNetworkCapabilities)
        val connectionInfo = wifiManager?.connectionInfo

        return WifiInfoState.fromSnapshot(
            WifiInfoSnapshot(
                hardwarePresent = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI),
                enabled = wifiManager?.isWifiEnabled,
                connected = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    ?: connectionInfo?.networkId?.let { it != INVALID_NETWORK_ID },
                ssid = connectionInfo?.ssid,
                ipAddress = formatIpv4Address(connectionInfo?.ipAddress) ?: findIpv4Address(linkProperties),
                interfaceName = linkProperties?.interfaceName ?: findWifiInterfaceName(),
            ),
        )
    }
}

object UnavailableWifiInfoSource : WifiInfoSource {
    override fun load(): WifiInfoState = WifiInfoState()
}

private fun formatIpv4Address(ipAddress: Int?): String? {
    if (ipAddress == null || ipAddress == 0) {
        return null
    }

    return String.format(
        Locale.US,
        "%d.%d.%d.%d",
        ipAddress and 0xff,
        ipAddress shr 8 and 0xff,
        ipAddress shr 16 and 0xff,
        ipAddress shr 24 and 0xff,
    )
}

private fun findIpv4Address(linkProperties: LinkProperties?): String? = linkProperties
    ?.linkAddresses
    ?.firstOrNull { it.address is Inet4Address && !it.address.isLoopbackAddress }
    ?.address
    ?.hostAddress

private fun findWifiInterfaceName(): String? = runCatching {
    Collections.list(NetworkInterface.getNetworkInterfaces())
        .firstOrNull { networkInterface ->
            val name = networkInterface.name ?: return@firstOrNull false
            name.startsWith("wlan") || name.startsWith("wifi")
        }
        ?.name
}.getOrNull()

private const val INVALID_NETWORK_ID = -1
