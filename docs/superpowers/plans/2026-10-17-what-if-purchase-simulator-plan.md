# Implementation Plan: "What-If" Purchase Simulator ("Boleh Beli Gak Ya?")

## 1. Goal
Implement a proactive purchase simulator in SpendWealthy that calculates the financial impact of a planned purchase on daily allowance, emergency runway, and family savings goals prior to spending.

---

## 2. Technical Scope & Architecture

### 2.1 File Map & Responsibilities
- `composeApp/src/commonMain/kotlin/com/spendwealthy/app/domain/service/PurchaseSimulationEngine.kt`:
  Core domain calculation engine implementing verdict logic and impact math.
- `composeApp/src/commonMain/kotlin/com/spendwealthy/app/domain/model/PurchaseSimulationModels.kt`:
  Data contracts (`PurchaseVerdict`, `PurchaseSimulationRequest`, `PurchaseSimulationResult`).
- `composeApp/src/commonMain/kotlin/com/spendwealthy/app/ui/screens/simulator/PurchaseSimulatorViewModel.kt`:
  MVI/StateFlow ViewModel coordinating wallet balances, bills, budgets, and reactive debounce calculation.
- `composeApp/src/commonMain/kotlin/com/spendwealthy/app/ui/screens/simulator/PurchaseSimulatorBottomSheet.kt`:
  Interactive Compose Multiplatform bottom sheet with currency input, preset chips, wallet selector, and reactive metrics card.
- `composeApp/src/commonMain/kotlin/com/spendwealthy/app/core/di/KoinModules.kt`:
  Koin dependency injection registration for engine and ViewModel.
- `composeApp/src/commonMain/kotlin/com/spendwealthy/app/ui/screens/home/HomeScreen.kt`:
  Integration of `"Boleh Beli?"` quick action entry point.
- `composeApp/src/commonTest/kotlin/com/spendwealthy/app/domain/service/PurchaseSimulationEngineTest.kt`:
  Domain unit test suite.
- `composeApp/src/commonTest/kotlin/com/spendwealthy/app/ui/screens/simulator/PurchaseSimulatorViewModelTest.kt`:
  ViewModel unit test suite.

---

## 3. Step-by-Step Implementation Steps

### Step 1: Domain Models & Calculation Engine
1. Create `PurchaseSimulationModels.kt` containing `PurchaseVerdict`, `PurchaseSimulationRequest`, and `PurchaseSimulationResult`.
2. Implement `PurchaseSimulationEngine.kt` computing:
   - Remaining daily safe allowance before vs after purchase.
   - Emergency runway months before vs after purchase based on monthly burn rate.
   - Family savings goal postponement estimation.
   - Verdict classification (`SAFE_TO_BUY`, `PROCEED_WITH_CAUTION`, `HIGH_RISK_DEFICIT`).
3. Write comprehensive unit tests in `PurchaseSimulationEngineTest.kt`.
4. Register `PurchaseSimulationEngine` in `KoinModules.kt`.

### Step 2: ViewModel Implementation
1. Create `PurchaseSimulatorViewModel.kt`:
   - State holding `itemName`, `priceAmount`, `selectedWallet`, `availableWallets`, `simulationResult`, `isCalculating`.
   - Debounced flow (150ms) triggering engine recalculation when price or wallet changes.
2. Write unit tests in `PurchaseSimulatorViewModelTest.kt`.
3. Register ViewModel in `KoinModules.kt`.

### Step 3: Compose UI Components
1. Create `PurchaseSimulatorBottomSheet.kt`:
   - Standardized title header (`"Boleh Beli Gak Ya?"` + subtitle).
   - Formatted currency input field with haptic feedback.
   - Quick preset chips (`Rp 100rb`, `Rp 500rb`, `Rp 2jt`, `Rp 10jt`).
   - Wallet selection horizontal list.
   - Reactive verdict banner with icons and side-by-side metric comparisons.
   - Action buttons: "Lanjut Catat Transaksi" & "Jadikan Target Tabungan".

### Step 4: HomeScreen Integration
1. Update `HomeScreen.kt` Quick Action buttons to include `"Boleh Beli?"` with shopping bag / psychology icon.
2. Wire clicking the action to show `PurchaseSimulatorBottomSheet`.
3. Wire CTA button callbacks to navigate to `AddTransactionScreen` or `FamilyScreen`.

### Step 5: Verification & Simulator Execution
1. Run `./gradlew compileDebugKotlinAndroid testDebugUnitTest`.
2. Deploy to iOS simulator via `./run.sh fast-ios` or `./run.sh ios`.
3. Capture simulator screenshot to verify UI polish and responsiveness.
