---
name: android-native-ui
description: 'Design, implement, review, and verify polished native Android interfaces for Kotlin/Java apps using Jetpack Compose, Material 3, classic Android Views, or hybrid Compose/View/Canvas rendering. Use for screen design, UI architecture, navigation, design systems, responsive/adaptive layouts, accessibility, localization, custom drawing, game HUDs/controls, game menus/shop/pause/result screens, game-style buttons/panels/progress bars, motion and haptic feedback, style presets for projects without a visual direction (giao diện game), testing, and UI polish. Inspect each repository first; never assume a specific app, brand, architecture, color palette, or rendering engine.'
metadata:
  version: 1.4.0
---

# Android Native UI Engineering

A repository-first skill for Android UI. It covers ordinary apps and game-like apps built with Kotlin/Java, Jetpack Compose/Material 3, Android Views, custom Canvas, or a mix. It is a set of engineering and design methods, not a visual theme. References `15`–`17` describe client/UI integration boundaries only; they do not implement or replace billing compliance, backend security, matchmaking, server authority, or anti-cheat systems. The project's code, design language, and constraints are the source of truth.

## Scope

Use it for native Android UI design, implementation, review, redesign, navigation, dialogs, forms, HUDs, custom drawing, responsive layouts, accessibility, localization, and UI tests.

**Depth of coverage.** The method layers apply to every Android UI: discovery (`01`), state and navigation (`03`), interop (`04`), accessibility and localization (`05`), verification (`06`), and review (`11`). The most developed domain guidance, and all of `assets/templates/`, is for game-style UI (`08`–`10`), with additional game-runtime, performance, input, monetization, save/service, and multiplayer UI references (`12`–`17`). For ordinary Material 3 apps, rely on the project's own components and the official Material 3 and Compose documentation (`07`); `02` gives the method for a design system, not a component catalogue. When a request falls outside the developed areas, say so and lean on the repository and the official docs rather than inventing detail.

Do not assume Compose or Material 3. Detect the actual stack. For Flutter, React Native, Unity, Godot, or web projects, keep only the discovery and UX principles and follow that stack's own architecture. Never rewrite a project into Compose unless asked.

## Operating contract

1. **Inspect before editing.** Read the target screen, its callers, the state owner, the navigation entry, tokens, strings, reusable components, and nearby tests. Read Gradle files and the version catalog before choosing APIs.
2. **Separate facts from assumptions.** Mark what you observed, what you assumed, what you recommend, and what you did not verify. Never claim you ran a command or opened a file you did not.
3. **Protect behavior.** A visual task must not change game rules, persistence, data models, network, billing, ads, auth, permissions, or analytics. Explain any behavior change.
4. **Preserve working architecture.** Do not replace a custom renderer, navigation, state approach, or design system because another pattern is fashionable.
5. **Avoid generic output.** No universal palette, one radius everywhere, a wall of identical cards, accent color on everything, or decorative motion. Choose direction from the product and existing assets. For concrete alternatives see `references/08` and `references/10`.
6. **Use project tokens.** Prefer existing theme values, strings, and components. Change shared tokens deliberately rather than scattering literals.
7. **Make the UI usable.** Clear hierarchy, meaningful states, contrast, semantics, safe-area handling, localization, and layouts that survive text expansion.
8. **Keep changes scoped.** No unrelated refactors or new dependencies when an existing API works.
9. **Verify the slice.** Build, test, and inspect rendering when tools exist. If not, state the limit. Reading source is not a visual or device test.
10. **Protect secrets.** Never copy credentials, signing passwords, tokens, private endpoints, or user data into code, logs, or reports.

## Intent router

**Mandatory baseline for every UI task:** load `01` (repository discovery), `05` (accessibility/localization/adaptive layout), and `06` (verification). The table below adds specialist references; it does not replace the baseline. For a narrowly scoped question, skim only the relevant baseline sections, but preserve all three concerns in the plan and handoff.

| Request | Load | Primary work |
|---|---|---|
| Improve or redesign one screen | `01`, `02`, `05`, `06` | Inspect state flow, define hierarchy, reuse tokens, verify narrow viewport and behavior |
| New screen or feature | `01`, `03` | Find analogous screens, state contracts, strings, and tests first |
| UI bug | `03` | Isolate the state or constraint, trace ownership, patch the smallest layer, add a regression test |
| Theme or design system | `02`, `10` | Audit tokens, set palette/type/shape/spacing rules, migrate consistently |
| Navigation, dialog, Back | `03` | Map states, Back/cancel/confirm semantics, focus restoration |
| Game-style UI (menus, HUD, shop, result, buttons, panels, bars) | `08`, `10` if no visual direction, `09` | Brief, token spec, implement with templates, render, critique |
| Rendering engine, game loop, surface, shaders | `12`, `04`, `06` | Preserve the current renderer; review lifecycle, fixed-step timing, surface/context recreation, frame pacing, and render hot-path allocation |
| Performance, jank, startup, memory, battery, thermal | `13`, `06` | Measure a representative journey first; use traces/benchmarks and record device/build context |
| Keyboard, gamepad, joystick, multi-touch, focus | `14`, `05`, `03` | Normalize platform events to intents; test disconnects, focus, pointer identity, and accessibility |
| Billing, shop, rewarded ads, consent/privacy options | `15`, `03`, `05`, `06` | Model asynchronous UI states; never grant entitlement from a tap or ad dismissal; verify provider policies |
| Sign-in, achievements, leaderboard, save/sync | `16`, `03`, `06` | Define ownership, offline/error/retry behavior, schema migration, and conflict policy |
| Multiplayer status, reconnect, result submission, integrity messages | `17`, `03`, `05`, `06` | Keep UI truthful and accessible; backend authority and anti-cheat decisions remain server-side |
| Animation, feedback, rewards, haptics | `09` | Motion that explains state, reduced-motion path, rapid-change tests |
| Custom canvas, View, game controls | `04` | Preserve renderer contract, check drawing, hit tests, lifecycle |
| Localization, accessibility, adaptive layout | `05` | Real locales, font fallback, semantics, insets, window sizes |
| Review or audit | `11`, `06`, plus the lens references `11` points to | Read-only. Findings ranked by severity and confidence, with file references; check tests and ported templates for assertions that cannot fail |

