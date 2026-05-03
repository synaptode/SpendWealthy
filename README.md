# SpendWealthy 🐾✨💎

**SpendWealthy** is a state-of-the-art, premium financial tracking application built with **Kotlin Multiplatform**. It combines high-end fintech aesthetics with a sophisticated "High-End Pounce" design language, offering a state-of-the-art experience for managing personal wealth across Android and iOS.

---

## 🌟 Key Features

### 🏛 High-End Dashboard
A premium overview of your financial status. Features a "Total Balance" summary with visibility toggles, high-impact metrics, and quick-access transaction feeds.

### 💳 Card Carousel & Management
Manage your digital and physical cards through a sleek, glassmorphic carousel. Support for multiple card types (Debit, Savings, Credit) with intuitive detail views.

### 📊 Advanced Analytics & Insights
- **Smart Charts**: Custom-drawn bar charts for income and expense trends.
- **Spending Patterns**: AI-driven insights (Pounce Patterns) to help you optimize your cash flow.
- **Category Breakdown**: visual distribution of expenses across custom categories.

### 💸 Rapid Financial Actions
- **Quick Send**: Horizontal contact bar for instant peer-to-peer transfers.
- **Top Up**: Streamlined workflows for adding funds to your accounts.
- **Transaction Ledger**: A high-precision list of all your financial "pounces" with detailed merchant and category tagging.

### 🛡 Secure Vault
A dedicated, encrypted space for storing sensitive financial documents, certificates, and ID files, ensuring your critical data is always within reach and protected.

### 🎯 Goal Tracking (Treat Goals 🐟)
Visual budgeting system that allows you to set targets for major purchases or savings milestones, with real-time progress tracking and motivational micro-animations.

---

## 🚀 Tech Stack

- **Language**: Kotlin 2.x
- **Multiplatform Framework**: Kotlin Multiplatform (KMP)
- **UI Engine**: Compose Multiplatform
- **Local Persistence**: SQLDelight (Driver-based local-first storage)
- **Navigation**: Voyager (Native-feeling stack and tab navigation)
- **Dependency Injection**: Koin
- **Serialization**: KotlinX Serialization
- **Concurrency**: Kotlin Coroutines & Flow
- **Architecture**: Clean Architecture (Data -> Domain -> UI) with MVVM pattern

---

## 🏗 Project Structure

```text
.
├── composeApp/                 # Shared Kotlin Multiplatform module
│   ├── src/
│   │   ├── commonMain/         # Core shared logic
│   │   │   ├── kotlin/
│   │   │   │   ├── data/       # Repositories & Data Sources
│   │   │   │   ├── domain/     # Use Cases & Models
│   │   │   │   ├── ui/         # Screens & Components
│   │   │   │   └── theme/      # Design System (Forest Green & Neon Lime)
│   │   │   └── sqldelight/     # Database Schema & Migrations
│   │   ├── androidMain/        # Android-specific platform code
│   │   └── iosMain/            # iOS-specific platform code
├── iosApp/                     # Native iOS wrapper project (Xcode)
└── gradle/                     # Build configuration
```

---

## 🏁 Getting Started

### Prerequisites
- **Android Studio Koala+** or **IntelliJ IDEA Ultimate**
- **Xcode 15+** (for iOS builds)
- **Kotlin Multiplatform Plugin** installed in IDE
- **JDK 17+**

### Installation

1. **Clone the repository**:
   ```shell
   git clone https://github.com/yourusername/spendwealthy.git
   cd spendwealthy
   ```

2. **Sync Gradle**:
   Open the project in Android Studio and wait for the Gradle sync to complete.

3. **Run on Android**:
   Use the `composeApp` run configuration or:
   ```shell
   ./gradlew :composeApp:installDebug
   ```

4. **Run on iOS**:
   Open `iosApp/iosApp.xcworkspace` in Xcode and press `Cmd + R`.

---

## 🛠 Advanced Configuration

### Database Migrations
SQLDelight migrations are handled in `commonMain/sqldelight`. To update the schema:
1. Create a new `.sqm` file in the migrations folder.
2. Update the `.sq` file with the new table definitions.
3. Run `./gradlew generateSqlDelightInterface`.

---

## 🗺 Roadmap

- [ ] **Multi-Currency Support**: Automated FX conversion for global travelers.
- [ ] **Shared Accounts**: Joint "Treat Stashes" for family budgeting.
- [ ] **Cloud Sync**: Secure, end-to-end encrypted backup of vault data.
- [ ] **Biometric Lock**: FaceID and Fingerprint integration for the Secure Vault.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

We welcome contributions! Please read our [Contributing Guidelines](CONTRIBUTING.md) before submitting PRs.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

**SpendWealthy** — *Pounce on your financial freedom.* 🐾✨