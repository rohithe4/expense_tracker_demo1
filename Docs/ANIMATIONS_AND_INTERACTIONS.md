# Animations & Interactions

Motion is a first-class citizen in this application. It provides the "Premium" feel requested in the design rules.

## 🎰 Rolling Counter (`RollingCounter.kt`)
**Used in**: Hero Card (Main Balance).

### Logic
- **Odometer Style**: Digits exist on physical reels that "roll" vertically to the target number.
- **Cascading Start**: Each digit has a 60ms stagger delay from its neighbor (left-to-right).
- **Direction**: Always rolls forward into the final cycle for a consistent "loading" feel.
- **Timing**: 800ms duration with `CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)`.

## 🔢 Per-Digit Entry (`RollingAmountDisplay`)
**Used in**: Amount Entry Bottom Sheet.

### Logic
- **Scale + Fade**: As you type on the keypad, digits scale up from 0.5x and fade in from the bottom.
- **Deletion**: Digits scale down to 0.5x and fade out.
- **Layout Stability**: Slots are fixed-width (`tnum`) to prevent the entire amount from "jittering" as digits change.

## 🃏 Hero Reveal
**Used in**: Dashboard on Load.

### Logic
- **Parallel Reveal**: The Hero Card uses a 200ms cross-fade (`AnimatedContent`) as soon as the database emits the first state.
- **Skeletons**: Skeletons use a subtle pulse animation (`infiniteRepeatable` with `tween(1000)`).

## 🗂 Segmented Control Sliding
**Used in**: Transaction Type Toggle.

### Logic
- **Active Pill**: A physical background box that translates horizontally using `animateDpAsState`.
- **Interpolation**: FastOutSlowInEasing over 220ms.
- **Color Morphing**: The pill color animates between Success-Alpha and Error-Alpha tints.

## 🐍 Swipe & Snaky
**Used in**: Transaction rows.

### Logic
- **Physics**: Uses `SwipeToDismissBox` (Compose M3) with custom thresholds.
- **Feedback**: A centered Snackbar with a bold action button for "UNDO".
- **Rule**: Deletion is "Instant in UI, Final on Timer" – the record is withdrawn from the visible list immediately but physically removed from DB after a short delay or on snackbar dismissal.

---
> [!CAUTION]
> **Avoid Jitter**: Always use `fontFeatureSettings = "tnum"` (Tabular Numbers) for any numeric animation to ensure digit widths remain constant.
