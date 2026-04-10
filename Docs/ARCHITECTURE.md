# Technical Architecture

## 🏛 Package Structure
```text
com.example.expensetrackerdemo
├── data
│   ├── local         # Room Database & DAOs
│   ├── model         # Transaction & Template entities
│   └── repository    # Single source of truth (ExpenseRepository)
├── ui
│   ├── components    # Reusable atoms and molecules (Buttons, Cards)
│   ├── navigation    # NavHost and App Screens definitions
│   ├── screens       # Page-level Composables (Dashboard, Add txn)
│   ├── theme         # Custom design system tokens (Color, Type)
│   └── viewmodel     # State management (ExpenseViewModel)
└── MainActivity.kt   # Entry point & Theme provider
```

## 🧠 State Management
The app uses a **Consolidated State Pattern** (similar to MVI) within the `ExpenseViewModel`.

### Dashboard State
- `dashboardState` is a `StateFlow<DashboardState>`.
- **Optimization**: The `isReady` flag is used to coordinate sequential or parallel reveals.
- Data flows: `allTransactions` -> `allTemplates` -> Map to `DashboardState`.

### Form State
- `AddTransactionScreen` uses **Local MutableState** for form inputs (name, amount, etc.).
- This keeps UI responsiveness high during typing.
- Persistent save only happens on the final button click via `viewModel.addTransaction`.

## 🔄 Data Persistence
- **Room Database**: Asynchronous IO via Kotlin Coroutines (`Suspend` functions).
- **Flow Observation**: The UI observes Room data as `Flows`, ensuring the Dashboard automatically updates when a transaction is added or deleted.

## 🏗 Key Components Architecture
- **HomeHeroCard**: Decoupled from logic; receives raw totals (Balance, Income, Expense).
- **RecentTransactionsSection**: Uses a `LazyColumn` for efficiency. Implements custom swipe-away logic for deletion.
- **RollingCounter**: A custom `DigitReel` based layout using the `Animatable` API for precision odometer-style motion.

---
> [!TIP]
> Always use `hilt` style dependency injection logic if refactoring, though currently manually managed in `MainActivity` for simplicity.
