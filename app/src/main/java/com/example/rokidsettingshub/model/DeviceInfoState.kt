package com.example.rokidsettingshub.model

data class DeviceInfoSnapshot(
    val modelName: String?,
    val androidVersion: String?,
    val totalStorageBytes: Long?,
    val freeStorageBytes: Long?,
)

data class DeviceInfoState(
    val modelName: String = UNAVAILABLE_TEXT,
    val androidVersion: String = UNAVAILABLE_TEXT,
    val totalStorage: String = UNAVAILABLE_TEXT,
    val usedStorage: String = UNAVAILABLE_TEXT,
    val freeStorage: String = UNAVAILABLE_TEXT,
) {
    companion object {
        const val UNAVAILABLE_TEXT = "Unavailable"

        fun fromSnapshot(snapshot: DeviceInfoSnapshot): DeviceInfoState {
            val totalBytes = snapshot.totalStorageBytes
            val freeBytes = snapshot.freeStorageBytes
            val usedBytes = if (
                totalBytes != null &&
                freeBytes != null &&
                totalBytes >= freeBytes
            ) {
                totalBytes - freeBytes
            } else {
                null
            }

            return DeviceInfoState(
                modelName = snapshot.modelName.orUnavailable(),
                androidVersion = snapshot.androidVersion
                    ?.takeUnless { it.isBlank() }
                    ?.let { "Android $it" }
                    ?: UNAVAILABLE_TEXT,
                totalStorage = formatStorageSize(totalBytes),
                usedStorage = formatStorageSize(usedBytes),
                freeStorage = formatStorageSize(freeBytes),
            )
        }
    }
}

private fun String?.orUnavailable(): String = this?.takeUnless { it.isBlank() } ?: DeviceInfoState.UNAVAILABLE_TEXT

internal fun formatStorageSize(bytes: Long?): String {
    if (bytes == null || bytes < 0L) {
        return DeviceInfoState.UNAVAILABLE_TEXT
    }
    if (bytes < 1024L) {
        return "$bytes B"
    }

    val units = arrayOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var unitIndex = -1
    while (value >= 1024.0 && unitIndex < units.lastIndex) {
        value /= 1024.0
        unitIndex += 1
    }
    return String.format("%.1f %s", value, units[unitIndex])
}
