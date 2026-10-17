---
name: universal-game-ui
description: 'Design, build, review, and improve UI/UX for any video game, genre, engine, platform, or input method. Use for HUDs, menus, inventory, maps, dialogue, settings, overlays, responsive game UI, safe areas, aspect ratios, controller or touch navigation, accessibility, localization, UI architecture, and game UI testing.'
metadata:
  version: 1.0.0
---

# Universal Game UI/UX

A single, engine-neutral skill for planning, implementing, reviewing, and validating game interfaces across genres, engines, platforms, and input devices. Adapt principles to the actual project; never assume one layout or control scheme fits every game.

## When to Use

Use for any in-game interface or player-facing interaction, including:

- HUDs, status indicators, combat feedback, objectives, notifications, and tutorials.
- Title, pause, settings, save/load, matchmaking, lobby, and results screens.
- Inventory, equipment, crafting, shops, maps, quest logs, journals, and collection screens.
- Dialogue, branching choices, character interactions, and narrative overlays.
- Touch, mouse, keyboard, gamepad, accessibility, rebinding, and cross-device input flows.
- Responsive layout, resolution/aspect-ratio support, safe areas, split-screen, handheld, or VR UI.
- UI architecture, state flow, game-state binding, performance, UX critique, and verification.

This skill covers UI behavior and presentation. Use the project's engine APIs and existing dependencies for implementation; do not assume any particular engine or invent APIs.

## Operating Contract

- Respect the game's brief, genre, fiction, art direction, audience, platform, and existing conventions. A stylized game need not look like a conventional app; clarity and usability still matter.
- Preserve working behavior and visual identity during refinement. For a requested redesign, preserve game rules, content, and platform constraints while committing to one coherent new direction.
- Inspect the real project and target screen before proposing or changing code. Separate observed facts from assumptions.
- Solve the player's task first. Visual effects, animation, and novelty must not obscure state, input, or next action.
- Make changes within scope. Do not change gameplay rules or unrelated systems unless the request requires it.
- Support accessibility and localization in the layout and interaction model, not as a last-minute patch.
- Never claim a device, build, input path, screenshot, or test was verified unless it was actually checked.

## Lifecycle

1. **Initialize:** inspect the target, engine/version, UI framework, input abstraction, game-state/events, target platforms, tests, and relevant art/design docs. Identify player task, screen states, constraints, and what must not change.
2. **Route:** select the closest task path below. Keep one primary path and combine paths only when the feature crosses them.
3. **Implement or review:** use project-native components and patterns; make assumptions explicit when platform or design requirements are missing.
4. **Verify:** run the narrowest meaningful checks, inspect rendered UI, and test the input methods and display conditions relevant to the change.
5. **Report:** summarize the changed behavior, actual checks, and remaining limitations. Do not silently repair unrelated documentation or design-system drift.

If invoked without a concrete task, offer a concise menu of game UI work (design, build, critique, accessibility, responsive layout, input/navigation, or architecture). Do not make unsolicited changes.

## Intent Router

| Player/UI task | Primary focus |
|---|---|
| HUD or gameplay overlay | Information hierarchy, glanceability, event-driven updates, safe placement, combat readability |
| Menus or screen flow | Screen/state model, focus, back/cancel behavior, modal ownership, transitions |
| Inventory, shop, map, journal, or collection | Repeated-item scanning, sorting/filtering, selection details, empty/loading/error states, input parity |
| Dialogue, tutorial, or onboarding | Reading rhythm, choice clarity, pacing, localization, skip/replay behavior |
| Settings, controls, accessibility | Clear categories, immediate feedback, persistence, reset/confirm flows, remapping and device support |
| Mobile or touch UI | Thumb reach, touch targets, gesture conflicts, safe areas, portrait/landscape policy |
| Console, PC, handheld, or TV | Resolution policy, focus navigation, readable distance, controller/mouse coexistence |
| Split-screen or multiplayer | Per-player ownership, focus/input isolation, shared-screen constraints, identity and status clarity |
| VR or spatial UI | Comfort, depth/readability, motion, interaction distance, head/world-space tradeoffs |
| Critique, audit, or polish | Evidence-based findings ordered by player impact; distinguish defects from preferences |

