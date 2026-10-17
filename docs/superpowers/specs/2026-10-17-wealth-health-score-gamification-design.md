# Superpowers Design Spec: Wealth Health Score & Financial Gamification

**Status:** Approved  
**Author:** Principal Mobile Engineer  
**Date:** 2026-10-17  
**Milestone:** 3 of 5 (Product Differentiators)

---

## 1. Executive Summary & Problem Statement

### 1.1 The Pain Point
Aplikasi pencatat keuangan konvensional seringkali membosankan dan membuat pengguna stres karena hanya menampilkan angka merah pengeluaran. Tanpa adanya *sense of progress*, *gamification*, atau pengakuan atas disiplin finansial, 70% pengguna berhenti menggunakan aplikasi budgeting setelah 2–3 minggu.

### 1.2 The Solution: Wealth Health Score & Gamification
SpendWealthy mengubah pencatatan keuangan menjadi pengalaman yang memuaskan dan berprestasi layaknya *"Credit Score + Fitness Tracker Finansial"*:
1. **Skor Finansial Berkelanjutan (300 – 850):**
   - Menghitung kesehatan finansial riil pengguna secara holistik berdasarkan 5 pilar (Resilience, Momentum, Budget Adherence, Liquidity, Legacy).
   - Klasifikasi Tier:
     - **300 – 500:** *Financial Alert / Needs Attention* (Merah)
     - **501 – 700:** *Developing Builder* (Kuning/Amber)
     - **701 – 800:** *Wealth Professional* (Emerald Green)
     - **801 – 850:** *Sovereign Elite* (Canary Gold)
2. **Visual Gauge & Streak Counter:**
   - Visual arc meter / speedometer modern dengan jarum/progres halus.
   - Indikator *Daily Logging Streak* (e.g. 🔥 *7 Hari Berturut-turut Disiplin Finansial*).
3. **Actionable Score Boosters ("Misi Penaikan Skor"):**
   - Rekomendasi konkret berorientasi misi:
     - 🎯 *"Alokasikan tabungan bulan ini (+15 pts)"*
     - 🛡️ *"Perpanjang dana darurat ke 3 bulan (+25 pts)"*
     - ⚡ *"Selesaikan seluruh tagihan sebelum jatuh tempo (+10 pts)"*
4. **Koleksi Badge Prestasi Finansial (Gamification):**
   - Visual badge terlock & unlocked: *Budget Master*, *Emergency Shield*, *Debt Free*, *Super Allocator*, *7-Day Streak*.

---

## 2. Architecture & Domain Design

### 2.1 Domain Models (`WealthHealthModels.kt`)
```kotlin
data class FinancialStreak(
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val lastActiveDate: LocalDate,
    val isStreakMaintainedToday: Boolean
)

data class ScoreBoosterMission(
    val id: String,
    val title: String,
    val description: String,
    val potentialPoints: Int,
    val category: BoosterCategory,
    val isCompleted: Boolean,
    val actionDeepLink: String? = null
)

enum class BoosterCategory {
    EMERGENCY_FUND, BILL_PAYMENT, BUDGET_DISCIPLINE, SAVING_GOAL, ASSET_GROWTH
}

data class FinancialBadge(
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val tier: BadgeTier,
    val isUnlocked: Boolean,
    val unlockedAt: Instant? = null
)

enum class BadgeTier {
    BRONZE, SILVER, GOLD, PLATINUM
}

data class ComprehensiveHealthState(
    val score: WealthScore,
    val streak: FinancialStreak,
    val boosters: List<ScoreBoosterMission>,
    val badges: List<FinancialBadge>
)
```

### 2.2 Gamification & Health Score Engine (`FinancialGamificationService.kt`)
- `evaluateMissions(...)`: Menginspeksi saldo dompet, runway bulan dana darurat, tagihan yang belum terbayar, dan kepatuhan anggaran untuk menghasilkan booster missions yang relevan dan dinamis.
- `evaluateBadges(...)`: Memeriksa pencapaian pengguna (misalnya: apakah memiliki dana darurat > 3 bulan, tidak ada tagihan overdue, streak aktif).
- `calculateStreak(...)`: Menghitung streak pencatatan transaksi pengguna berdasarkan tanggal transaksi terakhir.

---

## 3. UI/UX Components

1. **`WealthScoreCard` (HomeScreen Component):**
   - Kartu elegan di HomeScreen (antara Total Balance dan Cashflow Metrics, atau dapat dibuka dengan tap).
   - Menampilkan skor besar (e.g. `742`), Tier badge (`Wealth Professional`), delta bulan ini (`+12 pts`), dan streak pill (`🔥 7 Hari Disiplin`).
2. **`WealthScoreDetailBottomSheet` (Full Interactive Modal):**
   - Header & Speedometer Arc Gauge yang memukau.
   - 5 Pillar Breakdown Bar:
     - Resilience (Dana Darurat): e.g. 110/150
     - Momentum (Pertumbuhan Aset): e.g. 105/150
     - Adherence (Kepatuhan Anggaran): e.g. 120/150
     - Liquidity (Likuiditas Kas): e.g. 85/100
     - Legacy (Keluarga & Vault): e.g. 70/100
   - Tab *"Misi Penaikan Skor"* (Booster Missions).
   - Tab *"Koleksi Lencana"* (Gamified Badges).

---

## 4. Verification Plan
- Unit test engine kalkulasi skor, misi, dan badge unlocking (`FinancialGamificationServiceTest.kt`).
- Unit test ViewModel state (`WealthScoreViewModelTest.kt`).
- Visual & interaction validation di iOS Simulator (iPhone 17 Pro).
