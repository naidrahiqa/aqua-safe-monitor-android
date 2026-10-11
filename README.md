# AquaSafe Monitor — Android App

Android app for real-time water quality monitoring from Supabase.

**Related repos:**
- [aqua-safe-monitor](https://github.com/naidrahiqa/aqua-safe-monitor) — Web dashboard (React + Vite + Tailwind + Leaflet)

## Tech Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Gradle 8.9** + **AGP 8.7.3** + **JDK 21**
- **Ktor Client** (OkHttp engine) — REST API to Supabase
- **Kotlinx Serialization** — JSON parsing
- **osmdroid** — OpenStreetMap map (free, no API key)
- **DataStore Preferences** — local storage for locations

## Structure

```
app/src/main/java/com/aquasafe/monitor/
├── MainActivity.kt
├── data/
│   ├── SupabaseConfig.kt          # Supabase URL + anon key
│   ├── SensorRepository.kt        # Fetch data from the REST API
│   └── TestLocationStore.kt       # Store locations in DataStore
├── model/
│   ├── SensorReading.kt           # Data class + enum + SensorConfig
│   └── TestLocation.kt            # Test location model
├── viewmodel/
│   └── DashboardViewModel.kt      # Data polling + state management
├── ui/
│   ├── navigation/AppNav.kt       # Bottom nav pill bar + NavHost + screen transitions
│   ├── screens/
│   │   ├── OverviewScreen.kt      # Home: WQI + 4 gauges + locations
│   │   ├── SensorScreen.kt        # Sensor detail per tab
│   │   ├── MapScreen.kt           # OSM map (osmdroid) + colored pins
│   │   ├── HistoryScreen.kt       # Reading history
│   │   └── SettingsScreen.kt      # Settings
│   ├── components/
│   │   ├── Panel.kt               # Design system: PanelCard, SectionHeader, StatusPill
│   │   ├── GaugeCard.kt           # Animated semi-circular gauge + WqiHeroCard
│   │   ├── SensorChart.kt         # History line chart (gradient area)
│   │   └── StatusChip.kt          # Status chip (VERY SUITABLE/SUITABLE/DANGER)
│   ├── theme/
│   │   ├── Color.kt               # Semantic colors (surfaces, status, sensors)
│   │   ├── Type.kt                # WaterSafeTypography + Data* text styles
│   │   └── Theme.kt               # Dark theme + shape tokens
│   └── util/Formats.kt            # Timestamp formatting
app/src/main/res/font/
    ├── jetbrainsmono_regular.ttf  # JetBrains Mono Regular
    ├── jetbrainsmono_medium.ttf   # JetBrains Mono Medium
    └── jetbrainsmono_bold.ttf     # JetBrains Mono Bold
```

## Setup

### 1. JDK 21
```bash
# Check that it's installed
java -version  # must be OpenJDK 21.x

# Not installed yet? Install via winget
winget install EclipseAdoptium.Temurin.21.JDK
```

### 2. Gradle config (global)
Create/edit `~/.gradle/gradle.properties`:
```
org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.12.8-hotspot
```

### 3. Supabase configuration
Edit `gradle.properties`:
```properties
SUPABASE_URL=https://PROJECT_ID.supabase.co
SUPABASE_ANON_KEY=eyJ...
```

Or create `local.properties` (not committed):
```properties
SUPABASE_URL=https://PROJECT_ID.supabase.co
SUPABASE_ANON_KEY=eyJ...
```

### 4. Build & install
```bash
# Build
gradlew.bat :app:assembleDebug

# Install to device (ADB)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Design System

### Colors (`ui/theme/Color.kt`)
| Token | Hex | Usage |
|-------|-----|-------|
| `SurfaceDark` | `#040810` | Main background |
| `SurfaceDeep` | `#060D1B` | Bottom gradient |
| `Panel` | `#0B1526` | Panel cards |
| `PanelLight` | `#13203A` | Hover cards |
| `AccentCyan` | `#22D3EE` | Primary accent |
| `Success` | `#22C55E` | Safe status |
| `Warning` | `#F59E0B` | Warning status |
| `Danger` | `#EF4444` | Danger status |

### Sensor Colors (matches web dashboard)
| Sensor | Color |
|--------|-------|
| pH | `#F59E0B` (amber) |
| Temperature | `#F97316` (orange) |
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
- **PanelCard** — dark card + subtle border, uses theme colors
- **SectionHeader** — consistent title + gray subtitle
- **StatusPill** — online/live status with pulsing dot
- **GaugeCard** — animated semi-circular gauge + count-up numbers
- **SensorChart** — history line chart with gradient area

### Animations
- WQI ring & gauges: sweep + **count-up** numbers (`FastOutSlowInEasing`)
- Cards enter with a **spring** `DampingRatioMediumBouncy`
- Bottom nav: active icon scales up + glows
- Screen transitions: fade + scale (`tween` 240ms)

## Features

### Dashboard (Home)
- **WQI Hero Card** — animated ring, color-coded 0-100 score, count-up numbers
- **4 Gauge Cards** — pH, Temperature, TDS, Turbidity with safe/unsafe indicators
- **Time Filter** — "All | 1 Hour | 6 Hours | 24 Hours" chips
- **Online Status** — pulsing green dot + last-update timestamp

### Sensor Detail
- A tab per sensor (pH, Temperature, TDS, Turbidity)
- Gauge + history chart (gradient area under the line) + status chip
- Same time filter

### Location Map (osmdroid / OpenStreetMap)
- **100% free** — no API key, no credit card
- Pins colored by water quality status
- Tap the map to mark a point → press + to save
- Add/remove locations + sync sensor data to a location

## Connecting to Supabase

The app uses the **anon key** (public, no login) with RLS policies:
```sql
-- Public can read (for the anonymous Android app)
CREATE POLICY "Public can read sensor data"
    ON public.sensor_data FOR SELECT USING (true);

CREATE POLICY "Public can read devices"
    ON public.devices FOR SELECT USING (true);
```

**Important**: these policies are for demo mode. For production, require login.

## Integration with the Web Dashboard

The Android app and the web dashboard share the same Supabase backend:

| Aspect | Web | Android |
|-------|-----|---------|
| **Backend** | Supabase (JS client) | Supabase (Ktor REST) |
| **WQI Algorithm** | Weighted average (0.3 pH + 0.25 TDS + 0.25 Turb + 0.2 Temp) | Identical |
| **Status Threshold** | >=80 VERY SUITABLE, >=60 SUITABLE, <60 DANGER | Identical |
| **Sensor Colors** | `#F59E0B` pH, `#F97316` Temperature, `#22C55E` TDS, `#8B5CF6` Turb | Identical |

### WQI Algorithm (Unified)

```
Sub-index pH:     100 if 6.5-8.5, interpolation 5.0-6.5 & 8.5-10.0
Sub-index TDS:    100 if <=300, interpolation 300-500
Sub-index Turb:   100 if <=1, interpolation 1-5
Sub-index Temp:   100 if 20-30°C
Weighted: pH*0.3 + TDS*0.25 + Turb*0.25 + Temp*0.2
```

This algorithm runs in the Edge Function (server-side) and is copied to the Android app for local calculation (test locations).

## Troubleshooting

### Build fails: "Unsupported class file major version 69"
→ JDK is too new. Make sure `org.gradle.java.home` in `~/.gradle/gradle.properties` points to JDK 21.

### App is empty: "Waiting for sensor data..."
1. Check that `SupabaseConfig.kt` has the URL + key filled in
2. Check that the `sensor_data` table in the Supabase dashboard has data
3. Check that the RLS policies are applied (see the SQL above)

### Map is empty (osmdroid)
→ Requires an internet connection (OSM tiles are downloaded when opened). If offline, try again later — tiles are not cached.

### Gauge doesn't show the safe/unsafe indicator
→ Make sure the value is not null. If null, it shows "No data yet" (gray).
