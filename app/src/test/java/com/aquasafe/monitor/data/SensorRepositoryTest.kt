package com.aquasafe.monitor.data

import com.aquasafe.monitor.model.SensorReading
import com.aquasafe.monitor.model.WaterStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SensorRepositoryTest {

    private val repository = SensorRepository()

    @Test
    fun `fetchReadings returns list when not configured`() = runTest {
        // When Supabase is not configured, should return empty list (not crash)
        val readings = repository.fetchReadings(10)
        assertNotNull(readings)
        assertTrue(readings is List<SensorReading>)
    }

    @Test
    fun `fetchLatest returns null or SensorReading`() = runTest {
        val latest = repository.fetchLatest()
        // When not configured, returns null
        // When configured, returns a reading
        if (latest != null) {
            assertTrue(latest is SensorReading)
            assertTrue("pH should be 0-14", latest.pH in 0.0..14.0)
            assertTrue("TDS should be >= 0", latest.tds >= 0.0)
            assertTrue("Turbidity should be >= 0", latest.turbidity >= 0.0)
            assertTrue("WQI should be 0-100", latest.wqiScore in 0.0..100.0)
        }
    }

    @Test
    fun `SensorReading waterStatus returns valid enum`() {
        val validStatuses = listOf(WaterStatus.SANGAT_LAYAK, WaterStatus.LAYAK, WaterStatus.BAHAYA)

        // Test each status label
        validStatuses.forEach { status ->
            val result = WaterStatus.fromLabel(status.label)
            assertEquals(status, result)
        }
    }

    @Test
    fun `WaterStatus fromLabel handles null and unknown gracefully`() {
        assertEquals(WaterStatus.LAYAK, WaterStatus.fromLabel(null))
        assertEquals(WaterStatus.LAYAK, WaterStatus.fromLabel("UNKNOWN_STATUS"))
        assertEquals(WaterStatus.LAYAK, WaterStatus.fromLabel(""))
    }

    @Test
    fun `WaterStatus fromLabel is case insensitive`() {
        assertEquals(WaterStatus.SANGAT_LAYAK, WaterStatus.fromLabel("sangat layak"))
        assertEquals(WaterStatus.BAHAYA, WaterStatus.fromLabel("bahaya"))
        assertEquals(WaterStatus.LAYAK, WaterStatus.fromLabel("LAYAK"))
    }
}
