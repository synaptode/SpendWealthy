# ADR-003: Dependency Injection — Koin Multiplatform

- **Status:** Accepted
- **Tanggal:** 2026-10-16
- **Konteks:**
  KMP membutuhkan framework DI yang ringan, tidak bergantung pada Java reflection (karena harus berjalan di iOS melalui Kotlin/Native LLVM), dan mendukung scoping Android & iOS secara deklaratif.
- **Keputusan:**
  Menggunakan **Koin** (`io.insert-koin:koin-core` v4.0.0) dengan `koin-compose` dan `koin-compose-viewmodel`.
- **Alternatif yang Dipertimbangkan:**
  1. *kotlin-inject:* Compile-time safe via KSP, namun setup generator lebih kompleks dan integrasi runtime compose viewmodel kurang fleksibel untuk proyek fase awal-menengah.
  2. *Manual Service Locator:* Rentan kesalahan runtime, sulit diverifikasi di CI, dan tidak mendukung scoping ViewModel secara otomatis.
- **Konsekuensi:**
  - *Positif:* Setup instan, DSL intuitif, integrasi first-class dengan Compose Multiplatform dan Voyager.
  - *Negatif:* Verifikasi graph terjadi di runtime; wajib menjalankan tes integritas DI (`checkModules`) di CI.
