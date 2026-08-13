package com.aquasafe.monitor.data

/**
 * Konfigurasi Supabase.
 *
 * Ganti nilainya dengan project kamu:
 *   1. https://supabase.com/dashboard → Project → Settings → API
 *   2. Copy "Project URL" → SUPABASE_URL
 *   3. Copy "anon public" key → SUPABASE_ANON_KEY
 */
object SupabaseConfig {
    const val SUPABASE_URL = "https://dohhcabunjojfdqcgicw.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRvaGhjYWJ1bmpvamZkcWNnaWN3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODYyNjgzOTAsImV4cCI6MjEwMTg0NDM5MH0.Vp5lpoUhEa55KOXoU--viEcVNCojWy-yub8VLZ6Bdlk"

    val isConfigured: Boolean
        get() = !SUPABASE_URL.contains("YOUR_") &&
                !SUPABASE_ANON_KEY.contains("YOUR_") &&
                SUPABASE_ANON_KEY.startsWith("eyJ")
}
