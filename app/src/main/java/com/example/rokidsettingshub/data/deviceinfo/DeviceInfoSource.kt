package com.example.rokidsettingshub.data.deviceinfo

import android.os.Build
import android.os.StatFs
import com.example.rokidsettingshub.model.DeviceInfoSnapshot
import com.example.rokidsettingshub.model.DeviceInfoState
import java.io.File

interface DeviceInfoSource {
    fun load(): DeviceInfoState
}

class AndroidDeviceInfoSource(
    private val dataDirectory: File,
) : DeviceInfoSource {
    override fun load(): DeviceInfoState {
        val storageSnapshot = runCatching {
            val statFs = StatFs(dataDirectory.absolutePath)
            statFs.totalBytes to statFs.availableBytes
        }.getOrNull()

        return DeviceInfoState.fromSnapshot(
            DeviceInfoSnapshot(
                modelName = Build.MODEL,
                androidVersion = Build.VERSION.RELEASE,
                totalStorageBytes = storageSnapshot?.first,
                freeStorageBytes = storageSnapshot?.second,
            ),
        )
    }
}

object UnavailableDeviceInfoSource : DeviceInfoSource {
    override fun load(): DeviceInfoState = DeviceInfoState()
}
