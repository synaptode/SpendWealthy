# ADR-001: Pendekatan UI — Compose Multiplatform

- **Status:** Accepted
- **Tanggal:** 2026-10-16
- **Konteks:**
  Aplikasi SpendWealthy menargetkan platform Android dan iOS dengan siklus rilis yang cepat, konsistensi desain yang tinggi (sistem warna *Forest Green & Neon Lime* dan *High-End Pounce* aesthetics), serta kebutuhan untuk berbagi komponen UI kustom (seperti CatPad, grafis saldo, kartu glassmorphic, dan visualizer analitik).
- **Keputusan:**
  Mengadopsi **Compose Multiplatform** untuk 100% layer presentasi bersama lintas Android dan iOS, dibungkus secara tipis oleh `MainActivity` (Android) dan `MainViewController` (iOS).
- **Alternatif yang Dipertimbangkan:**
  1. *Native UI terpisah (Jetpack Compose di Android + SwiftUI di iOS):* Menghasilkan overhead pemeliharaan ganda untuk komponen kustom dan animasi interaktif.
  2. *Flutter:* Tidak menawarkan interop Kotlin native murni dan interoperabilitas langsung dengan framework keamanan platform (Android Keystore / iOS Keychain).
- **Konsekuensi:**
  - *Positif:* Konsistensi visual 1:1, satu tim engineer dapat mengelola seluruh fitur, perputaran iterasi UI/UX jauh lebih cepat.
  - *Negatif:* Perlu perhatian ekstra terhadap performa recomposition dan alokasi objek pada perangkat Android berspesifikasi rendah (RAM 2-3GB).
