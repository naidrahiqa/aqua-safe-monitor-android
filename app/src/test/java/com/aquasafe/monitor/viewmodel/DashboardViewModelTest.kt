package com.aquasafe.monitor.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardViewModelTest {

    @Test
    fun `DashboardUiState has correct defaults`() {
        val state = DashboardUiState()
        assertTrue(state.readings.isEmpty())
        assertNull(state.latestReading)
        assertFalse(state.loading)
        assertNull(state.error)
        assertEquals(0L, state.lastUpdatedAt)
        assertTrue(state.locations.isEmpty())
        assertNull(state.syncingLocationId)
        assertNull(state.timeRangeHours)
    }

    @Test
    fun `DashboardUiState copy preserves unchanged fields`() {
        val state = DashboardUiState(
            loading = true,
            error = "test error",
            timeRangeHours = 6,
        )
        val updated = state.copy(loading = false)
        assertFalse(updated.loading)
        assertEquals("test error", updated.error)
        assertEquals(6, updated.timeRangeHours)
    }

    @Test
    fun `DashboardUiState timeRangeHours can be null or integer`() {
        val nullRange = DashboardUiState(timeRangeHours = null)
        val oneHour = DashboardUiState(timeRangeHours = 1)
        val sixHours = DashboardUiState(timeRangeHours = 6)
        val twentyFourHours = DashboardUiState(timeRangeHours = 24)

        assertNull(nullRange.timeRangeHours)
        assertEquals(1, oneHour.timeRangeHours)
        assertEquals(6, sixHours.timeRangeHours)
        assertEquals(24, twentyFourHours.timeRangeHours)
    }

    @Test
    fun `DashboardUiState syncingLocationId tracks sync state`() {
        val state = DashboardUiState(syncingLocationId = "loc-123")
        assertEquals("loc-123", state.syncingLocationId)

        val cleared = state.copy(syncingLocationId = null)
        assertNull(cleared.syncingLocationId)
    }
}
