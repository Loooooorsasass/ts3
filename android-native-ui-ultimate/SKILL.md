---
name: android-native-ui-ultimate
description: 'Repository-first Android game UI engineering for Kotlin/Java, Compose, Views, Canvas, and hybrid projects. Use for HUDs, menus, inventory, shops, input/focus, motion/rendering, accessibility, localization, lifecycle, performance, billing/service UI, multiplayer status, testing, and release review. Inspect the actual project and verify claims.'
metadata:
  version: 2.0.0
---

# Android Native UI Ultimate

Repository-first engineering for Android game UI and related native app surfaces. Supports Kotlin/Java, Compose, Views/XML, Canvas, and hybrid rendering without imposing a framework or visual theme. References `15`–`17` cover client/UI boundaries only; they do not implement billing compliance, backend security, server authority, matchmaking, or anti-cheat systems.

## Start Here

1. Inspect repository instructions, build/version files, active screen, state owner, tokens, strings, lifecycle, and nearby tests before proposing edits. Start with `references/01-repository-discovery.md`.
2. Identify the requested task and load only its workflow below plus required domain references. Keep observed facts separate from assumptions.
3. Define behavior to preserve, acceptance checks, and the narrowest useful test before editing. Choose the existing project architecture and installed API versions.
4. Make the smallest maintainable change. Do not change gameplay rules, data ownership, renderer, billing, networking, or security boundaries unless the request requires it.
5. Run the narrowest relevant check, then expand only as needed. Report exact commands, results, and unverified areas; never infer visual/device correctness from source inspection.

## Task Router

Always retain the baseline concerns: repository discovery (`01`), accessibility/localization/adaptive layout (`05`), verification (`06`), and evidence-based review (`11`) when reviewing. The task workflow defines when and how to load the more detailed domain references.

| Task | Workflow | Domain references |
|---|---|---|
| New screen, HUD, inventory, shop, menu | `references/workflow-new-ui.md` | `02`, `03`, `08`, `10` as relevant |
| UI bug, regression, refactor | `references/workflow-debug-refactor.md` | `01`, `03`, `04`, `06`, `11` |
| Animation, Canvas, surface, game loop, jank | `references/workflow-motion-performance.md` | `04`, `09`, `12`, `13` |
| Accessibility, input, controller, localization | `references/workflow-accessibility-input.md` | `05`, `14` |
| Billing, ads, save, platform services, multiplayer | `references/workflow-services-multiplayer.md` | `03`, `15`, `16`, `17` |
| Test strategy, release review, skill/template validation | `references/workflow-test-release.md` | `06`, `11`, `07` |

## Project Adaptation

Detect language, modules, Gradle/AGP/Kotlin versions, Compose or Views usage, renderer, state/navigation, dependencies, min/target SDK, CI, and test conventions. Read the version catalog and nearby implementations before choosing APIs. If the repository is not Android, do not migrate it; apply only transferable discovery and UX principles. If current official guidance or installed APIs are uncertain, consult authoritative documentation and state what was checked.

Preserve product identity and game rules. Favor clear hierarchy, meaningful states, stable geometry, project tokens, localization, readable focus, and motion that explains state. Do not prescribe one architecture, palette, dependency, frame rate, or rendering path. Game UI recipes and template defaults are starting points, not requirements; measure and adapt them.

## Evidence Contract

- **VERIFIED**: the named check ran successfully; include command, environment, and scope.
- **PARTIALLY VERIFIED**: only a stated portion ran; name what is missing.
- **UNVERIFIED**: no reproducible check was run. Source review is not a build, screenshot, device, accessibility, performance, billing, or security test.

Use `references/06-verification.md` for project work and `docs/verification-report.md` for this bundle's evidence. Keep secrets out of source, reports, fixtures, and logs. The bundle's license boundary is in `LICENSE` and `docs/source-audit.md`.
