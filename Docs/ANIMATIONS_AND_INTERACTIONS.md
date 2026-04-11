---
name: mobile-design-eng
summary: Mobile design engineering skill for Android and Flutter apps focused on polished UI, motion quality, interaction correctness, and production-ready implementation details.
source_inspiration: Adapted from Emil Kowalski-style design engineering principles and tailored for Android Jetpack Compose and Flutter.
platforms:
  - Android (Jetpack Compose)
  - Flutter
***

# Mobile Design Engineering Skill

This skill is a mobile-first adaptation of a design engineering philosophy focused on polish, motion, responsiveness, and the invisible details that make apps feel premium.

It is intended for:
- Android apps built with Jetpack Compose.
- Flutter apps built with widgets, implicit animations, and explicit animation controllers.
- Consumer mobile apps where interaction feel matters, especially finance, productivity, tools, utilities, and messaging.

It is **not** a direct web-to-mobile translation. The goal is to preserve the philosophy while replacing web-specific guidance with mobile-native implementation patterns.

***

## Initial response

When this skill is invoked without a specific question, respond only with:

> I'm ready to help you build mobile interfaces that feel polished, responsive, and physically believable across Android and Flutter.

Do not provide anything else until asked.

***

# Core philosophy

## Taste is trained

Good mobile UI is not just clean layout and good colors. It is the feeling that every action behaves the way the user expects. Taste is built by repeatedly studying well-crafted apps, slowing down interactions, and noticing what feels natural versus what feels synthetic.

## Invisible details matter more on mobile

Mobile UI is touched directly. Because the hand is involved, any mismatch in timing, weight, or feedback becomes obvious much faster than on desktop. Tiny errors in easing, delay, touch response, and animation staging compound into an app that feels cheap.

## Motion should explain, not decorate

Animation should communicate:
- where something came from,
- what changed,
- whether the app heard the interaction,
- how one state relates to another.

If animation exists only because it “looks cool,” remove it unless it is rare and meaningful.

## Physicality beats theatricality

Most mobile UI should feel physically plausible rather than flashy. Use motion that suggests mass, damping, elasticity, and continuity. Avoid cartoon bounce unless the product explicitly calls for playful branding.

***

# Review format

When reviewing UI code, always use a markdown table in this exact structure:

| Before | After | Why |
| --- | --- | --- |
| `tween(300)` on button press | `tween(120, easing = FastOutSlowInEasing)` | Press feedback should feel immediate |
| `scale = 0f` on enter | `scale = 0.94f, alpha = 0f` | Elements should not appear from nothing |
| Animation on sheet preload | Snap to final state on reopen | Existing data should not re-animate |
| Live formatting while typing | Raw typing, format on confirm | Prevents character-diff animation bugs |

Never use loose Before/After bullet lists when reviewing code.

***

# Animation decision framework

Before animating anything, answer these questions in order.

## 1. Should this animate?

| Frequency | Decision |
| --- | --- |
| 100+ times/day | Avoid animation or make it nearly instant |
| Tens of times/day | Keep animation minimal |
| Occasional | Standard animation is fine |
| Rare or celebratory | Can add more delight |

Examples:
- Keyboard/dialpad entry: micro animation only.
- Tab switch: fast and almost invisible.
- Pull to refresh: subtle physicality is fine.
- First-run onboarding: can be more expressive.

## 2. Why is it animating?

Valid reasons:
- Feedback.
- State transition clarity.
- Spatial continuity.
- Physical plausibility.
- Reducing jarring state changes.

Invalid reason:
- “Because animation looks nicer.”

## 3. What motion type fits?

| Situation | Preferred motion |
| --- | --- |
| Tap feedback | quick scale, tiny opacity change |
| Enter/exit small element | scale + fade |
| Sheet, modal, panel | translate + fade |
| Gesture-following UI | spring-based / physics-based |
| Value changes | per-digit or per-part transitions only |
| Decorative movement | spring with restraint |

## 4. How fast should it be?

| Element | Duration |
| --- | --- |
| Press feedback | 80–140ms |
| Digit entry/delete | 100–180ms |
| Chips, segmented controls | 120–180ms |
| Menus, popovers, tooltips | 140–220ms |
| Bottom sheets | 220–420ms |
| Dialogs | 180–320ms |
| Pull-to-refresh micro response | tied to gesture/spring, not fixed duration |

Rule: most core UI animations should feel done within about 300ms unless they are gesture-driven or intentionally explanatory.

***

# Motion principles for mobile

## Immediate response matters most

The first visible response should happen immediately after the user acts. If there is a lag before anything starts, the app feels broken even if the full animation is beautiful.

Examples:
- Button should compress instantly on touch down.
- Digit should begin entry animation as soon as it is typed.
- Pull-release micro reaction should happen at the visual settle moment, not after an extra programmed delay.

## Match animation to touch

