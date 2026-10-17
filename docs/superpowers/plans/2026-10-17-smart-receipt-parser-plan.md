# Implementation Plan: Smart Offline Receipt & Transaction Parser (Milestone 4)

## Proposed Changes

### 1. Domain Layer
- **`composeApp/src/commonMain/kotlin/com/spendwealthy/app/domain/model/SmartReceiptModels.kt`**
  - Define `ParsingSource`, `ReceiptLineItem`, `SplitBillAllocation`, `ParsedTransaction`.
- **`composeApp/src/commonMain/kotlin/com/spendwealthy/app/domain/service/SmartTransactionParserService.kt`**
  - Implement heuristic tokenizers, regex patterns for BCA, Mandiri, BRI, BNI, GoPay, OVO, ShopeePay, DANA, and itemized restaurant receipts.
  - Implement wallet auto-matching and proportional split-bill calculator.

### 2. Unit Testing
- **`composeApp/src/commonTest/kotlin/com/spendwealthy/app/domain/service/SmartTransactionParserServiceTest.kt`**
  - 10+ test cases covering Indonesian bank SMS, push notifications, itemized bills with PB1 tax, wallet matching, and split-bill math.

### 3. Presentation Layer & DI
- **`composeApp/src/commonMain/kotlin/com/spendwealthy/app/ui/screens/receipt/SmartReceiptScannerViewModel.kt`**
  - Voyager `ScreenModel` managing parsed transaction state, wallet selection, item breakdown, and split-bill allocations.
- **`composeApp/src/commonMain/kotlin/com/spendwealthy/app/ui/components/ReceiptScannerModal.kt`**
  - Upgrade modal to integrate `SmartTransactionParserService` with tabs:
    - Kamera OCR (dengan sample receipts)
    - Tempel Teks SMS / Notifikasi Bank (dengan auto-detect format)
    - Rincian Split-Bill & Salin WhatsApp
- Register service & ViewModel in `KoinModules.kt`.

### 4. Verification
- Run `./gradlew testDebugUnitTest` to guarantee 100% test pass rate.
- Run on iOS Simulator via `./run.sh ios`.
- Capture screenshot verification.
- Git commit & push Milestone 4.
