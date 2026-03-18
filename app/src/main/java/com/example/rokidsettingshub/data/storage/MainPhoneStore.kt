package com.example.rokidsettingshub.data.storage

import android.content.SharedPreferences
import com.example.rokidsettingshub.model.ManagedDevice

data class StoredMainPhone(
    val address: String,
    val name: String,
)

class MainPhoneStore(
    private val storage: KeyValueStorage,
) {
    fun save(device: ManagedDevice) {
        storage.putString(KEY_ADDRESS, device.address)
        storage.putString(KEY_NAME, device.name)
    }

    fun load(): StoredMainPhone? {
        val address = storage.getString(KEY_ADDRESS) ?: return null
        val name = storage.getString(KEY_NAME) ?: return null
        return StoredMainPhone(
            address = address,
            name = name,
        )
    }

    fun clear() {
        storage.remove(KEY_ADDRESS)
        storage.remove(KEY_NAME)
    }

    interface KeyValueStorage {
        fun getString(key: String): String?
        fun putString(key: String, value: String)
        fun remove(key: String)
    }

    companion object {
        fun fromSharedPreferences(sharedPreferences: SharedPreferences): MainPhoneStore {
            return MainPhoneStore(SharedPreferencesKeyValueStorage(sharedPreferences))
        }

        private const val KEY_ADDRESS = "main_phone_address"
        private const val KEY_NAME = "main_phone_name"
    }

    private class SharedPreferencesKeyValueStorage(
        private val sharedPreferences: SharedPreferences,
    ) : KeyValueStorage {
        override fun getString(key: String): String? = sharedPreferences.getString(key, null)

        override fun putString(key: String, value: String) {
            sharedPreferences.edit().putString(key, value).apply()
        }

        override fun remove(key: String) {
            sharedPreferences.edit().remove(key).apply()
        }
    }
}
