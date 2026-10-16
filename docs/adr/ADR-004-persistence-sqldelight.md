# ADR-004: Persistensi Data Lokal — SQLDelight Offline-First

- **Status:** Accepted
- **Tanggal:** 2026-10-16
- **Konteks:**
  SpendWealthy mengusung pilar *local-first* dan privasi finansial mutlak. Aplikasi harus dapat berfungsi penuh tanpa koneksi internet dan mampu menangani transaksi relasional kompleks (akun dompet, kategori, transaksi, anggaran, cicilan, dan vault dokumen).
- **Keputusan:**
  Menggunakan **SQLDelight** (`app.cash.sqldelight`) dengan driver SQLite native per platform (`android-driver` di Android dan `native-driver` di iOS).
- **Alternatif yang Dipertimbangkan:**
  1. *Room KMP:* Masih relatif baru di ekosistem multiplatform dan tool migrasi skema di iOS memerlukan konfigurasi tambahan.
  2. *Realm / Mongo Device Sync:* Mengandung overhead binary yang besar dan model lisensi proprietary.
- **Konsekuensi:**
  - *Positif:* SQL-first murni, query type-safe yang divalidasi saat kompilasi dari berkas `.sq`, performa eksekusi sangat cepat dengan memori minimal.
  - *Negatif:* Penulisan migrasi skema dan mapping entitas harus dilakukan secara eksplisit tanpa otomatisasi ORM penuh.
