---
trigger: always_on
---

# Finance App Design Rule

Apply these design rules to every screen unless I explicitly override them.

## Design direction
- The app should feel calm, premium, minimal, and modern.
- Avoid loud, flashy, overly colorful, or generic AI-looking UI.
- Prefer soft surfaces, clear hierarchy, and spacious layouts.

## Visual style
- Use card-based UI.
- Cards must feel visually separated from the page background using surface color contrast, subtle border, and soft shadow.
- The page background should be a soft neutral tone, not pure white if cards are white.
- Avoid heavy gradients, neon glows, and overly decorative effects.
- Keep the interface clean and elegant.

## Typography
- Use Figtree for the UI.
- Create strong hierarchy using size, weight, and spacing.
- Keep labels small and neat.
- Keep headings clear and bold.
- Avoid random font sizes.
Use simple, consistent finance language throughout the app.

## Rules
- Use the same term everywhere for the same concept.
- Prefer plain English over jargon.
- Keep labels short, clear, and easy to understand.
- Do not introduce alternate names for the same thing.
- Reuse the app's chosen terms in UI text, docs, prompts, and code comments.

## Examples
- Use one term for money added: `Income` or `Credit`, not both unless the difference is intentional.
- Use one term for money spent: `Expense` or `Debit`, not both unless the difference is intentional.
- Use one term for total available money: `Balance` or `Net Income`, not both unless the difference is intentional.
- Use one term for categories: `Category`, not `Tag` / `Type` / `Label` mixed randomly.

## Writing Standard
- If a finance term is technical, pair it with a plain explanation the first time it appears.
- Do not rename terms casually in different screens.
- If a new term is needed, add it to the glossary before using it.

## Review Check
Before submitting any UI text, prompt, or code:
- Is the term already used elsewhere in the app?
- Is it simple enough for a first-time user?
- Will this confuse income vs expense, credit vs debit, or balance vs total?
- Did I keep the wording identical across the app?

## Priority
Consistency > Cleverness.
Clarity > Finance jargon.

## Components
- Prefer rounded corners and clean card containers.
- Use grouped cards for transaction sections instead of many disconnected floating cards.
- Recent transactions should usually appear inside one main card with dividers between rows.
- Buttons should feel clear and polished, not bulky.

## Buttons
- Primary buttons use black background with white text unless I specify otherwise.
- Secondary buttons should be quiet and neutral.
- Avoid too many competing button styles on one screen.

## Tabs and motion
- Segmented controls should use a smooth sliding active pill.
- When switching tabs, the active indicator should move smoothly.
- Do not show ugly click highlight, dark pressed box, ripple, or tap flash on tabs.
- Motion should feel refined, quick, and subtle.

## Lists and layout
- Long content must never be clipped.
- Pages should scroll correctly when content exceeds the viewport.
- Add enough bottom spacing so the last item is always visible.
- Keep spacing consistent and balanced.

## Quality bar
- Preserve good visual hierarchy.
- Keep alignment clean.
- Keep spacing consistent.
- Do not redesign unrelated parts when changing one component.
- Make improvements feel intentional and high quality.