package com.aquasafe.monitor.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aquasafe.monitor.model.TestLocation
import com.aquasafe.monitor.model.defaultTestLocations
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString

private val Context.dataStore by preferencesDataStore(name = "watersafe")

/**
 * Penyimpanan lokasi pengujian — DataStore (pengganti localStorage di web).
 * Lokasi pengujian disimpan di device, sama seperti versi web.
 */
class TestLocationStore(private val context: Context) {

    private val key = stringPreferencesKey("test_locations")

    suspend fun load(): List<TestLocation> {
        val prefs = context.dataStore.data.first()
        val raw = prefs[key] ?: return defaultTestLocations()
        return runCatching { SensorRepository.sharedJson.decodeFromString<List<TestLocation>>(raw) }
            .getOrElse { defaultTestLocations() }
    }

    suspend fun save(locations: List<TestLocation>) {
        context.dataStore.edit { prefs ->
            prefs[key] = SensorRepository.sharedJson.encodeToString(locations)
        }
    }
}
