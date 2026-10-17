# Design Specification: "What-If" Purchase Simulator ("Boleh Beli Gak Ya?")

## 1. Overview & Problem Statement

Users of personal finance apps often experience **impulsive buying remorse**. Existing tools only record expenses *post-facto* (after the money is already spent). SpendWealthy aims to differentiate itself by providing proactive, pre-purchase decision intelligence that answers:
> *"If I buy this item right now, how does it affect my daily allowance until payday, my emergency runway, and my family savings goals?"*

This feature turns SpendWealthy into a proactive financial guardian rather than a passive ledger.

---

## 2. Architecture & Layering

Following Clean Architecture principles already established in SpendWealthy:

```
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│  - PurchaseSimulatorBottomSheet.kt                     │
│  - PurchaseSimulatorViewModel.kt                       │
│  - Entry Points: Home Quick Action & FAB Add Sheet     │
└──────────────────────────┬─────────────────────────────┘
                           │ uses / observes
┌──────────────────────────▼─────────────────────────────┐
│                      Domain Layer                      │
│  - PurchaseSimulationEngine.kt                         │
│  - Models: PurchaseSimulationRequest, Result, Verdict  │
│  - Reuses: SafeToSpendEngine, ResilienceService        │
└──────────────────────────┬─────────────────────────────┘
                           │ reads
┌──────────────────────────▼─────────────────────────────┐
│                      Data Layer                        │
│  - WalletRepository, TransactionRepository,            │
│    BillRepository, BudgetRepository, FamilyRepository  │
└────────────────────────────────────────────────────────┘
```

---

## 3. Domain Models & Calculation Logic

### 3.1 Domain Models

```kotlin
package com.spendwealthy.app.domain.model

enum class PurchaseVerdict {
    SAFE_TO_BUY,          // High resilience, minimal impact on daily cash & savings
    PROCEED_WITH_CAUTION, // Acceptable, but tightens daily allowance or delays a goal
    HIGH_RISK_DEFICIT     // Depletes safety buffer, cuts runway < 1 month, or creates daily deficit
}

data class PurchaseSimulationRequest(
    val itemName: String,
    val price: Money,
    val selectedWalletId: String? = null,
    val categoryId: String? = null
)

data class PurchaseSimulationResult(
    val verdict: PurchaseVerdict,
    val verdictTitle: String,
    val verdictMessage: String,
    val currentDailyAllowance: Money,
    val simulatedDailyAllowance: Money,
    val emergencyRunwayMonthsCurrent: Double,
    val emergencyRunwayMonthsSimulated: Double,
    val impactOnFamilyGoalDaysDelayed: Int,
    val recommendation: String,
    val canAffordDirectly: Boolean
)
```

### 3.2 Calculation Rules in `PurchaseSimulationEngine`

1. **Daily Safe Allowance Calculation:**
   - Retrieve current `SafeToSpendResult` from `SafeToSpendEngine` using current liquidity, unpaid bills, budgets, and days until payday.
   - Simulate deduction of `price.amount` from active liquidity.
   - Recalculate `simulatedDailyAllowance = (simulatedSafeLiquidity / remainingDaysToPayday).coerceAtLeast(0)`.

2. **Emergency Runway Impact:**
   - Calculate current monthly burn rate from the last 90 days of transactions.
   - `emergencyRunwayMonthsSimulated = ((currentLiquidity - price.amount) / monthlyBurnRate).coerceAtLeast(0.0)`.

3. **Family Goal Delay Impact:**
   - If active collaborative goals exist in `FamilyRepository`:
     - Estimate monthly surplus after mandatory bills and basic budget.
     - Calculate how many days of planned savings the purchase price represents:
       `daysDelayed = ((price.amount.toDouble() / monthlySavingsRate) * 30).toInt().coerceAtLeast(0)`.

