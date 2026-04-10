# Work Log & Decisions

This log captures the reasoning behind unconventional or significant design and architectural choices.

## 📅 Decision Log

### 1. Amount Entry as a Bottom Sheet
- **Context**: Standard numeric text fields are clunky on mobile and often show the generic system keyboard.
- **Decision**: Use a custom `ModalBottomSheet` with a tailored numeric keypad.
- **Rationale**: Provides consistent sizing, haptic feedback, and allows for the premium "Rolling Amount" animation which is impossible with standard `TextField`.

### 2. Grouped Transaction Cards
- **Context**: Showing 20 individual cards with shadows creates "visual noise" and clutter.
- **Decision**: Group transactions by day inside one large white surface card with a subtle border.
- **Rationale**: Improves focus on the content and aligns with the "Calm" design rule in `finance-app-design.md`.

### 3. Parallel Reveal Dashboards
- **Context**: Waiting for Room DB to aggregate 1,000 transactions before showing the screen causes a blank white flash.
- **Decision**: Reveal the Hero Card and Transactions independently as data becomes available.
- **Rationale**: Perceived performance is more important than absolute load time. The "Skeleton" pulse keep the user engaged during the 200ms-400ms delay.

### 4. Tabular Numbers (`tnum`)
- **Context**: In most fonts, "1" is narrower than "8". This causes standard text to "wiggle" as amounts roll.
- **Decision**: Force `fontFeatureSettings = "tnum"` on all counters.
- **Rationale**: Essential for visual stability in high-motion UI components.

## 🛠 Revision History
- **v1.0**: Initial Room setup and fragments.
- **v2.0**: Migrated to 100% Jetpack Compose.
- **v2.1**: Implemented `RollingCounter`.
- **v2.2**: Optimized Dashboard for parallel reveal (Performance Sprint).

---
> [!NOTE]
> We decided **against** using a third-party charting library. All charts will be custom-drawn using Compose `Canvas` to ensure they match the specific "Soft Surface" aesthetic.
