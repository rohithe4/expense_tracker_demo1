# Known Issues & Gotchas

A list of practical "traps" and incomplete areas to be aware of when working on this repo.

## ⚠️ Performance & Re-composition
- **Heavy ViewModels**: The `DashboardState` combine logic runs on every small database change. If the transaction count grows to 1,000+, this mapping logic might become a bottleneck. 
- **Solution**: Use `transactions.asSequence()` for mapping or move heavy computations to a background dispatcher.
- **Hero Card Jitter**: If `RollingCounter` is passed a changing `Double` too frequently, the animations might interrupt each other. Ensure the state doesn't emit redundant updates with identical values.

## 🎨 Visual Regressions
- **Border Clipping**: On some low-DPI emulators, the 1dp border on cards might disappear or look uneven. Prefer `thickness = (0.5).dp` or `1.dp` specifically with `alpha = 0.07f`.
- **Keyboard Overlap**: The `AmountEntrySheet` is a `ModalBottomSheet`. Ensure that navigation bars are handled via `navigationBarsPadding()` so the "Confirm" button isn't hidden by the system's pill navigation.

## 🧩 Amount Sheet "0" Bug
- **Issue**: Attempting to delete the last digit might leave an empty string, which causes an "Invalid Format" error on confirm.
- **Prevention**: The logic in `AmountEntrySheet` should always ensure that an empty string is treated as `0.00` or disabled the confirm button.

## 🔄 Transaction Deletion Logic
- **Gotcha**: Swipe-to-delete is implemented using `SwipeToDismissBox`. If the user swipes very fast, Compose might clear the row before the "Undo" data is captured in the ViewModel.
- **Safety**: Always capture the `Transaction` object *before* calling the delete function in the VM.

## 📱 Device Specifics
- **Haptics**: `HapticFeedbackType.TextHandleMove` is used for the keypad. On cheap vibration motors, this might feel like a generic long buzz. Test on a Pixel or Samsung for the intended tactile "click".

---
> [!WARNING]
> **Do not use `Modifier.clickable` with ripples on cards.**
> Use a custom `interactionSource` with `indication = null` to maintain the premium feel, or use a very soft, tailored shadow shift instead.