On mobile, every animation is judged against the user’s finger. If the motion starts too late, overshoots too much, or ignores gesture velocity, it feels fake.

## Preserve continuity

If a value changes, animate only the part that changed whenever possible.
If content is preloaded, snap to the stable state instead of replaying entry motion.
If formatting characters are causing layout bugs, separate raw input from formatted display.

## Prefer asymmetry

Fast response in, clean settle out.
- Press-in should be immediate.
- Release should be slightly softer.
- Exit should often be faster than entry.

***

# Easing and springs

## Android Compose guidance

Recommended defaults:
- `spring()` for gesture-linked and physically suggestive motion.
- `tween()` for short deterministic transitions.
- Avoid slow starts for direct interactions.

Good mental defaults:
- Entry: fast-out feel.
- Exit: slightly faster.
- Movement on-screen: fast-out-slow-in or spring.

Useful Compose patterns:
- `spring(dampingRatio = 0.8f, stiffness = 500f)` for crisp, premium micro motion.
- `spring(dampingRatio = 0.7f, stiffness = 300f)` for softer physical response.
- `tween(120)` for tap feedback.

## Flutter guidance

Recommended defaults:
- `Curves.easeOut` for most entry.
- `Curves.easeInOut` for on-screen movement.
- `SpringSimulation` or spring-based package/controller patterns for physically believable motion.
- `AnimatedScale`, `AnimatedSlide`, `TweenAnimationBuilder`, and explicit controllers where precision is required.

## Avoid these mistakes

- Slow ease-in on UI entry.
- Large bounce in serious apps.
- Springs with huge stiffness that look robotic.
- Programmed delay after a visible event.
- Simultaneous fade-crossfades between overlapping states when character-level precision matters.

***

# Component principles

## Buttons

Buttons must acknowledge touch immediately.

Recommended behaviors:
- Slight scale down on press, around `0.97–0.99`.
- Optional tiny opacity shift.
- Release should recover quickly.

Avoid:
- No pressed state.
- Large shrink.
- Delayed press reaction.

## Bottom sheets

Bottom sheets should feel attached to the bottom edge and responsive to drag.

Rules:
- Preloaded content should appear already settled, not animate in from scratch on reopen.
- Drag should include damping at limits.
- Dismiss/expand should reflect gesture velocity when possible.
- Avoid over-animating sheet internals during initial open.

## Lists and cards

Cards in motion should preserve hierarchy.
- Use subtle movement and opacity.
- Avoid bouncy reorder animations in serious contexts.
- Swipe actions should reveal controls with spatial logic, not random popping.

## Tabs and segmented controls

Switches should feel immediate.
- Keep animation very short.
- Never let motion make the tab feel slower to use.
- Active indicator motion should be smooth but restrained.

***

# Numeric input and amount-display rules

This section is especially important for finance apps.

## Raw input vs formatted display

Never force live formatting into the same animation pipeline if it causes character instability.

Preferred model:
- Keep a raw editable numeric string during entry.
- Animate only the digits while typing.
- Apply final formatting when the user confirms, blurs, or lands in a non-editing display state.

This prevents bugs such as:
- commas replacing digits,
- disappearing characters,
- unstable keys,
- weird center-expansion on reopen.

## Per-digit animation

Animate only changed digits.

Recommended behavior:
- Digit entry: scale up from bottom-center. No fade. Bottom-center anchor (TransformOrigin 0.5f, 1f).
- Digit delete: scale down to bottom-center. No fade. No slide. Pure scale only.
- Keep digit slot width stable.
- Use tabular numbers if typography allows.

Avoid:
- Sliding all digits on every change.
- Crossfading the entire amount string.
- Re-animating unchanged digits.
- Treating formatting commas as if they are digits.

## Initial digit special case

The transition from placeholder/zero state to the first entered digit must be treated separately.

Rules:
- Do not crossfade placeholder zeroes against real digits.
- Do not let zeroes split outward from center.
- The first entered digit should use a deliberate scale-up transition.
- Deleting back to the zero state should use a dedicated reverse transition.

## Indian number system

For Indian finance apps:
- If live comma insertion causes animation instability, show raw digits while editing.
- Format on confirm for final display: `12,34,567`.
- Use consistent formatting in summaries, hero cards, transaction rows, and read-only fields.

***

# Gesture design

## Pull to refresh

Pull-to-refresh should feel like one connected physical event.

Rules:
- The dragged container should move as one layer.
- Resistance should increase as pull distance grows.
- Release should spring back with a believable settle.
- Secondary micro-reactions, such as digits jumping inside a card, should happen at the visual impact moment.
- Never add an obvious dead gap after the container settles.

## Swipe actions

Swipe should feel anchored.
- Reveal controls from the edge they belong to.
- Keep the original item partially visible when appropriate.
- Use velocity as part of dismiss logic.
- Add friction rather than hard stops.

## Drag interactions

