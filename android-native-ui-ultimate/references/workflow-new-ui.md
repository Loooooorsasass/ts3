# Workflow: New UI

## Activate when

Creating or substantially redesigning a player-facing screen or component: HUD, inventory, shop, menu, pause/result, settings, reward, or navigation flow.

## Inspect before editing

Read repository instructions, module and toolchain versions, active route/screen, neighboring implementations, game phase and player goal, state/data owner, tokens/assets, strings/locales, supported windows/input, and tests. Record behavior and systems that must not change. Load `01`, `02`, `03`, `05`, `06`, and `08`; add `10` only if direction is missing, `14` for controller focus, and service references only for the corresponding integration.

## Procedure

1. State the player task, primary action, entry/exit/back behavior, relevant states, and acceptance checks. Separate facts from assumptions.
2. Map information by urgency and frequency. For HUD, keep play-critical information glanceable and the playfield readable. For inventory/shop, make comparison, selected details, availability, and recovery states clear.
3. Establish a brief visual direction and map primitives to existing semantic/component tokens. Specify typography roles, layout constraints, states, focus, and motion. Do not impose web breakpoints or CSS tokens on native UI.
4. Choose the existing Compose, Views/XML, Canvas, or hybrid implementation boundary. Keep business/game state in its current owner; the UI renders it and emits intents.
5. Implement one vertical slice, including loading/empty/error/disabled/selected states that apply. Externalize strings and preserve long-text, RTL, and font-scaling behavior where supported.
6. Render and inspect the actual target states where a runnable path exists. Refine only defects against the brief and acceptance criteria.

## Choose the solution

Prefer project components, assets, state holders, and navigation. Use custom drawing only where it serves the established art direction or a measured need. Keep stable dimensions for dynamic labels and focus rings. Templates are optional starting points, not defaults; read `assets/templates/README.md` before porting.

## Verify

Run a focused state/unit test, relevant Compose/View interaction test, and screenshot comparison if configured. Inspect representative compact/expanded or portrait/landscape windows, font scaling, localized long copy, safe insets, touch/focus, and accessibility. Verify state changes originate from the authoritative owner.

## Accept when

The stated player task is clear; required states and actions work; existing behavior and architecture are preserved; strings, semantics, focus, and layout constraints are handled; and all named checks pass. If no render/device path exists, explicitly leave visual behavior UNVERIFIED.

## Evidence boundary

Do not claim visual polish from source or previews not rendered. Do not claim TalkBack, gamepad, device, billing, network, or performance behavior without the corresponding executed check.