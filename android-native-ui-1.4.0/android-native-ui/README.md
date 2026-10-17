# android-native-ui

A reusable Agent Skill for designing, implementing, reviewing, and verifying native Android interfaces — including **game-style UI** (HUD, menus, shop, result screens, buttons, panels, bars, motion) — across many apps. It is not an app-specific theme. It adapts to the actual repository and supports Kotlin/Java, Jetpack Compose/Material 3, classic Android Views/XML, and hybrid View/Canvas/Compose rendering.

## Structure

```text
android-native-ui/
├── SKILL.md                       # operating contract, router, workflow
├── README.md
├── references/
│   ├── 01-repository-discovery.md
│   ├── 02-design-system.md
│   ├── 03-navigation-state.md
│   ├── 04-view-canvas-interop.md
│   ├── 05-accessibility-localization-adaptive.md
│   ├── 06-verification.md
│   ├── 07-sources.md
│   ├── 08-game-ui-craft.md        # HUD, controls, depth recipe, components, screens, polish
│   ├── 09-motion-juice.md         # timing, effects, APIs, haptics, reduced motion
│   ├── 10-style-presets.md        # 5 presets with palettes and checked contrast
│   ├── 11-review-rubric.md        # read-only review: procedure, lenses, severity, report template
│   ├── 12-rendering-game-loop.md  # surfaces, render lifecycle, fixed-step loop, shaders
│   ├── 13-performance.md          # benchmarking, jank, frame budgets, thermal/memory
│   ├── 14-input-controllers.md    # gamepad, joystick, keyboard focus, multi-touch
│   ├── 15-monetization-ads-consent.md # purchase UI, rewarded ads, consent states
│   ├── 16-platform-services-save.md # sign-in, achievements, save schema and sync
│   └── 17-multiplayer-ui-anticheat.md # connection/result UI and server-trust boundaries
└── assets/templates/
    ├── README.md
    ├── compose/                   # Kotlin/Compose templates
    ├── views/                     # Java + XML (drawables, styles, layout) templates
    ├── tests/                     # Compose UI tests and a JVM unit test (not yet run)
    └── scripts/port_check.sh      # read-only porting checklist
```

## Scope and depth

The method layers (discovery, state and navigation, interop, accessibility and localization, verification, review) apply to any native Android UI. The templates remain focused on **game-style UI**. References `12`–`14` cover game runtime, performance, and input; `15`–`17` cover client/UI integration for monetization, platform services/save, and multiplayer status. They do not implement backend security, server-authoritative gameplay, or provider compliance. Ordinary Material 3 app guidance stays at the method level and defers to the project's own components and the official docs.

**Design decision (v1.4.0): one skill, not two, and no rename.** The router in `SKILL.md` already loads `08`–`10` only for game-style requests, so mixed content costs little context. Splitting would either duplicate the shared references or make the game skill depend on another skill being installed. Revisit the split (a core `android-native-ui` plus an `android-game-ui`) if ordinary-app guidance grows to a size comparable to the game material.

## Install

### Claude Code (project-scoped)

Copy the whole `android-native-ui` folder into your project:

```text
.claude/skills/android-native-ui/SKILL.md
```

### Agent setups using `.agents/skills/`

```text
.agents/skills/android-native-ui/SKILL.md
```

Some tools use a different skill path or loading behavior. Follow the agent's current documentation; a folder copied to one location does not guarantee every tool will automatically load it. Keep `references/` and `assets/` beside `SKILL.md`, and keep the folder name equal to the `name` in the SKILL.md frontmatter (`android-native-ui`).

## Example prompts

- “Use `android-native-ui` to audit the home screen and propose the highest-impact UI improvements before editing.”
- “Redesign only this screen, using the app's existing brand, tokens, and components. Preserve business logic and navigation.”
- “Make the main menu and result screen look like a polished casual game. The repo has no visual direction — offer me two or three style presets first.”
- “Restyle our buttons and panels with depth (lip, edge, highlight), pressed/disabled states, and a reduced-motion path.”
- “Add a health bar with a damage trail and a coin counter that counts up, without changing game logic.”
- “Trace why this dialog's Back behavior is inconsistent; fix the state ownership and add a regression test.”
- “Make this Compose screen adapt to compact phone and expanded tablet windows, and respect edge-to-edge insets.”
- “Review the Compose-to-AndroidView bridge for listener leaks, stale state, and frame-time risks.”
- “Review localization (including Vietnamese diacritics), TalkBack semantics, touch sizes, and long-text layout for the feature I changed.”
- “Review this PR's UI changes read-only. Rank findings by severity and confidence, cite files and lines, and tell me what you could not verify.”

## What this skill deliberately does not do

- It does not impose one app's colors, fonts, screens, classes, dependencies, or business/game rules on another project. Style presets are examples used only when the repository has no visual direction.
- It does not require Compose or Material 3 when the app uses another native stack.
- It does not authorize changing backend/business/gameplay logic during a visual-only task.
- It does not assert that a build, device, screenshot, or integration test succeeded unless it was actually run. The templates under `assets/templates/` have not been compiled and must be built and tested in the target project.

## Reference set

Version 1.4.0 adds references `12`–`17` for rendering/game-loop architecture, performance, controllers/input, monetization and consent UI, platform services/save data, and multiplayer/anti-cheat UI boundaries. The new sections are guidance, not verified integrations; re-check current provider policies and SDK/version compatibility in the target project.


`references/` contains detailed workflows for repository discovery, visual design systems, navigation and state, custom View/Canvas interop, accessibility/localization/adaptive layout, verification, source references, game UI craft, motion/feedback, style presets, and review/audit, game rendering/performance/input, monetization UI, platform services/save data, and multiplayer UI boundaries.
