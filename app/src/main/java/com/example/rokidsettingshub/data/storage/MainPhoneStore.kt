package com.example.rokidsettingshub.data.storage

import android.content.SharedPreferences

data class StoredMainPhone(
    val address: String,
    val name: String,
)

class MainPhoneStore(
    private val sharedPreferences: SharedPreferences,
) {
    fun save(mainPhone: StoredMainPhone) {
        sharedPreferences.edit()
            .putString(KEY_ADDRESS, mainPhone.address)
            .putString(KEY_NAME, mainPhone.name)
            .apply()
    }

    fun load(): StoredMainPhone? {
        val address = sharedPreferences.getString(KEY_ADDRESS, null) ?: return null
        val name = sharedPreferences.getString(KEY_NAME, null)
            .takeUnless { it.isNullOrBlank() }
            ?: address
        return StoredMainPhone(
            address = address,
            name = name,
        )
    }

    companion object {
        private const val KEY_ADDRESS = "main_phone_address"
        private const val KEY_NAME = "main_phone_name"
    }
}
