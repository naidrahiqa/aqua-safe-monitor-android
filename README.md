# AquaSafe Monitor — Android App

Aplikasi Android untuk monitoring kualitas air real-time dari Supabase.

**Related repos:**
- [aqua-safe-monitor](https://github.com/naidrahiqa/aqua-safe-monitor) — Web dashboard (React + Vite + Tailwind + Leaflet)

## Tech Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Gradle 8.9** + **AGP 8.7.3** + **JDK 21**
- **Ktor Client** (OkHttp engine) — REST API ke Supabase
- **Kotlinx Serialization** — JSON parsing
- **osmdroid** — peta OpenStreetMap (gratis, tanpa API key)
- **DataStore Preferences** — local storage lokasi

## Struktur

```
app/src/main/java/com/aquasafe/monitor/
├── MainActivity.kt
├── data/
│   ├── SupabaseConfig.kt          # URL + anon key Supabase
│   ├── SensorRepository.kt        # Fetch data dari REST API
│   └── TestLocationStore.kt       # Simpan lokasi di DataStore
├── model/
│   ├── SensorReading.kt           # Data class + enum + SensorConfig
│   └── TestLocation.kt            # Model lokasi pengujian
├── viewmodel/
│   └── DashboardViewModel.kt      # Polling data + state management
├── ui/
│   ├── navigation/AppNav.kt       # Bottom nav pill bar + NavHost + transisi layar
│   ├── screens/
│   │   ├── OverviewScreen.kt      # Beranda: WQI + 4 gauge + lokasi
│   │   ├── SensorScreen.kt        # Detail sensor per tab
│   │   ├── MapScreen.kt           # Peta OSM (osmdroid) + pin berwarna
│   │   ├── HistoryScreen.kt       # Riwayat pembacaan
│   │   └── SettingsScreen.kt      # Pengaturan
│   ├── components/
│   │   ├── Panel.kt               # Design system: PanelCard, SectionHeader, StatusPill
│   │   ├── GaugeCard.kt           # Gauge semi-lingkaran animasi + WqiHeroCard
│   │   ├── SensorChart.kt         # Line chart riwayat (gradient area)
│   │   └── StatusChip.kt          # Chip status (SANGAT LAYAK/LAYAK/BAHAYA)
│   ├── theme/
│   │   ├── Color.kt               # Semantic colors (surfaces, status, sensors)
│   │   ├── Type.kt                # WaterSafeTypography + Data* text styles
│   │   └── Theme.kt               # Dark theme + shape tokens
│   └── util/Formats.kt            # Format timestamp
app/src/main/res/font/
    ├── jetbrainsmono_regular.ttf  # JetBrains Mono Regular
    ├── jetbrainsmono_medium.ttf   # JetBrains Mono Medium
    └── jetbrainsmono_bold.ttf     # JetBrains Mono Bold
```

## Setup

### 1. JDK 21
```bash
# Cek sudah terpasang
java -version  # harus openjdk 21.x

# Belum ada? Install via winget
winget install EclipseAdoptium.Temurin.21.JDK
```

### 2. Gradle Config (global)
Buat/edit `~/.gradle/gradle.properties`:
```
org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.12.8-hotspot
```

### 3. Konfigurasi Supabase
Edit `gradle.properties`:
```properties
SUPABASE_URL=https://PROJECT_ID.supabase.co
SUPABASE_ANON_KEY=eyJ...
```

Atau buat `local.properties` (tidak di-commit):
```properties
SUPABASE_URL=https://PROJECT_ID.supabase.co
SUPABASE_ANON_KEY=eyJ...
```

### 4. Build & Install
```bash
# Build
gradlew.bat :app:assembleDebug

# Install ke device (ADB)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Design System

### Colors (`ui/theme/Color.kt`)
| Token | Hex | Usage |
|-------|-----|-------|
| `SurfaceDark` | `#040810` | Background utama |
| `SurfaceDeep` | `#060D1B` | Gradient bawah |
| `Panel` | `#0B1526` | Kartu panel |
| `PanelLight` | `#13203A` | Kartu hover |
| `AccentCyan` | `#22D3EE` | Aksen utama |
| `Success` | `#22C55E` | Status aman |
| `Warning` | `#F59E0B` | Status peringatan |
| `Danger` | `#EF4444` | Status bahaya |

### Sensor Colors (matches web dashboard)
| Sensor | Color |
|--------|-------|
| pH | `#F59E0B` (amber) |
| Suhu | `#F97316` (orange) |
| TDS | `#22C55E` (green) |
| Turbidity | `#8B5CF6` (purple) |
| WQI | `#22D3EE` (cyan) |

### Typography (`ui/theme/Type.kt`)
- **Inter** — UI text (labels, headings)
- **JetBrains Mono** — Data/numbers (bundled in `res/font/`)

Text styles for data display:
- `DataMedium` — 14sp, used in chart stats
- `DataLarge` — 22sp, used in gauge values
- `DataXLarge` — 32sp, used in main gauge values
- `DataHero` — 44sp, used in WQI hero card

### Components
- **PanelCard** — kartu gelap + border halus, uses theme colors
- **SectionHeader** — judul + subtitle abu-abu konsisten
- **StatusPill** — status online/live dengan dot berdenyut
- **GaugeCard** — gauge semi-lingkaran animasi + angka count-up
- **SensorChart** — line chart riwayat dengan area gradien

### Animasi
- Ring WQI & gauge: sweep + angka **count-up** (`FastOutSlowInEasing`)
- Kartu masuk layar dengan **spring** `DampingRatioMediumBouncy`
- Bottom nav: ikon aktif membesar + glow
- Pindah layar: fade + scale (`tween` 240ms)

## Fitur

### Dashboard (Beranda)
- **WQI Hero Card** — ring animasi, skor 0-100 dengan warna, angka count-up
- **4 Gauge Card** — pH, Suhu, TDS, Turbidity dengan indikator aman/tidak
- **Filter Waktu** — chip "Semua | 1 Jam | 6 Jam | 24 Jam"
- **Status Online** — dot hijau berdenyut + timestamp terakhir update

### Sensor Detail
- Tab per sensor (pH, Suhu, TDS, Turbidity)
- Gauge + chart riwayat (area gradien di bawah garis) + status chip
- Filter waktu yang sama

### Peta Lokasi (osmdroid / OpenStreetMap)
- **Gratis 100%** — tanpa API key, tanpa kartu kredit
- Pin berwarna sesuai status kualitas air
- Ketuk peta → tandai titik → tombol + untuk simpan
- Tambah/hapus lokasi + sinkron data sensor ke lokasi

## Koneksi ke Supabase

App pakai **anon key** (public, tanpa login) dengan RLS policy:
```sql
-- Public can read (untuk app Android anon)
CREATE POLICY "Public can read sensor data"
    ON public.sensor_data FOR SELECT USING (true);

CREATE POLICY "Public can read devices"
    ON public.devices FOR SELECT USING (true);
```

**Penting**: Policy ini untuk mode demo. Untuk produksi, wajibkan login.

## Troubleshooting

### Build gagal: "Unsupported class file major version 69"
→ JDK terlalu baru. Pastikan `org.gradle.java.home` di `~/.gradle/gradle.properties`指向 JDK 21.

### App kosong: "Menunggu data sensor..."
1. Cek `SupabaseConfig.kt` sudah diisi URL + key
2. Cek tabel `sensor_data` di Supabase dashboard ada data
3. Cek RLS policy sudah diapply (lihat SQL di atas)

### Map kosong (osmdroid)
→ Butuh koneksi internet (tile OSM di-download saat buka). Kalau offline, coba lagi nanti — tile tidak disimpan cache lama.

### Gauge tidak muncul indikator aman/tidak
→ Pastikan value tidak null. Kalau null, tampil "Belum ada data" (abu-abu).
