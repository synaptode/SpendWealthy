# Implementation Plan: Smart Paycheck Splitter ("Bagi Gaji Otomatis")

**Milestone:** 2 of 5  
**Design Spec:** [2026-10-17-smart-paycheck-splitter-design.md](../specs/2026-10-17-smart-paycheck-splitter-design.md)

---

## Proposed Changes

### Domain Layer
- **`composeApp/.../domain/model/PaycheckSplitModels.kt`**:
  - `SplitStrategy` (50/30/20, 60/20/20, 40/20/40, CUSTOM).
  - `PaycheckSplitBreakdown`, `GoalSplitAllocation`, `ExecuteSplitRequest`, `ExecuteSplitResult`.
- **`composeApp/.../domain/service/PaycheckSplitterEngine.kt`**:
  - Pure domain engine to calculate breakdowns, validate bills coverage, project daily safe allowance, and distribute savings across goals.
- **`composeApp/.../domain/usecase/ExecutePaycheckSplitUseCase.kt`**:
  - Orchestrates ledger income recording, savings transfers, and goal progress updates.

### Presentation Layer
- **`composeApp/.../ui/screens/splitter/PaycheckSplitterViewModel.kt`**:
  - ScreenModel managing income amount, wallet selection, strategy selection, custom ratios, and execution state.
- **`composeApp/.../ui/screens/splitter/PaycheckSplitterBottomSheet.kt`**:
  - Material 3 ModalBottomSheet displaying paycheck input, strategy tabs, 3-bucket interactive progress bar, detailed bucket breakdown cards, and execution CTA.
- **`composeApp/.../ui/screens/home/HomeScreen.kt`**:
  - Add quick trigger for Paycheck Splitter (e.g. inside header or quick actions / FAB menu).

### Dependency Injection
- **`composeApp/.../core/di/KoinModules.kt`**:
  - Register `PaycheckSplitterEngine`, `ExecutePaycheckSplitUseCase`, and `PaycheckSplitterViewModel`.

---

## Verification Plan

### Automated Tests
- `PaycheckSplitterEngineTest.kt`: Unit test allocation logic, rounding, bills warning, and goal allocation.
- `ExecutePaycheckSplitUseCaseTest.kt`: Unit test atomic recording of income and goal progress updates.
- `PaycheckSplitterViewModelTest.kt`: Test reactive UI state and calculations.
- `./gradlew testDebugUnitTest`: Verify 100% pass rate.

### Manual Verification on Simulator
- Open Paycheck Splitter BottomSheet.
- Verify changing salary input updates all 3 buckets reactively.
- Switch strategies (50/30/20 -> 60/20/20 -> 40/20/40).
- Confirm bills warning and target goal allocations.
- Tap "Alokasikan Gaji Sekarang" and verify state.
