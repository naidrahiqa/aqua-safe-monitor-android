package com.aquasafe.monitor.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class FormatsTest {

    @Test
    fun `fmtAgo returns baru saja for recent timestamps`() {
        val now = Instant.now().toEpochMilli()
        val result = fmtAgo(now)
        assertEquals("baru saja", result)
    }

    @Test
    fun `fmtAgo returns minutes for timestamps within an hour`() {
        val fiveMinAgo = Instant.now().minus(5, ChronoUnit.MINUTES).toEpochMilli()
        val result = fmtAgo(fiveMinAgo)
        assertEquals("5 mnt lalu", result)
    }

    @Test
    fun `fmtAgo returns hours for timestamps over an hour ago`() {
        val threeHoursAgo = Instant.now().minus(3, ChronoUnit.HOURS).toEpochMilli()
        val result = fmtAgo(threeHoursAgo)
        assertEquals("3 jam lalu", result)
    }

    @Test
    fun `fmtDateTime returns formatted date string`() {
        // 13 Aug 2026, 14:30 WIB (approx)
        val millis = Instant.parse("2026-08-13T07:30:00Z").toEpochMilli()
        val result = fmtDateTime(millis)
        assertTrue(result.contains("2026"))
        assertTrue(result.contains("14:30"))
    }

    @Test
    fun `fmtDateTimeIso returns formatted date from ISO string`() {
        val result = fmtDateTimeIso("2026-08-13T07:30:00Z")
        assertTrue(result.contains("2026"))
    }

    @Test
    fun `fmtDateTimeIso returns raw string on invalid input`() {
        val result = fmtDateTimeIso("not-a-date")
        assertEquals("not-a-date", result)
    }

    @Test
    fun `fmtAgoIso returns null for null input`() {
        val result = fmtAgoIso(null)
        assertNull(result)
    }

    @Test
    fun `fmtAgoIso returns formatted string for valid ISO`() {
        val recent = Instant.now().minus(10, ChronoUnit.MINUTES).toString()
        val result = fmtAgoIso(recent)
        assertEquals("10 mnt lalu", result)
    }
}
