# Superpowers Design Spec: Smart Paycheck Splitter ("Bagi Gaji Otomatis")

**Status:** Approved  
**Author:** Principal Mobile Engineer  
**Date:** 2026-10-17  
**Milestone:** 2 of 5 (Product Differentiators)

---

## 1. Executive Summary & Problem Statement

### 1.1 The Pain Point
Banyak pengguna di Indonesia dan Asia Tenggara mengalami fenomena *"gaji cuma numpang lewat"*—pada tanggal gajian (tanggal 25 atau akhir bulan), uang masuk dalam jumlah besar, tetapi tanpa alokasi terencana uang tersebut cepat habis untuk belanja impulsif sebelum tagihan rutin atau komitmen tabungan diamankan.

Metode penganggaran manual tradisional terlalu lambat dan membosankan: pengguna harus menghitung persentase secara manual, menghitung satu per satu tagihan yang jatuh tempo, lalu membagi-bagi saldo.

### 1.2 The Solution: Smart Paycheck Splitter
Fitur **Smart Paycheck Splitter** hadir sebagai asisten cerdas yang langsung memandu pengguna saat uang gajian tiba:
1. **One-Tap Ingestion:** Pengguna memasukkan nominal gaji (atau mendeteksi income transaksi).
2. **Preset Strategi Teruji:**
   - **50 / 30 / 20 (Klasik Seimbang):** 50% Kebutuhan & Tagihan, 30% Lifestyle/Jatah Harian, 20% Tabungan/Investasi.
   - **60 / 20 / 20 (Prioritas Kebutuhan & Keluarga):** 60% Pokok, 20% Lifestyle, 20% Tabungan.
   - **40 / 20 / 40 (Aggressive Fire / Future Wealth):** 40% Pokok, 20% Lifestyle, 40% Tabungan & Investasi.
   - **Kustom:** Slider persentase interaktif dengan auto-balancing.
3. **Konteks Realita Finansial (Smart Binding):**
   - Mendeteksi total tagihan yang belum dibayar bulan ini (`BillRepository`), memastikan alokasi "Kebutuhan" tidak kurang dari tagihan riil.
   - Mengalokasikan pos "Tabungan" langsung ke Target Finansial / Dana Darurat aktif (`GoalRepository`).
4. **Eksekusi Sekali Klik (Atomic Execution):**
   - Mencatat transaksi Income ke dompet payroll.
   - Mentransfer atau mengalokasikan porsi tabungan ke `Goal` aktif.
   - Mengupdate Safe-to-Spend harian pengguna secara instan.

---

## 2. Architecture & Domain Design

### 2.1 Domain Models (`PaycheckSplitModels.kt`)
```kotlin
enum class SplitStrategy(
    val title: String,
    val description: String,
    val needsRatio: Float,
    val wantsRatio: Float,
    val savingsRatio: Float
) {
    BALANCED_50_30_20("50 / 30 / 20 Seimbang", "50% Kebutuhan, 30% Lifestyle, 20% Tabungan", 0.50f, 0.30f, 0.20f),
    NEEDS_FOCUSED_60_20_20("60 / 20 / 20 Keluarga", "60% Kebutuhan, 20% Lifestyle, 20% Tabungan", 0.60f, 0.20f, 0.20f),
    AGGRESSIVE_SAVER_40_20_40("40 / 20 / 40 Fire", "40% Kebutuhan, 20% Lifestyle, 40% Tabungan", 0.40f, 0.20f, 0.40f),
    CUSTOM("Kustom", "Atur persentase sesuai kebutuhanmu", 0.50f, 0.30f, 0.20f)
}

data class PaycheckSplitBreakdown(
    val totalIncome: Money,
    val needsAmount: Money,
    val needsRatio: Float,
    val wantsAmount: Money,
    val wantsRatio: Float,
    val savingsAmount: Money,
    val savingsRatio: Float,
    val upcomingBillsTotal: Money,
    val isNeedsSufficientForBills: Boolean,
    val billsDeficit: Money,
    val dailySafeAllowanceProjection: Money,
    val goalAllocations: List<GoalSplitAllocation>
)

data class GoalSplitAllocation(
    val goal: Goal,
    val allocatedAmount: Money,
    val projectedAmount: Money,
    val willAchieveGoal: Boolean
)

data class ExecuteSplitRequest(
    val totalIncome: Money,
    val payrollWalletId: String,
    val savingsWalletId: String?,
    val note: String,
    val breakdown: PaycheckSplitBreakdown
)
```

