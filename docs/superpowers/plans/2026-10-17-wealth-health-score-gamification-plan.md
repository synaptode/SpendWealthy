# Implementation Plan: Wealth Health Score & Financial Gamification

**Milestone:** 3 of 5  
**Design Spec:** [2026-10-17-wealth-health-score-gamification-design.md](../specs/2026-10-17-wealth-health-score-gamification-design.md)

---

## Proposed Changes

### Domain Layer
- **`composeApp/.../domain/model/WealthHealthModels.kt`**:
  - `FinancialStreak`, `ScoreBoosterMission`, `BoosterCategory`, `FinancialBadge`, `BadgeTier`, `ComprehensiveHealthState`.
- **`composeApp/.../domain/service/FinancialGamificationService.kt`**:
  - Evaluates streaks from transactions, dynamic actionable missions from financial state, and badge achievement unlocks.

### Presentation Layer
- **`composeApp/.../ui/screens/health/WealthScoreViewModel.kt`**:
  - ScreenModel coordinating `WealthScoreEngine`, `ResilienceService`, `FinancialGamificationService`, wallets, bills, budgets, goals, and transactions.
- **`composeApp/.../ui/screens/health/WealthScoreCard.kt`**:
  - HomeScreen luxury card displaying Score (300-850), tier status badge, delta indicator, and streak pill.
- **`composeApp/.../ui/screens/health/WealthScoreDetailBottomSheet.kt`**:
  - Interactive bottom sheet with visual circular/arc gauge, 5-pillar breakdown meters, mission checklist, and badge collection grid.
- **`composeApp/.../ui/screens/home/HomeScreen.kt`**:
  - Insert `WealthScoreCard` prominently on Home and trigger `WealthScoreDetailBottomSheet`.

### Dependency Injection
- **`composeApp/.../core/di/KoinModules.kt`**:
  - Register `FinancialGamificationService` and `WealthScoreViewModel`.

---

## Verification Plan
1. Unit tests in `FinancialGamificationServiceTest.kt` verifying streak calculation, mission generation, and badge unlock evaluation.
2. Unit tests in `WealthScoreViewModelTest.kt` verifying reactive state and 5-pillar breakdown aggregation.
3. Gradle check via `./gradlew testDebugUnitTest`.
4. Verification and screenshot capture on live iOS simulator.
