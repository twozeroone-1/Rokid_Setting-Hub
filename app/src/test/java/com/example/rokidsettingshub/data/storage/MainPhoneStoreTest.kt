package com.example.rokidsettingshub.data.storage

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MainPhoneStoreTest {

    @Test
    fun saveAndLoadPersistsMainPhoneSnapshot() {
        val sharedPreferences = FakeSharedPreferences()
        val writer = MainPhoneStore(sharedPreferences)
        val snapshot = StoredMainPhone(
            address = "AA:BB:CC:DD:EE:11",
            name = "Pixel 10",
        )

        writer.save(snapshot)

        val reader = MainPhoneStore(sharedPreferences)

        assertEquals(snapshot, reader.load())
    }

    @Test
    fun saveWritesSnapshotInSingleSharedPreferencesEdit() {
        val sharedPreferences = FakeSharedPreferences()
        val store = MainPhoneStore(sharedPreferences)

        store.save(
            StoredMainPhone(
                address = "AA:BB:CC:DD:EE:12",
                name = "Galaxy S30",
            ),
        )

        assertEquals(1, sharedPreferences.editCallCount)
        assertEquals(1, sharedPreferences.appliedTransactions.size)
        assertEquals(2, sharedPreferences.appliedTransactions.single().size)
    }

    @Test
    fun loadFallsBackToAddressWhenNameSnapshotIsMissing() {
        val sharedPreferences = FakeSharedPreferences()
        val store = MainPhoneStore(sharedPreferences)
        val snapshot = StoredMainPhone(
            address = "AA:BB:CC:DD:EE:13",
            name = "Pixel Fold",
        )

        store.save(snapshot)
        sharedPreferences.removeStoredValue(snapshot.name)

        assertEquals(
            StoredMainPhone(
                address = snapshot.address,
                name = snapshot.address,
            ),
            store.load(),
        )
    }

    private class FakeSharedPreferences : SharedPreferences {
        private val values = mutableMapOf<String, Any?>()
        var editCallCount = 0
            private set
        val appliedTransactions = mutableListOf<Map<String, Any?>>()

        override fun getAll(): MutableMap<String, *> = values.toMutableMap()

        override fun getString(key: String?, defValue: String?): String? {
            return values[key] as? String ?: defValue
        }

        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
            @Suppress("UNCHECKED_CAST")
            return (values[key] as? MutableSet<String>) ?: defValues
        }

        override fun getInt(key: String?, defValue: Int): Int = values[key] as? Int ?: defValue

        override fun getLong(key: String?, defValue: Long): Long = values[key] as? Long ?: defValue

        override fun getFloat(key: String?, defValue: Float): Float = values[key] as? Float ?: defValue

        override fun getBoolean(key: String?, defValue: Boolean): Boolean = values[key] as? Boolean ?: defValue

        override fun contains(key: String?): Boolean = values.containsKey(key)

        override fun edit(): SharedPreferences.Editor {
            editCallCount += 1
            return FakeEditor()
        }

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit

        fun removeStoredValue(value: String) {
            val key = values.entries.first { it.value == value }.key
            values.remove(key)
        }

        private inner class FakeEditor : SharedPreferences.Editor {
            private val pending = linkedMapOf<String, Any?>()
            private var clearRequested = false

            override fun putString(key: String?, value: String?): SharedPreferences.Editor = apply {
                pending[key.orEmpty()] = value
            }

            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = apply {
                pending[key.orEmpty()] = values
            }

            override fun putInt(key: String?, value: Int): SharedPreferences.Editor = apply {
                pending[key.orEmpty()] = value
            }

            override fun putLong(key: String?, value: Long): SharedPreferences.Editor = apply {
                pending[key.orEmpty()] = value
            }

            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = apply {
                pending[key.orEmpty()] = value
            }

            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = apply {
                pending[key.orEmpty()] = value
            }

            override fun remove(key: String?): SharedPreferences.Editor = apply {
                pending[key.orEmpty()] = REMOVED
            }

            override fun clear(): SharedPreferences.Editor = apply {
                clearRequested = true
            }

            override fun commit(): Boolean {
                apply()
                return true
            }

            override fun apply() {
                if (clearRequested) {
                    values.clear()
                }
                appliedTransactions += pending.toMap()
                pending.forEach { (key, value) ->
                    if (value === REMOVED) {
                        values.remove(key)
                    } else {
                        values[key] = value
                    }
                }
            }
        }

        private companion object {
            val REMOVED = Any()
        }
    }
}
