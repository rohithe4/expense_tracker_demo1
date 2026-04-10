# Screen-by-Screen Behavior

## 🏠 Dashboard Screen (`DashboardScreen.kt`)
**Purpose**: Central hub for financial overview.

### Layout Logic
- **Hero Card**: Sticky-feel header (though it scrolls). Shows Total Balance in large `RollingCounter`.
- **Skeleton State**: Uses `HomeHeroCardSkeleton` and `RecentTransactionsSkeleton` for perceived performance.
- **Parallel Reveal**: The screen does not wait for all data to load before showing the Hero. It reveals components as their specific data slices arrive.

### Behaviors
- **Balance Update**: When a transaction is added, the balance "rolls" to the new value over 800ms.
- **Swipe-to-Delete**: Swipe any transaction left to reveal a delete action. Deletion triggers an "Undo" snackbar.

---

## 📝 Log Transaction (`AddTransactionScreen.kt`)
**Purpose**: High-speed, high-fidelity data entry.

### Layout Logic
- **Form Groups**: Categorized into Section Type, Amount, Metadata (Merchant, Category), and Extra Notes.
- **Amount Field**: Not a text input. It is a "Tap-to-Expand" trigger for the bottom sheet.

### Key Interactions
- **Transaction Type Toggle**: Sliding animation between Income/Expense. Surface color shifts between Success (Green) and Error (Red) tints.
- **Amount Entry**:
    - Tap Amount -> **`AmountEntrySheet`** opens.
    - Custom Keypad with Haptic Feedback.
    - **`RollingAmountDisplay`**: Digits scale and fade in/out vertically as you type.
- **Smart Date Picker**: Exposed dropdown with "Today", "Yesterday", and "Other" (opens system calendar).

---

## 📋 Recent Transactions (`RecentTransactionsSection.kt`)
**Purpose**: Audit trail of spending.

### Layout Logic
- **Grouping**: Transactions are grouped by date (e.g., "Today", "April 08").
- **Header Style**: Sticky headers (or simulated) with `MetaSm` labels.

### Behaviors
- **Long Press**: Triggers edit mode (navigates to `AddTransactionScreen` with ID).
- **Empty State**: Shows a calm "No transactions yet" message with a secondary CTA button.

---

## 🏗 Template Selection
**Purpose**: One-tap filling for regular expenses.
- Located within the Add Transaction screen.
- Tapping a template (e.g., "Starbucks") auto-fills the Name, Amount, and Category instantly.

---
> [!TIP]
> Use **Compose Navigation** arguments to pass the `transactionId` when moving from Dashboard -> AddTransaction for editing.
