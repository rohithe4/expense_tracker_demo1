# Project Overview: Expense Tracker Demo

## 🎯 App Purpose
The **Expense Tracker Demo** is a minimalist personal finance application designed to prove that technical utility can co-exist with premium, high-end aesthetics. It focuses on local-only speed, privacy, and delightful interactions.

## 🚶‍♂️ Core User Flow
1. **View Dashboard**: Check net balance, see categorized income/expense bars.
2. **Add Transaction**: High-speed entry using custom numeric keypad and rolling counter feedback.
3. **Manage Templates**: Quickly re-use transaction patterns (e.g., "Daily Lunch", "Rent").
4. **Edit/Delete**: Long-press or swipe to manage transactions with immediate undo capability.

## 📦 Glossary of Terms
| Term | Description |
| :--- | :--- |
| **Hero Card** | The main balance display at the top of the dashboard. |
| **Rolling Counter** | A custom component that "rolls" numeric digits like an odometer. |
| **Amount Sheet** | The bottom sheet used for entering currency values. |
| **Parallel Reveal** | The UI optimization where sections reveal as soon as data is ready. |
| **Segmented Toggle** | A custom sliding control for Income/Expense selection. |

## 🛠 Feature Status
| Feature | Implementation | Notes |
| :--- | :--- | :--- |
| Dashboard | ✅ Complete | Includes Hero Card and Recent Transactions Grouping. |
| Add Transaction | ✅ Complete | Uses Bottom Sheet Amount entry with haptics. |
| Edit Transaction | ✅ Complete | Reuse same screen as "Add". |
| Swipe Actions | ✅ Complete | Swipe-to-delete with Undo Snackbar. |
| Room DB | ✅ Complete | Local repository pattern with Flow observation. |
| Templates | ✅ Complete | Fill form from saved templates. |
| Search | ⏳ Ongoing | Basic structure planned; no UI yet. |
| Analytics | ⏳ Planned | Hero card placeholders; real charts pending. |

---
> [!NOTE]
> This app is strictly **Local-Only**. Data persistence is handled via Room Database located in the `data/local` package.

---
[Master Index](file:///c:/Users/ROHITH/AndroidStudioProjects/ExpenseTrackerDemo/docs/HANDOFF_MASTER.md) | **Next Step**: [Setup & Run](file:///c:/Users/ROHITH/AndroidStudioProjects/ExpenseTrackerDemo/docs/SETUP_AND_RUN.md)
