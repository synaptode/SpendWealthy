# ADR-006: Keamanan Data Lokal & Platform-Specific Hardware Gateways

## Status
Diterima (Accepted)

## Konteks
Sebagai aplikasi personal finance offline-first, **SpendWealthy** menyimpan data finansial sensitif pengguna (transaksi, saldo rekening, anggaran bulanan, brankas dokumen rahasia) langsung di penyimpanan lokal perangkat tanpa server pihak ketiga.
Dibutuhkan strategi keamanan, isolasi data, dan integrasi hardware native (biometrik, keychain/keystore, background notifications, secure share) yang:
1. Menjamin kerahasiaan data (Zero Knowledge, enkripsi pada rest & transit).
2. Mematuhi standar keamanan Android Keystore dan Apple Secure Enclave / Keychain.
3. Menghindari abstraksi bocor (*leaky abstraction*) ke layer bisnis (Domain).

## Keputusan
1. **Pemisahan Interface Gateways di Domain**:
   - `SecureStorage`: Kontrak penyimpanan key-value terenkripsi.
   - `BiometricGateway`: Kontrak otentikasi biometrik (Face ID, Touch ID, Fingerprint).
   - `NotificationScheduler`: Kontrak penjadwalan reminder tagihan lokal.
   - `FileShareGateway`: Kontrak ekspor file aman tanpa izin penyimpanan global yang berlebihan.
2. **Implementasi Native Bebas Dependensi Eksternal Liar**:
   - **Android**: Menggunakan `AndroidKeyStore` dengan algoritma AES/GCM/NoPadding, master key hardware-backed, dan `androidx.biometric.BiometricPrompt`.
   - **iOS**: Menggunakan API bawaan Cocoa (`LocalAuthentication.LAContext`, `Security.framework` Keychain, dan `UserNotifications.UNUserNotificationCenter`) langsung via Kotlin/Native tanpa jembatan CocoaPods pihak ketiga yang lambat.
3. **Resilience & Safe Fallbacks**:
   - Seluruh pemanggilan gateway mengembalikan `GatewayResult<T>` (`Success` atau `Failure(message, recoverable)`), mencegah crash jika hardware biometrik tidak tersedia atau izin notifikasi ditolak oleh pengguna.

## Konsekuensi
### Positif
- **Zero Third-Party SDK Bloat**: Tidak bergantung pada library tracking atau analytics invasif.
- **Testable**: Domain dan ViewModel dapat diuji 100% menggunakan `FakeSecureStorage` dan `FakeNotificationScheduler` tanpa perlu emulator/simulator.
- **Hardware-Level Security**: Kunci enkripsi tersimpan di Secure Enclave / Android StrongBox jika didukung perangkat.

### Negatif / Mitigasi
- Kode platform-specific ditulis terpisah di `androidMain` dan `iosMain`.
- *Mitigasi*: Dikelola melalui Koin `platformModule` dengan interface yang identik dan terdokumentasi.
