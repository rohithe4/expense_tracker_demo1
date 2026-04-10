# Next Steps: Project Roadmap

This list contains prioritized tasks for the next phase of development.

## 🔴 High Priority (Immediate)
- **[BUG]**: Fix the "Empty State" button on the Dashboard; it currently doesn't navigate to the `AddTemplate` screen correctly.
- **[REFACTOR]**: Move categories and account lists into a dedicated `Constants.kt` file instead of hardcoding them in `AddTransactionScreen`.
- **[OPTIMIZATION]**: Add `remember` to the `SimpleDateFormat` in `SmartDateField` to prevent object allocation during state changes.

## 🟡 Medium Priority (New Features)
- **Search Functionality**: Implement a top search bar on the Dashboard to filter transactions by name or category.
- **Real Analytics**: Replace the placeholder graph icons on the Hero Card with actual custom-drawn sparklines using `Canvas`.
- **Transaction Export**: Add a "Export to CSV" feature found in the settings (Settings screen currently non-existent).

## 🟢 Low Priority (Polish)
- **Multi-Currency Support**: Allow users to change the default "₹" prefix.
- **Dark Mode Refinement**: The current theme is heavily based on `ColorBg` (`#EFEFED`). Create a corresponding `ColorBgDark` for a seamless dark-mode experience.
- **Micro-interactions**: Add a subtle bounce effect to the FAB (Add Transaction button) when it first appears.

## 🧪 Validation Checklist
Before marking any task as done:
- [ ] UI matches `finance-app-design.md` exactly.
- [ ] No performance regressions on the `RollingCounter`.
- [ ] Build completes without `ksp` errors.
- [ ] Tested on both standard and narrow-screen emulators.

---
> [!TIP]
> Start with the search functionality as it's the most requested user feature.