## Design and Implementation Workflow

### 1. Establish player context

Identify the player's immediate goal, game phase, information urgency, session context, audience, platform, control device, and likely viewing distance. Map entry, active, blocked, success, failure, pause, and return states that apply. For multiplayer or split-screen, determine which player owns each control and piece of information.

### 2. Choose the presentation model

Decide whether each element should be persistent HUD, contextual overlay, menu, diegetic/world-space UI, or a combination. Keep critical, time-sensitive information easy to find. Use contextual display when it reduces clutter without hiding information players need to plan.

Define hierarchy by urgency and frequency: critical gameplay state first, current task next, secondary detail after. Group related information, use consistent labels and icon meanings, and avoid presenting every available statistic at once.

### 3. Establish visual direction

Follow the game's established art direction, typography, color language, shapes, materials, and animation style. Use contrast and spacing to distinguish priority and state. Use color redundantly with labels, shape, position, or icons; never make color the only signal. Choose expressive detail appropriate to the game's world without sacrificing legibility or interaction feedback.

Build a deliberate visual grammar instead of defaulting to a stack of identical panels, one accent color on every control, or the same shape and type treatment everywhere. Give high-priority information a clear focal point; vary density and emphasis to match the task. Distinctiveness should come from choices that fit this game, not decoration that competes with play.

Use readable type at the expected screen size and viewing distance. Keep text concise, support scaling where feasible, and design for longer translated strings. Use real game assets and icons from the project when relevant; do not invent branded or lore claims.

### 4. Build resilient layout and scale

- Prefer anchors, constraints, and layout containers over fixed pixel coordinates.
- Define a reference resolution and an explicit policy for other aspect ratios: expand, letterbox, crop only when safe, or reposition anchored regions.
- Keep important information within platform-reported safe areas. Account for notches, rounded corners, overscan, touch-system bars, and platform overlays.
- Establish stable dimensions for HUD regions, icons, focus outlines, counters, and controls so dynamic labels or hover/focus states do not shift neighboring content.
- Test ultrawide, 16:9, 4:3 where supported, portrait/landscape on mobile, handheld screens, and split-screen viewports as relevant.
- For VR/spatial UI, prioritize comfortable scale, distance, depth, motion, and interaction. Avoid forcing rapid refocusing or obstructing the playable view.

### 5. Model navigation and screen state

- Represent screen flow with explicit states or a stack: opening an overlay suspends or layers the underlying screen; closing it restores the correct screen and input owner.
- Define back/cancel behavior for every nested flow. Confirm destructive or irreversible actions; provide a clear recovery path where possible.
- Ensure every controller/keyboard screen has a valid initial focus, predictable navigation, visible focus feedback, and a deliberate focus restore point when returning.
- Support mouse, keyboard, gamepad, touch, and device switching as required by the project. Do not let hover-only behavior replace focus behavior or vice versa.
- Use the existing input abstraction and remapping system. Show current bindings and prevent conflicting or impossible mappings when rebinding is in scope.
- Make focus and selection behavior robust when dynamic items are added, removed, disabled, or filtered.

### 6. Connect UI to game state safely

- Prefer public events, view models, or narrow state interfaces over reaching into unrelated gameplay internals.
- Update HUD elements when relevant state changes; avoid polling gameplay state every frame unless the project has a justified, measured need.
- Define ownership and lifetime for subscriptions, async work, timers, and animations. Unsubscribe or cancel them when a screen closes or its target is destroyed.
- Handle pause/time-scale behavior intentionally: menus, audio, timers, and background gameplay must follow the requested pause policy.
- Keep presentation state separate from authoritative gameplay state. UI feedback must not silently become the source of truth for game rules.
- Handle unavailable data, reconnects, delayed responses, and failure states for networked or service-backed features.

### 7. Cover game-specific interaction patterns

Adapt these patterns to the genre rather than applying them mechanically:

