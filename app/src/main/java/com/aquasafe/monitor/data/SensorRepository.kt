package com.aquasafe.monitor.data

import com.aquasafe.monitor.model.SensorReading
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Repository sensor — ambil data dari Supabase REST API.
 * (konsep: ESP32 → Supabase → app ini)
 */
class SensorRepository(
    private val client: HttpClient = defaultClient,
    private val config: SupabaseConfig = SupabaseConfig,
) {

    suspend fun fetchReadings(limit: Int = 100, sinceHours: Int? = null): List<SensorReading> {
        if (!config.isConfigured) return emptyList()
        val response = client.get("${config.SUPABASE_URL}/rest/v1/sensor_data") {
            parameter("select", "*")
            parameter("order", "created_at.desc")
            parameter("limit", limit)
            if (sinceHours != null) {
                val since = Instant.now().minusSeconds(sinceHours * 3600L)
                    .atOffset(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_INSTANT)
                parameter("created_at", "gte.$since")
            }
            header("apikey", config.SUPABASE_ANON_KEY)
            header(HttpHeaders.Authorization, "Bearer ${config.SUPABASE_ANON_KEY}")
        }
        return response.body<List<SensorReading>>()
    }

    suspend fun fetchLatest(): SensorReading? = fetchReadings(1).firstOrNull()

    companion object {
        val defaultClient: HttpClient by lazy {
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 15_000
                    connectTimeoutMillis = 10_000
                }
            }
        }
    }
}
