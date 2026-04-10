---
trigger: always_on
---

# Project Continuation Rule

Use project documentation efficiently and preserve existing conventions.

## Documentation usage
- Treat `Docs/HANDOFF_MASTER.md` as the primary onboarding and source-of-truth file.
- Do not read the entire `Docs/` folder by default.
- Read only the documentation files relevant to the current task.
- Use `Docs/ui-ux-pro-max.md` only when a task needs deeper UI/UX reference beyond the app-specific docs.

## Task workflow
- Start each new task by reading `Docs/HANDOFF_MASTER.md`.
- From that file, identify which additional docs are needed.
- Read only those relevant files before editing.
- Do not load unrelated docs.

## Editing behavior
- Preserve the documented design language, architecture, interaction patterns, and motion rules.
- Do not redesign unrelated parts.
- Make targeted changes only.
- Before changing a major pattern, check the relevant documentation first.

## Token efficiency
- Keep context lean.
- Do not repeatedly reread large files in the same task unless necessary.
- Use fresh chats for separate tasks.