- **Action, shooter, racing, sports:** prioritize glanceable status, timely feedback, unobstructed play space, and clear damage/objective signals.
- **RPG, strategy, simulation, management:** support dense information through grouping, comparison, filters, inspect details, and deliberate navigation.
- **Puzzle, casual, platformer:** make affordances and feedback legible, keep interruption low, and expose hints/help without trapping progress.
- **Narrative, visual novel, adventure:** pace reading and choices, distinguish speaker/context, preserve player control over advance/skip, and support replay where appropriate.
- **Card, board, tactics:** emphasize selection, legal/invalid actions, turn ownership, comparison, and undo/confirmation expectations.
- **Live-service, co-op, competitive:** communicate party/player identity, readiness, connectivity, permissions, match state, and recoverable failures without misleading urgency.

These are prompts for inspection, not mandatory genre checklists. Follow the actual game's mechanics and player research.

### 8. Include accessibility and localization

Consider scalable text, contrast, color-vision differences, non-color state cues, readable subtitles, speaker identification, subtitle background/size, reduced motion, screen shake/flash intensity, input remapping, hold/toggle alternatives, difficulty of timing windows, and one-handed/touch needs where relevant. Let players change supported options without losing progress or focus.

Externalize player-facing text through the game's localization system. Test long strings, pluralization, dynamic values, right-to-left scripts, and font fallback where supported. Avoid baking text into images when it needs translation or scaling.

### 9. Add motion and feedback with purpose

Use transitions and animation to explain hierarchy, causality, selection, progress, or state change. Keep response time appropriate to the game moment; do not delay urgent actions behind decorative motion. Respect reduced-motion options and avoid flashing patterns that may create risk. Keep haptics/audio cues complementary to visual feedback, not required as the only indication.

## Verification Checklist

Run checks relevant to the touched slice; do not claim unsupported coverage.

- **State/flow:** open, close, back, cancel, confirm, pause/resume, transitions, focus restoration, and error recovery behave correctly.
- **Input:** complete the flow with each supported input method, including device switching and controller-only/keyboard-only operation where applicable.
- **Layout:** resize and test representative aspect ratios, safe areas, localization lengths, dynamic values, and text scaling; verify nothing clips, overlaps, or obscures play-critical content.
- **Gameplay integration:** HUD reflects authoritative state changes, does not stale after reconnect/respawn, and does not poll unnecessarily. Subscriptions and async tasks clean up on teardown.
- **Accessibility:** inspect focus visibility, contrast, non-color cues, subtitle/text settings, reduced motion, and remapping relevant to the change.
- **Performance:** avoid unnecessary per-frame work, layout churn, allocations, and excessive UI rebuilds in frequently updated or multiplayer screens.
- **Visual review:** run the game or use current project screenshots/fixtures. Inspect actual rendering at representative display sizes and states; fix clear defects and rerun the focused check.

For a review-only request, list concrete findings first, ordered by player impact, with file/screen references and missing tests. Do not modify code unless asked or clearly implied.

## Engine and Tool Adaptation

Detect the engine and version from project files and neighboring code. Use the existing UI framework, input system, localization, and test conventions. Verify API names against project code or installed documentation before using them. Keep this skill engine-neutral: translate its patterns into the project's actual nodes/components/widgets and avoid copy-pasting engine-specific code from another version.

For Android games whose UI is implemented in Jetpack Compose, follow the project's Material 3 and Compose versions, consume theme tokens, handle `WindowInsets`/edge-to-edge and system bars, preserve touch targets and text scaling, and provide content descriptions for non-text controls. Customize color, typography, and shape to the game's identity instead of shipping untouched component defaults. Do not introduce Compose into a game engine's rendered UI unless the project already uses or explicitly requests that architecture; coordinate any native overlay with engine input, safe areas, and lifecycle.

For new work, build the usable game experience itself rather than a marketing page or explanatory placeholder. Use the project's real visual assets where appropriate. If a running game/editor or screenshot tool is available, inspect rendered output; if not, use tests, fixtures, source inspection, and clearly state the visual verification limitation.

## Completion Report

State the UI/gameplay surface changed, key behavior or design decisions, checks actually run, and any platform/input paths that remain unverified. Keep the report concise and separate verified facts from assumptions.
