package com.aquasafe.monitor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aquasafe.monitor.data.SensorRepository
import com.aquasafe.monitor.data.TestLocationStore
import com.aquasafe.monitor.model.SensorReading
import com.aquasafe.monitor.model.TestLocation
import com.aquasafe.monitor.model.computeWQI
import com.aquasafe.monitor.model.wqiStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

data class DashboardUiState(
    val readings: List<SensorReading> = emptyList(),
    val latestReading: SensorReading? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val lastUpdatedAt: Long = 0L,
    val locations: List<TestLocation> = emptyList(),
    val syncingLocationId: String? = null,
    val timeRangeHours: Int? = null, // null = semua, 1/6/24 = filter jam
)

/**
 * Satu ViewModel untuk seluruh app:
 * - polling data sensor dari Supabase tiap 10 detik
 * - kelola lokasi pengujian (tambah/hapus/sinkron dari sensor)
 * - lokasi tersimpan di DataStore (pengganti localStorage web)
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SensorRepository()
    private val store = TestLocationStore(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var pollJob: Job? = null
    private var consecutiveErrors = 0

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(locations = store.load()) }
            refresh()
            startPolling()
        }
    }

    private fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (true) {
                val delayMs = if (consecutiveErrors > 0) {
                    // Exponential backoff: 10s, 20s, 40s, max 60s
                    (POLL_INTERVAL_MS * (1 shl (consecutiveErrors - 1).coerceAtMost(2))).coerceAtMost(60_000L)
                } else {
                    POLL_INTERVAL_MS
                }
                delay(delayMs)
                refresh(showLoading = false)
            }
        }
    }

    fun setTimeRange(hours: Int?) {
        _uiState.update { it.copy(timeRangeHours = hours) }
        refreshWithRange(hours)
    }

    fun refresh(showLoading: Boolean = true) {
        refreshWithRange(_uiState.value.timeRangeHours, showLoading)
    }

    private fun refreshWithRange(sinceHours: Int? = _uiState.value.timeRangeHours, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _uiState.update { it.copy(loading = true, error = null) }
            try {
                val readings = repository.fetchReadings(200, sinceHours = sinceHours)
                consecutiveErrors = 0
                _uiState.update {
                    it.copy(
                        readings = readings,
                        latestReading = readings.firstOrNull(),
                        loading = false,
                        error = null,
                        lastUpdatedAt = System.currentTimeMillis(),
                    )
                }
            } catch (e: Exception) {
                consecutiveErrors++
                _uiState.update {
                    it.copy(loading = false, error = e.message ?: "Gagal memuat data sensor")
                }
            }
        }
    }

    fun addLocation(
        name: String,
        lat: Double,
        lng: Double,
        temperature: Double,
        ph: Double,
        tds: Double,
        turbidity: Double,
        notes: String,
    ) {
        val wqi = computeWQI(ph, tds, turbidity, temperature)
        val location = TestLocation(
            id = UUID.randomUUID().toString(),
            name = name,
            lat = lat,
            lng = lng,
            temperature = temperature,
            ph = ph,
            tds = tds,
            turbidity = turbidity,
            wqiScore = wqi,
            status = wqiStatus(wqi).label,
            notes = notes,
            createdAt = Instant.now().toString(),
        )
        _uiState.update { it.copy(locations = it.locations + location) }
        persistLocations()
    }

    fun removeLocation(id: String) {
        _uiState.update { it.copy(locations = it.locations.filterNot { it.id == id }) }
        persistLocations()
    }

    /** Sinkron pembacaan sensor terbaru ke sebuah lokasi pengujian */
    fun syncLocation(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(syncingLocationId = id) }
            try {
                val latest = repository.fetchLatest()
                if (latest != null) {
                    val updated = _uiState.value.locations.map { location ->
                        if (location.id == id) {
                            location.copy(
                                temperature = latest.temperature,
                                ph = latest.ph,
                                tds = latest.tds,
                                turbidity = latest.turbidity,
                                wqiScore = latest.wqiScore,
                                status = latest.status,
                                syncedAt = Instant.now().toString(),
                            )
                        } else location
                    }
                    _uiState.update { it.copy(locations = updated) }
                    persistLocations()
                } else {
                    _uiState.update {
                        it.copy(error = "Sensor belum mengirim data. Pastikan ESP32 aktif.")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Sinkronisasi gagal") }
            }
            _uiState.update { it.copy(syncingLocationId = null) }
        }
    }

    private fun persistLocations() {
        val current = _uiState.value.locations
        viewModelScope.launch { store.save(current) }
    }

    override fun onCleared() {
        super.onCleared()
        pollJob?.cancel()
    }

    companion object {
        private const val POLL_INTERVAL_MS = 10_000L
    }
}