## Workflow

1. **Discover.** Read the build files, manifest, entry point, UI surface, state and data owners, strings, tests, and CI. Check whether code is active before calling it legacy: search manifests, navigation, deep links, reflection, and DI. See `01`.
2. **Set constraints.** Write the goal, non-goals, the main action on the screen, durable state, supported Android versions and window sizes, and integrations that must not change. Make a conservative assumption when a safe path exists. Ask only when a missing decision blocks the work.
3. **Design with intent.** Give the screen one focal point. Use semantic tokens, a clear type scale, and meaningful accents. Cover loading, empty, error, disabled, selected, offline, and permission-denied states. See `02`.
4. **Keep state correct.** The UI renders state; the existing ViewModel or state holder owns business-critical data. Handle async work, cancellation, and stale callbacks. Avoid two mutable sources of truth. See `03`.
5. **Respect navigation and input.** Define Back at each layer, confirm destructive actions, and check gesture conflicts with scrolling, system gestures, and custom canvases. See `03` and `04`.
6. **Handle window conditions.** Use the project's inset APIs for edge-to-edge, cutouts, and the IME. Design for the app window size, not a device name. See `05`.
7. **Accessibility and localization from the start.** Semantic controls, 48 dp targets, non-color cues, string resources, long and right-to-left text, Vietnamese diacritics. See `05`.
8. **Implement the smallest maintainable change.** Reuse components. Add shared abstractions only for real repetition. See `02`.
9. **Verify and report.** Run the narrowest checks first, then the broader ones. Use the handoff template in `06`, and state exactly what was and was not run.

## Game UI and templates

`references/08-game-ui-craft.md` covers HUD zones, touch controls, the surface and depth recipe, component anatomy, screen blueprints, typography, performance, and a polish checklist. `references/09-motion-juice.md` covers timing, effects, haptics, and reduced motion. `references/10-style-presets.md` has five presets with contrast values for use when the project has no visual direction.

`assets/templates/` has Compose and View starting points for game buttons, panels, bars, count-up numbers, outlined text, press feedback, and reward bursts, plus Compose UI tests (including tests that assert on real animated values), a JVM unit test, and `scripts/port_check.sh`. Read `assets/templates/README.md` before using them. They have **not been compiled**. Port them, replace placeholder tokens, check version-dependent APIs, and run the tests before reporting them as working.

## Verification

- "Source reviewed" means files were inspected.
- "Compiled" or "tests passed" means the named command ran and succeeded.
- "Visually checked" means rendered output was inspected, from a screenshot or device.
- Passing a build does not prove visual quality, accessibility, billing, auth, or device coverage.

Commands and their details are in `references/06-verification.md`.

## References

- [01 — Repository discovery and architecture](references/01-repository-discovery.md)
- [02 — Design system and visual quality](references/02-design-system.md)
- [03 — Navigation, state, and interaction](references/03-navigation-state.md)
- [04 — Native View, Canvas, and Compose interop](references/04-view-canvas-interop.md)
- [05 — Accessibility, localization, and adaptive layout](references/05-accessibility-localization-adaptive.md)
- [06 — Verification and handoff](references/06-verification.md)
- [07 — Sources and official resources](references/07-sources.md)
- [08 — Game UI craft](references/08-game-ui-craft.md)
- [09 — Motion, feedback, and juice](references/09-motion-juice.md)
- [10 — Style presets](references/10-style-presets.md)
- [11 — Review and audit rubric](references/11-review-rubric.md)
- [12 — Rendering architecture and game loop](references/12-rendering-game-loop.md)
- [13 — Performance, frame budgets, and thermal behavior](references/13-performance.md)
- [14 — Input, controllers, and focus navigation](references/14-input-controllers.md)
- [15 — Monetization, ads, and consent UI](references/15-monetization-ads-consent.md)
- [16 — Platform services, sign-in, achievements, and save data](references/16-platform-services-save.md)
- [17 — Multiplayer status UI and anti-cheat boundaries](references/17-multiplayer-ui-anticheat.md)
