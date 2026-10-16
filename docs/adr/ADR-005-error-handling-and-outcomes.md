# ADR-005: Strategi Error Handling — Outcome & DomainError

- **Status:** Accepted
- **Tanggal:** 2026-10-16
- **Konteks:**
  Melempar exception tak terduga lintas layer arsitektur (dari data/gateway ke UI) sering kali menyebabkan crash yang tidak terdeteksi, silent failure, dan pengalaman pengguna yang buruk saat offline.
- **Keputusan:**
  Menggunakan tipe fungsional eksplisit `Outcome<T, E>` di batas layer domain dan hirarki tertutup `DomainError` (Unauthorized, NotFound, Validation, Offline, Unexpected) untuk menggantikan lemparan exception mentah.
- **Alternatif yang Dipertimbangkan:**
  1. *Standard kotlin.Result:* Kurang fleksibel karena error selalu bertipe `Throwable` dan tidak dapat dibatasi ke varian error bisnis tertentu.
  2. *Try-Catch di ViewModel:* Menyebabkan boilerplate berulang di setiap ViewModel dan rawan meloloskan `CancellationException` pada Coroutines.
- **Konsekuensi:**
  - *Positif:* Kegagalan dipaksa untuk ditangani secara eksplisit saat kompilasi (*compile-time exhaustive check*), mapping ke pesan UI (`UiText`) menjadi terpusat dan aman.
  - *Negatif:* Memerlukan mapping eksplisit dari SDK exception pihak ketiga ke `DomainError`.