### 2.2 Domain Engine (`PaycheckSplitterEngine.kt`)
- `calculateSplit(...)`: Pure functional engine yang menerima gaji, rasio/strategi, daftar tagihan aktif bulan ini, dan daftar target tabungan aktif.
- Menghitung porsi nominal, memvalidasi apakah porsi *Needs* cukup untuk membayar semua tagihan aktif bulan ini, dan mendistribusikan porsi *Savings* ke target tabungan berdasarkan prioritas deadline/progres.

### 2.3 Use Case (`ExecutePaycheckSplitUseCase.kt`)
- Menjalankan transaksi atomic:
  1. Catat transaksi `INCOME` ke payroll wallet via `LedgerRepository`.
  2. Alokasikan ke target tabungan (`GoalRepository.updateGoalProgress`).
  3. Jika savings wallet terpisah dipilih, catat transfer dari payroll wallet ke savings wallet.

---

## 3. UI/UX Design

### 3.1 Material 3 BottomSheet / Dialog Experience
- **Header:** "Bagi Gaji Otomatis (Paycheck Splitter)" dengan badge tips finansial.
- **Input Gaji:** Formatted currency field (e.g. `Rp 15.000.000`) dengan preset gaji cepat (Rp 5jt, 10jt, 15jt, 25jt, 50jt).
- **Selector Dompet Penerima:** Horizontal selector dompet (e.g. BCA, Mandiri, Cash).
- **Strategy Selector:** Pill tabs (50/30/20, 60/20/20, 40/20/40, Custom).
- **Interactive Visual Breakdown Card:**
  - Bar pembagi warna (Hijau Kebutuhan, Oranye/Kuning Lifestyle, Biru/Ungu Tabungan).
  - 3 pos pengeluaran interaktif dengan nominal dan status:
    - 🏠 **Kebutuhan & Tagihan:** Rp 7.500.000 (Tagihan bulan ini: Rp 3.200.000 ✅ Aman)
    - ☕ **Lifestyle & Safe Daily:** Rp 4.500.000 (Jatah harian baru: Rp 150.000/hari)
    - 🎯 **Tabungan & Masa Depan:** Rp 3.000.000 (Disalurkan ke: Dana Darurat & Rumah Impian)
- **Primary CTA Button:** "Alokasikan Gaji Sekarang" (dengan animasi haptic).
- **Secondary CTA:** "Tutup / Nanti Saja".

---

## 4. Test Strategy
1. **Unit Test Engine (`PaycheckSplitterEngineTest.kt`):**
   - Menghitung rasio 50/30/20 dengan tepat dan pembulatan akurat.
   - Verifikasi peringatan ketika porsi kebutuhan lebih kecil dari tagihan (`isNeedsSufficientForBills == false`).
   - Verifikasi pembagian proporsional ke multiple Goals.
2. **Unit Test UseCase (`ExecutePaycheckSplitUseCaseTest.kt`):**
   - Memastikan pemanggilan `LedgerRepository` dan `GoalRepository` sesuai parameter.
3. **Unit Test ViewModel (`PaycheckSplitterViewModelTest.kt`):**
   - State loading, perubahan nominal gaji, pergantian strategi, dan trigger eksekusi.
4. **Simulator Verification:**
   - Validasi interaksi di iPhone 17 Pro simulator.
