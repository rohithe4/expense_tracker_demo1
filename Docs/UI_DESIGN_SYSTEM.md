# UI Design System: Visual Guidelines

This app follows a **Minimalist Premium** aesthetic. Every component should feel soft, stable, and intentional.

## 🎨 Color Palette (`Color.kt`)
The color system is designed for high contrast text on soft, neutral surfaces.

- **Background (`ColorBg`)**: `#EFEFED` (Soft off-white)
- **Surface (`ColorSurface`)**: `#FFFFFF` (Pure white for cards)
- **Text (`ColorText`)**: `#1A1917` (Deep charcoal, never pure black)
- **Muted Text (`ColorTextMuted`)**: `#7A7874`
- **Primary Accent (`ColorPrimary`)**: `#01696F` (Deep Teal)

## 🔠 Typography (`Type.kt`)
- **Primary Font**: **Figtree** (Imported via Google Fonts/XML).
- **Scale**:
    - **TitleLg**: 28sp-32sp, Bold (Used for balances/amounts).
    - **BodyLg**: 18sp, Regular (Main items).
    - **MetaSm**: 12sp, All-Caps, Tracking 0.12.sp (Section labels).

## 💳 Card Structure
Avoid the generic "Material Card".
- **Corners**: 20dp-24dp rounded corners.
- **Borders**: 1dp width, `ColorBorder` (very subtle).
- **Grouping**: Recent transactions must be grouped inside ONE main card with dividers, not as individual floating cards.

## 🔘 Interactive Elements

### AppButton (`AppButton.kt`)
- **Primary**: Black background (`ColorButtonPrimary`), white text. 16dp rounded.
- **Secondary**: Soft border (`ColorButtonSecondaryBorder`), no background.

### Segmented Control
- Used for Income/Expense switching.
- **Behavior**: Smooth sliding "active pill" that changes color based on selection (Green for Income, Red for Expense).
- **Rule**: Never use default material ripples on this control.

## 📐 Spacing & Layout
- **Gutter**: 20dp horizontal padding on all screens.
- **Section Spacing**: 24dp vertical spacing between major sections.
- **Bottom Spacing**: Always add `40.dp + navigationBarsPadding()` at the bottom of scrollable views to ensure the last item is never clipped.

---
> [!IMPORTANT]
> **No Emojis as Icons**: Use SVG icons from Lucide/Heroicons or Material Icons with specific tints. Emojis break the "Premium" minimalist feel.
