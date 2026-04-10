# Project Handoff: Master Onboarding

Welcome to the **Expense Tracker Demo** repository. This document is optimized for AI agents and developers to quickly reach productive state without breaking the established premium patterns.

## 🏁 Quick Overview
- **Status**: Core functionality (Dashboard, Transaction logging, Templates) is implemented.
- **Goal**: A premium, minimalist, local-only expense tracker with 100% Jetpack Compose.
- **DNA**: Quality over quantity. Interactions must feel intentional and smooth.

## 🗺️ Task-Based Documentation Index
*Read the relevant file before starting any specific task:*

| Task Category | Documentation to Read |
| :--- | :--- |
| **UI/UX & Styling** | `UI_DESIGN_SYSTEM.md`, `ui-ux-pro-max.md` |
| **Business Logic & Data** | `ARCHITECTURE.md`, `STATE_AND_DATA_FLOW.md` |
| **Animations & Motion** | `ANIMATIONS_AND_INTERACTIONS.md`, `SCREEN_BY_SCREEN_BEHAVIOR.md` |
| **Troubleshooting & Fixes** | `KNOWN_ISSUES_AND_GOTCHAS.md`, `WORK_LOG_AND_DECISIONS.md` |

## 🚀 First Hour Onboarding Checklist
- [ ] **Read this file** and the `finance-app-design.md` workspace rule.
- [ ] **Inspect Dashboard Brain**: `app/src/main/java/com/example/expensetrackerdemo/ui/viewmodel/ExpenseViewModel.kt`.
- [ ] **Understand Animation Core**: `app/src/main/java/com/example/expensetrackerdemo/ui/components/RollingCounter.kt`.
- [ ] **Observe Parallel Reveal**: Run the app and watch how hero cards and lists reveal asynchronously.

## 🚫 The "Sacred" Rules (Never Break)
- **Local-Only**: No network calls or 3rd-party cloud libraries.
- **Parallel Reveal**: Do not introduce blocking code in `DashboardState` that delays transaction list rendering.
- **No Ripple Flash**: Interactive elements (tabs, pills) must use custom sliding/alpha motion, NEVER default Material ripples.
- **Typography Consistency**: Only use `Figtree` and strictly follow the hierarchy in `UI_DESIGN_SYSTEM.md`.

## 🛠️ Workflow Checklists

### **Before You Edit**
- [ ] Check `KNOWN_ISSUES_AND_GOTCHAS.md` to see if your area has known pitfalls.
- [ ] Ensure you understand the state management (unidirectional data flow) for that screen.
- [ ] Check if a custom component already exists (e.g., `PremiumButton`, `RollingAmountDisplay`).

### **Before You Mark Done**
- [ ] **Performance**: Ensure animations are 60fps and lists scroll smoothly.
- [ ] **Visuals**: Verify no "ugly" default Android highlights or clipping on long content.
- [ ] **Alignment**: Check that spacing matches the 8dp grid system.
- [ ] **States**: Verify empty, loading, and error states for any new screens.

---
> [!TIP]
> Use **`NEXT_STEPS.md`** to find the current development priority.
