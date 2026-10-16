# ADR-002: Solusi Navigasi — Voyager Multiplatform

- **Status:** Accepted
- **Tanggal:** 2026-10-16
- **Konteks:**
  Aplikasi memiliki navigasi hierarkis berlapis: Shell Navigation (TabNavigator untuk Home, Transaksi, Budget, Vault), stack navigator untuk alur modal (AddTransaction, AddBill, AddWallet), serta transisi antar sub-layar.
- **Keputusan:**
  Menggunakan **Voyager** (`cafe.adriel.voyager`) dengan ekstensi Koin (`voyager-koin`) dan TabNavigator (`voyager-tab-navigator`).
- **Alternatif yang Dipertimbangkan:**
  1. *Navigation Compose (Jetpack/KMP):* Masih dalam tahap stabilisasi multiplatform saat proyek dimulai, memiliki konfigurasi rute string yang lebih rawan kesalahan dibandingkan Screen model Voyager.
  2. *Decompose:* Sangat kuat namun memiliki kurva pembelajaran dan *boilerplate* `ComponentContext` yang lebih tinggi.
- **Konsekuensi:**
  - *Positif:* Type-safe Screen objects, navigasi tab & stack intuitif, integrasi mulus dengan Koin ScreenModel / BaseViewModel.
  - *Negatif:* Mengunci navigasi ke lifecycle Screen Voyager, harus berhati-hati saat mengakses `rootNavigator` dari dalam sub-tab.
