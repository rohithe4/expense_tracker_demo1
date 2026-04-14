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
 
 1. Typeface strategy
- Use one primary UI typeface consistently.
- Limit the number of typefaces.
- Use system-friendly or app-approved typefaces for clarity and performance.
- Keep numerals tabular where values animate or align.

2. Hierarchy
- Define a small, strict type scale.
- Use size, weight, and spacing to separate title, body, caption, and metadata.
- Avoid too many text levels.
- Headings should be clearly distinct from body text.

3. Readability
- Body copy should be easy to scan on small screens.
- Avoid overly thin weights for important text.
- Maintain good contrast.
- Prefer concise labels and short sentences.

4. Line height / leading
- Set line height deliberately, not by default.
- For mobile body text, line height should typically be around 1.5x to 1.7x the font size depending on the context.
- For headings, tighter leading is usually better.
- Multi-line titles should not feel cramped, but should stay visually grouped.

5. Spacing around text
- Keep title-to-body spacing intentional and consistent.
- Use vertical rhythm across cards, toasts, dialogs, rows, and sheets.
- Paragraph spacing should be larger than line spacing.
- Avoid random gaps between stacked text blocks.

6. Alignment and rhythm
- Align text to a predictable grid.
- Keep baseline rhythm consistent inside cards and form fields.
- Use consistent paddings so text blocks feel balanced.

7. Letter spacing / tracking
- Use letter spacing sparingly and intentionally.
- All-caps labels may need small tracking.
- Avoid excess tracking on body text.
- Keep numeric displays stable.

8. Accessibility
- Support larger text sizes cleanly.
- Preserve legibility at high font scales.
- Do not rely on color alone to convey meaning.
- Keep touch and reading comfort in mind.

9. Motion and typography interaction
- Text should not jitter during animation.
- Use stable widths for changing numbers.
- Prefer tabular figures where values animate.
- Avoid layout shifts when text updates.

10. Mobile-specific content rules
- Keep labels short.
- Prefer clear nouns and verbs.
- Use sentence case where appropriate.
- Avoid decorative text styles that reduce legibility.

11. Numeric and finance text
- Use tabular figures for amounts.
- Keep decimals and currency symbols visually stable.
- Do not let animated digits reflow surrounding text.
- Ensure numbers remain readable during transitions.

12. Component-specific rules
- Cards: title, body, and metadata must have a consistent hierarchy.
- Toasts/snackbars: compact title/body spacing, readable line height.
- Bottom sheets: large amount displays should remain crisp and centered.
- Forms: labels, helper text, and values should each have a distinct role.   

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

Use established mobile spacing best practices:
- 4-point / 8-point spacing systems
- consistent spacing primitives
- clear hierarchy through spacing
- comfortable touch targets
- vertical rhythm and breathing room
- predictable card and component spacing
- spacing tuned for mobile readability


## Spacing system
- Use a strict spacing scale based on 4dp increments.
- Prefer values like 4, 8, 12, 16, 20, 24, 32, 40, 48.
- Avoid arbitrary values unless visually necessary.
- Use named spacing tokens rather than raw numbers in code.

## Hierarchy by space
- Tight spacing for closely related items: 4–8dp.
- Medium spacing inside components: 12–16dp.
- Section spacing between groups: 20–24dp.
- Major page separation: 32dp or more.
- Bigger gaps should signal a new idea or new section.

## Card spacing
- Card internal padding: typically 16dp or 20dp.
- Space between stacked items inside a card: 8–12dp.
- Space between cards: 12–16dp.
- Keep card content away from edges so it breathes.
- Do not let text or icons touch card boundaries.

## Form spacing
- Label to field gap should be small and consistent, usually 8dp.
- Field to helper/error text should be compact and clear, 4–8dp.
- Space between form groups should be 16–24dp.
- Keep related controls visually grouped.

## Toast/snackbar spacing
- Vertical padding inside the toast should be balanced and compact.
- Title to body spacing should be deliberate and consistent.
- Toasts should sit above bottom navigation / system bars with safe-area padding.
- Do not make the container feel cramped or floating randomly.

## List spacing
- Rows inside a list should have enough space to scan quickly.
- Use dividers or spacing consistently, not both randomly.
- Keep dense finance lists readable without wasting vertical space.
- Grouped list sections should have clear separation from one another.

## Screen padding
- Default screen horizontal padding: 16dp or 20dp.
- Use consistent left/right margins across screens.
- Top and bottom safe areas must be respected.
- Avoid screen-edge crowding.

## Touch target spacing
- Interactive controls should have enough surrounding space to avoid accidental taps.
- Minimum touch target should remain comfortable for thumbs.
- Dense layouts should still maintain clear hit areas.

## Vertical rhythm
- Spacing should create a predictable visual rhythm through the app.
- Text, icons, cards, and controls should align to repeated spacing steps.
- Related content should feel grouped; unrelated content should feel separated.






---
> [!IMPORTANT]
> **No Emojis as Icons**: Use SVG icons from Lucide/Heroicons or Material Icons with specific tints. Emojis break the "Premium" minimalist feel.
