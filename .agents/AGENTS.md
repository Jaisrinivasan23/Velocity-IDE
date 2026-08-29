# Custom Rules for Velocity-IDE

## UI Design Constraints
- Always design and style frontend elements (React code, UI specs, or Jetpack Compose files) in strict accordance with the Velocity Design System defined in [SKILL.md](file:///c:/Users/tharu/Velocity-IDE/.agents/skills/frontend/SKILL.md).
- Velocity Design System summary: premium minimal dark surfaces (`#0E0F11` / `#141519` / `#1C1E24`), hairline borders (`#2A2D35`), retained orange accent (`#FD5E02`), radius 8/12/16dp, 4dp spacing grid, Material motion 150–250ms, no emojis in UI copy.

## Function-freeze rule (Android app)
- The Android app (`app/src/main/java/com/velocity/ide/`) is a real IDE: terminal output comes from actual shell processes, Git uses JGit, the AI agent runs a real LLM tool loop.
- Never replace a functional implementation with mocks, stubs, or simulated data in the UI layer.
- Presentation code must never change handlers, state writes, or backend calls.
- If an existing backend capability is incomplete, document it (quiet UI copy or the Known gaps section below) — never hide the limitation behind a fake success state.

## Known gaps (documentation only — do not fix silently, do not hide)
- `CheckpointDao` / checkpoint table are used by CheckpointManager (agent-run snapshots); checkpoints list UI is not exposed yet.
- `ui/editor/CodeEditorView.kt` is dead code.
- Home shortcut cards "Fix bug", "Explain Project" and "Deploy" have no-op handlers and are marked SOON in the UI.
- `AgentState.INSPECTING` / `WAITING_FOR_TOOL` are declared but not emitted by the engine.
- On-device local LLM is not implemented (Settings exposes only Gemini/OpenRouter).
- Token usage is not surfaced per run; context budget caps memory block and tool output only.

## Agent behavior (Android)
- Agent loop executes real tools against the sandboxed project folder; memory tools key on the database project id.
- Strict-by-default approval policy: destructive ops and `run_command` always ask unless the matching Settings toggle is disabled; "Always allow" overrides per tool for the run.
- Checkpoints: a zip snapshot is created before each agent run; the agent sheet offers rollback to the pre-run state after a completed run.
- Live streaming text, Stop, Retry, and run history (agent_runs table) are supported.
- Terminal accepts natural language: non-command input surfaces a "Run as AI request" chip routed to the agent.
- Guardrails: max 30 steps, 100 tool calls, 5-minute runtime, 40 KB tool-output truncation.

## Navigation structure (Android)
- Two-mode flow: Home (no bottom bar, hamburger drawer with Deploy + Settings) and Project mode (bottom bar: Workspace · Terminal · Preview · AI), entered by opening/creating a project and exited by leaving Workspace (deselects the project).
- Project-dependent screens show in-app empty states instead of blocking toasts when no project is open.