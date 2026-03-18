package com.example.rokidsettingshub.data.storage

import com.example.rokidsettingshub.model.DeviceConnectionState
import com.example.rokidsettingshub.model.DeviceType
import com.example.rokidsettingshub.model.ManagedDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MainPhoneStoreTest {

    @Test
    fun saveAndLoadPersistsMainPhoneSnapshot() {
        val backing = mutableMapOf<String, String>()
        val writer = MainPhoneStore(FakeKeyValueStorage(backing))
        val mainPhone = ManagedDevice(
            address = "AA:BB:CC:DD:EE:11",
            name = "Pixel 10",
            deviceType = DeviceType.Phone,
            connectionState = DeviceConnectionState.Connected,
            isMainPhone = true,
        )

        writer.save(mainPhone)

        val reader = MainPhoneStore(FakeKeyValueStorage(backing))

        assertEquals(
            StoredMainPhone(
                address = mainPhone.address,
                name = mainPhone.name,
            ),
            reader.load(),
        )
    }

    @Test
    fun clearRemovesPersistedMainPhone() {
        val backing = mutableMapOf<String, String>()
        val store = MainPhoneStore(FakeKeyValueStorage(backing))
        val mainPhone = ManagedDevice(
            address = "AA:BB:CC:DD:EE:12",
            name = "Galaxy S30",
            deviceType = DeviceType.Phone,
            connectionState = DeviceConnectionState.Paired,
            isMainPhone = true,
        )

        store.save(mainPhone)
        store.clear()

        assertNull(store.load())
    }

    private class FakeKeyValueStorage(
        private val values: MutableMap<String, String>,
    ) : MainPhoneStore.KeyValueStorage {
        override fun getString(key: String): String? = values[key]

        override fun putString(key: String, value: String) {
            values[key] = value
        }

        override fun remove(key: String) {
            values.remove(key)
        }
    }
}
