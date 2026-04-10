# State & Data Flow

## 📊 Data Model
The application uses two primary entities:

### `Transaction.kt`
- `id`: Auto-generated PK.
- `amount`: Double (Stored as absolute value).
- `type`: Int (1 for Income, -1 for Expense).
- `name`: String (Merchant/Title).
- `category`: String.
- `source`: String (Account/Wallet).
- `date`: Long (Timestamp).

### `Template.kt`
- Simplified version of Transaction for auto-filling frequent entries.

---

## 🏗 Repository Pattern (`ExpenseRepository.kt`)
The repository is the central access point for Room. It exposes:
- `Flow<List<Transaction>>`: Reactive stream of all records.
- `Flow<List<Template>>`: Reactive stream of templates.
- Standard CRUD suspend functions.

---

## 🧠 ViewModel Logic (`ExpenseViewModel.kt`)

### Consolidated Dashboard State
The `dashboardState` is computed by combining multiple flows:
```kotlin
val dashboardState = combine(
    repository.allTransactions,
    repository.allTemplates
) { transactions, templates ->
    DashboardState(
        netBalance = transactions.sumOf { it.amount * it.type },
        income = transactions.filter { it.type == 1 }.sumOf { it.amount },
        expense = transactions.filter { it.type == -1 }.sumOf { it.amount },
        recentTransactions = transactions.take(20),
        isReady = true
    )
}.stateIn(...)
```

### Amount Formatting
The `RollingCounter` uses a central `NumberFormat` to handle currency symbols and thousand separators according to `Locale("en", "IN")`.

---

## 🔄 Entry & Edit Flow
1. **Selection**: User taps a transaction on the Dashboard.
2. **Navigation**: `transactionId` is passed to the `AddTransactionScreen`.
3. **Fetching**: The Screen calls `viewModel.getTransactionById(id)` on init.
4. **Binding**: Local `mutableStateOf` variables are populated with the model data.
5. **Updating**: On save, `viewModel.updateTransaction(modifiedModel)` is called.
6. **Reactivity**: Room detects the change, emits a new list, the `dashboardState` flow re-combines, and the UI updates (with `RollingCounter` animating the new totals).

---
> [!NOTE]
> All timestamps are stored as `Long` (Epoch milliseconds). Date formatting for display is handled in the UI Layer using `java.util.Date` and `SimpleDateFormat`.
