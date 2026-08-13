param(
    [switch]$Install
)
$ErrorActionPreference = 'Stop'

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    $adb = "adb"
}

Write-Host "==> Building APK..." -ForegroundColor Cyan
& .\gradlew.bat :app:assembleDebug --console=plain
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apk = "app\build\outputs\apk\debug\app-debug.apk"
Write-Host "==> APK ready: $apk" -ForegroundColor Green

if ($Install) {
    Write-Host "==> Waiting for device (30s max)..." -ForegroundColor Cyan
    $device = ""
    for ($i = 0; $i -lt 30; $i++) {
        $out = & $adb devices
        $device = ($out | Where-Object { $_ -match "^\S+\s+device$" } | Select-Object -First 1)
        if ($device) { break }
        Start-Sleep -Seconds 1
    }
    if (-not $device) {
        Write-Host "!! No device found. Check USB cable + allow USB debugging on phone." -ForegroundColor Red
        exit 1
    }
    Write-Host "==> Installing to $($device.Split(' ')[0])..." -ForegroundColor Cyan
    & $adb install -r $apk
    if ($LASTEXITCODE -eq 0) {
        Write-Host "==> Done. Launching app..." -ForegroundColor Green
        & $adb shell am start -n com.aquasafe.monitor/.MainActivity
    }
}
