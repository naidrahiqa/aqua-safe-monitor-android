package com.aquasafe.monitor.data

import com.aquasafe.monitor.BuildConfig

/**
 * Konfigurasi Supabase.
 *
 * Nilai diambil dari gradle.properties:
 *   SUPABASE_URL=https://PROJECT_ID.supabase.co
 *   SUPABASE_ANON_KEY=eyJ...
 *
 * Atau override via local.properties untuk development.
 */
object SupabaseConfig {
    val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    val SUPABASE_ANON_KEY: String = BuildConfig.SUPABASE_ANON_KEY

    val isConfigured: Boolean
        get() = !SUPABASE_URL.contains("YOUR_") &&
                !SUPABASE_ANON_KEY.contains("YOUR_") &&
                SUPABASE_ANON_KEY.startsWith("eyJ")
}
