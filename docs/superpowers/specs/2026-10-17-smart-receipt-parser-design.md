# Design Spec: Smart Offline Receipt & Transaction Parser ("Scan Struk & Auto-Import SMS")

## Executive Summary
One of the primary friction points in daily personal finance is manual expense logging. Typing nominal values, merchant names, dates, and picking wallets repeatedly leads to user churn.
SpendWealthy introduces the **Smart Offline Receipt & Transaction Parser Engine (Milestone 4)**:
1. **Zero-Latency Offline Parser**: 100% on-device heuristic & regex token parser for Indonesian banking SMS (BCA, Mandiri, BRI, BNI), E-Wallets (GoPay, OVO, ShopeePay, DANA), QRIS receipts, and physical restaurant/store receipts.
2. **Itemized Receipt Breakdown with Tax & Service Extraction**: Parses individual line items, quantities, subtotal, government tax (PB1 / PPN 10-11%), and restaurant service charges (5-10%).
3. **Smart Split-Bill ("Bagi Bon Adil")**: Enables users to distribute line items to friends/family with proportional tax & service charge calculation, generating a ready-to-copy WhatsApp breakdown.
4. **Intelligent Wallet & Category Auto-Matching**: Automatically pairs the parsed transaction with the user's matching wallet (e.g. "BCA", "GoPay") and standard categories.

---

## 1. Domain Models (`SmartReceiptModels.kt`)

```kotlin
enum class ParsingSource {
    BCA_SMS,
    MANDIRI_LIVIN,
    BRI_BRIMO,
    BNI_WONDR,
    GOPAY_PUSH,
    OVO_PUSH,
    SHOPEEPAY_PUSH,
    DANA_PUSH,
    QRIS_STATIC_DYNAMIC,
    PHYSICAL_RECEIPT,
    FREE_TEXT
}

data class ReceiptLineItem(
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Long,
    val totalPrice: Long = quantity * unitPrice
)

data class SplitBillAllocation(
    val participantName: String,
    val itemIndices: List<Int>,
    val subtotal: Long,
    val taxAndServiceShare: Long,
    val totalToPay: Long
)

data class ParsedTransaction(
    val merchantName: String,
    val amount: Long,
    val dateText: String,
    val source: ParsingSource,
    val suggestedCategory: String,
    val matchedWalletId: String? = null,
    val matchedWalletName: String? = null,
    val items: List<ReceiptLineItem> = emptyList(),
    val subtotal: Long = amount,
    val tax: Long = 0L,
    val serviceCharge: Long = 0L,
    val referenceId: String? = null,
    val rawText: String = "",
    val confidence: Float = 0.9f
)
```

---

## 2. Core Service (`SmartTransactionParserService`)

- **Bank Pattern Parsers**:
  - `parseBca(text: String)`
  - `parseMandiri(text: String)`
  - `parseBri(text: String)`
  - `parseBni(text: String)`
  - `parseEWallet(text: String)`
  - `parseItemizedReceipt(text: String)`
- **Wallet Auto-Matcher**:
  - Matches detected keywords (`bca`, `mandiri`, `gopay`, `ovo`, `dana`, `cash`) against the user's active wallet list.
- **Split-Bill Engine**:
  - Allocates tax and service charge proportionally according to each participant's item subtotal.
  - Generates WhatsApp summary text.

---

## 3. UI Integration

- Reusable in `AddTransactionScreen` via the "OCR" button.
- Reusable in `HomeScreen` via Quick Action / Direct shortcut.
- Enhanced `ReceiptScannerModal`:
  - Tab 1: Live simulated receipt scanner with quick-presets.
  - Tab 2: Clipboard paste & real-time banking SMS parser.
  - Tab 3: Interactive Split-Bill tool with WhatsApp share.
