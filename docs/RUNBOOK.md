# SpendWealthy — Operational & Release Runbook

Panduan komprehensif bagi Mobile Engineer untuk menjalankan, menguji, memverifikasi migrasi basis data, dan memproduksi build rilis Android dan iOS.

---

## 1. Quick Development Shortcuts

Gunakan Makefile atau runner script yang telah disediakan:

```bash
# Menjalankan iOS Simulator secara instan (otomatis boot & open simulator window)
make ios
# Launch cepat < 2 detik (tanpa rebuild framework):
make fast-ios

# Menjalankan Android Emulator / Device fisik
make android

# Menjalankan KEDUA platform sekaligus (side-by-side)
make both

# Menjalankan seluruh test suite
make test
```

---

## 2. Testing & Quality Assurance

### 2.1 Menjalankan Unit Tests
```bash
./gradlew testDebugUnitTest
```
Seluruh 33 pengujian unit harus berstatus **100% Passed**. Modul yang diuji mencakup:
- `SafeToSpendEngineTest` (Kalkulasi liquidity buffer & safe-to-spend harian)
- `BudgetEngineTest` (Alokasi anggaran belanja)
- `CsvReportExporterTest` (Ekspor laporan CSV)
- `BalanceCalculatorTest` (Kalkulasi saldo lintas dompet)
- `MoneyTest` (Operasi matematika mata uang Rupiah)
- `OutcomeTest` (Monadic Result pattern)
- `SubscriptionPolicyTest` (Validasi kuota & status premium)
- `AppViewModelTest` (Startup state transition & in-app notifications)

### 2.2 Verifikasi Skema & Migrasi SQLDelight
Sebelum melakukan commit perubahan pada query basis data `.sq` atau migrasi `.sqm`:
```bash
./gradlew :composeApp:verifySqlDelightMigration
```

---

## 3. Production Release Checklist

### 3.1 Android Release
1. Pastikan Proguard keep rules di [composeApp/proguard-rules.pro](file:///Volumes/Local/Projects/Mobile/Multiplatform/spendwealthy/composeApp/proguard-rules.pro) mencakup class DTO baru.
2. Build Release APK:
   ```bash
   ./gradlew :composeApp:assembleRelease
   ```
3. Build Android App Bundle (AAB) untuk Google Play Store:
   ```bash
   ./gradlew :composeApp:bundleRelease
   ```
   Output dihasilkan di: `composeApp/build/outputs/bundle/release/composeApp-release.aab`.

### 3.2 iOS Production Archive
1. Pastikan scheme `iosApp` menggunakan konfigurasi `Release`.
2. Generate framework rilis Kotlin/Native:
   ```bash
   ./gradlew :composeApp:compileKotlinIosArm64
   ```
3. Melakukan Archive via Xcode:
   ```bash
   xcodebuild -project iosApp/iosApp.xcodeproj \
     -scheme iosApp \
     -configuration Release \
     -destination 'generic/platform=iOS' \
     -archivePath "build/iosApp.xcarchive" \
     archive
   ```
4. Distribusikan ke TestFlight via Xcode Organizer atau Fastlane.

---

## 4. Keamanan & Kebijakan Data
- **Offline-First**: Aplikasi beroperasi penuh tanpa jaringan internet.
- **Biometrik**: Menggunakan `BiometricPrompt` (Android) dan `LAContext` (iOS) untuk membuka kunci aplikasi.
- **Enkripsi**: Master key tersimpan di `AndroidKeyStore` dan `iOS Keychain`. Dokumen brankas disimpan terisolasi di private internal app storage.

---

## 5. Troubleshooting Cepat
- **Gradle Daemon Out of Memory**: Jalankan `./gradlew --stop` dan tingkatkan `-Xmx` di `gradle.properties`.
- **iOS Simulator Blank Screen**: Jalankan `make fast-ios` untuk me-restart instance simulator.
- **Android ADB Device Not Found**: Jalankan `adb kill-server && adb start-server`.