When dragging beyond limits:
- allow overscroll with damping,
- reduce movement ratio progressively,
- avoid hard walls unless functionally required.

***

# Performance rules

## Animate the right properties

Prefer animating:
- translation/offset,
- scale,
- opacity,
- clip/shape where cheap and appropriate.

Avoid animating:
- layout size on every frame when a transform could achieve the same feeling,
- expensive blur over large surfaces,
- full recompositions caused by poorly scoped animation state.

## Compose-specific guidance

- Keep animation state local to the smallest composable possible.
- Avoid diffing entire formatted strings when per-token identity is needed.
- Use stable keys for animated character lists.
- For gesture-linked animation, prefer `Animatable`, spring, and direct state mapping over indirect delayed triggers.
- Avoid triggering animations from stale `remember()` assumptions when the UI depends on updated state.

## Flutter-specific guidance

- Avoid rebuilding large widget trees for tiny animation changes.
- Use `AnimatedBuilder`, `ValueListenableBuilder`, or isolated animated widgets where possible.
- Prefer local animation state to broad `setState()` cascades.
- Be careful with text layout changes during animated formatting.

***

# Accessibility

## Reduced motion

Reduced motion does not mean no feedback. It means less travel and less physical displacement.

Keep:
- opacity changes,
- color changes,
- short state fades,
- subtle scale if necessary.

Reduce or remove:
- large movement,
- springy motion,
- gesture exaggeration,
- decorative parallax.

## Readability

Animated values must remain legible during motion.
For finance amounts, readability is more important than flair.

## Touch comfort

Do not make users wait through animation to continue input.
Typing, tapping, and editing should always feel immediate.

***

# Debugging checklist

When an animation feels wrong, inspect in this order:

1. Is there a delay before the first visible response?
2. Are unchanged elements re-animating?
3. Are two visual states overlapping unnaturally?
4. Is the wrong anchor point being used?
5. Is a generic transition being used where a special-case transition is needed?
6. Is formatting logic interfering with animation identity?
7. Is the spring too stiff, too soft, or too bouncy?
8. Is the animation tied to mathematical completion instead of visual settle?
9. Is preload state being mistaken for new content?
10. Is the issue only visible on a real device and not the emulator?

Always test complex motion on real hardware.

***

# Android implementation notes

## Jetpack Compose recommended tools

Use when appropriate:
- `animate*AsState` for simple property changes.
- `AnimatedVisibility` for controlled visibility changes.
- `AnimatedContent` only when content identity is truly changing and overlap is acceptable.
- `updateTransition` for coordinated multi-property state changes.
- `Animatable` for precise gesture-linked and sequential motion.
- `spring()` for physically believable movement.

Guidelines:
- Avoid using `AnimatedContent` for tightly controlled digit-by-digit formatting transitions unless keys and identity are well-defined.
- For per-digit transitions, model digits and formatting tokens explicitly when necessary.
- For preloaded sheet reopen, snap to final stable state on first composition.

***

# Flutter implementation notes

## Recommended tools

Use when appropriate:
- `AnimatedContainer` for simple container transitions.
- `AnimatedScale`, `AnimatedOpacity`, `AnimatedSlide` for focused implicit motion.
- `TweenAnimationBuilder` for lightweight custom transitions.
- `AnimationController` + `CurvedAnimation` for precise sequencing.
- Physics simulations or spring packages for gesture-linked reactions.

Guidelines:
- Separate editable/raw value state from formatted/read-only value state.
- Use keyed widgets carefully so digit identity remains stable.
- Avoid rebuilding the entire amount string if only one digit changes.

***

# Mobile review checklist

| Issue | Fix |
| --- | --- |
| No press feedback | Add immediate scale-down or opacity response |
| Entry starts from nothing | Start from `0.94–0.97` scale with opacity 0 |
| Generic fade between structured numeric states | Animate only changed digits or dedicated state transitions |
| Live formatting causes visual bugs | Separate raw edit state from formatted display state |
| Reopen animates preloaded content | Snap to stable loaded state on first composition |
| Gesture reaction happens after delay | Tie reaction to visual settle/impact moment |
| Spring feels robotic | Lower stiffness, tune damping |
| Motion feels floaty | Increase stiffness or reduce travel |
| Unchanged digits animate | Preserve stable identity and animate only changed slots |
| Too much bounce in serious app | Reduce bounce and overshoot |

***

# Guidance for the current finance app

For the current expense-tracker style app, default to these decisions:
- premium, not playful,
- subtle motion over flashy motion,
- raw amount editing in the dialpad,
- formatted amount on confirm,
- quick scale-up/scale-down per digit,
- no unnecessary live formatting animation,
- physically believable pull-to-refresh response,
- stable preloaded values on reopen,
- zero weird overlap between placeholder state and first typed digit.

If unsure, choose the option that improves clarity, stability, and perceived quality rather than the option that adds more visible motion.