4. **Verdict Determination:**
   - If `price.amount > selectedWallet.balance.amount`:
     - `verdict = HIGH_RISK_DEFICIT`
     - `verdictTitle = "Saldo Tidak Cukup"`
     - `recommendation = "Saldo dompet terpilih tidak mencukupi untuk transaksi tunai ini."`
   - Else if `emergencyRunwayMonthsSimulated < 1.0` OR `simulatedDailyAllowance.amount == 0L`:
     - `verdict = HIGH_RISK_DEFICIT`
     - `verdictTitle = "Risiko Tinggi / Defisit"`
     - `recommendation = "Pembelian ini akan menguras cadangan kas dan membuat jatah harianmu defisit hingga gajian."`
   - Else if `emergencyRunwayMonthsSimulated < 3.0` OR `simulatedDailyAllowance.amount < (currentDailyAllowance.amount * 0.5)`:
     - `verdict = PROCEED_WITH_CAUTION`
     - `verdictTitle = "Pertimbangkan Lagi"`
     - `recommendation = "Kamu bisa membelinya, tetapi sisa jatah harian berkurang lebih dari 50% hingga akhir bulan."`
   - Else:
     - `verdict = SAFE_TO_BUY`
     - `verdictTitle = "Aman Dibeli! 🎉"`
     - `recommendation = "Finansialmu sangat sehat untuk pembelian ini tanpa mengganggu pos tabungan maupun tagihan."`

---

## 4. UI / UX Design Specifications

### 4.1 Component: `PurchaseSimulatorBottomSheet`
- **Container:** Modal BottomSheet with rounded top corners (`28.dp`), surface background `AppColors.Surface`, blurred backdrop scrim.
- **Header:**
  - Standardized headline layout: `"Boleh Beli Gak Ya?"` with subtitle `"Simulasi dampak pembelian sebelum uang keluar"`.
- **Inputs:**
  - **Item Name**: Outlined/Surface text field with subtle border and clear button.
  - **Price Input**: Formatted currency text field (e.g. `Rp 1.500.000`) with quick preset chips (`Rp 100rb`, `Rp 500rb`, `Rp 2jt`, `Rp 10jt`).
  - **Wallet Picker**: Horizontal pill selector of active wallets showing current balance.
- **Reactive Results Card:**
  - Updates debounced (150ms) as the user types or adjusts presets.
  - **Verdict Header Pill**: Color-coded container (`AppColors.Success`, `AppColors.Warning`, `AppColors.Error`) with dynamic icon (`CheckCircle`, `HelpOutline`, `Warning`).
  - **Metrics Grid (Side-by-Side Comparison)**:
    - *Jatah Harian*: `Current` $\rightarrow$ `Simulated` (colored green/red indicator).
    - *Dana Darurat*: `Current` $\rightarrow$ `Simulated` (e.g., `4.5 Bln` $\rightarrow$ `3.2 Bln`).
    - *Tabungan Keluarga*: `Mundur X hari` or `Tidak terpengaruh`.
- **Action CTA Buttons:**
  - Primary CTA: *"Lanjut Catat Transaksi"* -> Closes sheet and navigates to `AddTransactionScreen` pre-populated with item name, price, and wallet.
  - Secondary CTA: *"Jadikan Target Tabungan"* -> Closes sheet and navigates to `FamilyScreen` / `AddGoalDialog`.
  - Tertiary: *"Batal / Simpan Saja Dulu"*.

---

## 5. Entry Points & Integration

1. **HomeScreen Quick Actions:**
   - Expand `QuickActionRow` from 4 items (`Send`, `Receive`, `Swap`, `Deposit`) into a dynamic 5-item grid or scrollable row adding `"Boleh Beli?"` (Icon: `Psychology` or `ShoppingBagOutlined`).
2. **MainScreen FAB Dock (+):**
   - In addition to directly adding income/expense, provide a quick entry *"Simulasi Beli"*.

---

## 6. Testing Strategy

1. **Unit Tests (`PurchaseSimulationEngineTest`):**
   - Verifying `SAFE_TO_BUY` for low price with high liquidity.
   - Verifying `PROCEED_WITH_CAUTION` when daily allowance drops below 50%.
   - Verifying `HIGH_RISK_DEFICIT` when price exceeds wallet balance or drops runway below 1 month.
   - Edge case: Zero days until payday (same-day payday handling).
   - Edge case: Zero transaction history / empty wallets.
2. **ViewModel Tests (`PurchaseSimulatorViewModelTest`):**
   - Testing debounced reactive calculation when price changes.
   - Testing wallet selection state changes.
   - Verifying transition to `AddTransactionScreen` payload.
