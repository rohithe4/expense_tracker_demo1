# Agent Continuation Rulebook (INTERNAL)

This document is for the **AI Assistant** (Antigravity). Follow these rules to ensure the project maintains its "UI/UX Pro Max" quality.

## 🤖 Pre-Action Checklist
1. **Read `HANDOFF_MASTER.md`**: Always start here if this is a new session.
2. **Inspect The Theme**: Never hardcode colors. Open `ui/theme/Color.kt` and use the existing tokens (`ColorBg`, `ColorPrimary`, etc.).
3. **Verify Typography**: Check `ui/theme/Type.kt`. Ensure you are using `BodyLg`, `TitleLg`, or `MetaSm` styles. Avoid generic `MaterialTheme.typography` defaults unless they are mapped correctly.

## 💅 Design Enforcement
- **Soft Shadows Only**: If you add elevation, use a very low value (2dp-4dp) or a subtle 1dp border with alpha.
- **No Clipped Content**: If you add a screen, ensure it's wrapped in a `.verticalScroll(rememberScrollState())` if there's any chance content exceeds the screen.
- **Consistent Corners**: Cards = 24dp, Buttons = 16dp. Do not mix.

## 📝 Code Conventions
- **StateFlow vs State**: Use `StateFlow` in ViewModels for persistent state. Use `mutableStateOf` in UI for transient interactions (like form inputs).
- **Previews**: Every new UI component MUST have a `@Preview`. If you can't preview it, it's likely too coupled with business logic.
- **Component Reuse**: Check `ui/components` before building a new UI element. `AppButton` and `HomeHeroCard` cover most generic needs.

## 🚀 Prompting Guidelines
- **Be Incremental**: If asked to "Add Search", first build the Search UI, then the Filter logic, then the empty state. Don't do it all in one 200-line edit.
- **Validate Performance**: If you change the `DashboardScreen`, double check that the `isReady` logic still allows parallel reveal.

## 🏁 Quality Checklist (Before marking task done)
- [ ] Did I use `RollingCounter` for large currency numbers?
- [ ] Is the spacing consistent with the 20dp gutter rule?
- [ ] Did I test the screen with a "back" button/interaction?
- [ ] Is the logic local-only (no network calls)?

---
> [!IMPORTANT]
> **Design Consistency is non-negotiable.** If the code works but looks "Unfinished" or "Generic Android", the task is NOT done.
