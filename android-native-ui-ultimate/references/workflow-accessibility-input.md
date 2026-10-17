# Workflow: Accessibility, Input, and Localization

## Activate when

Adding or changing semantics, touch targets, TalkBack behavior, focus, keyboard/gamepad, joystick/multi-touch, reduced motion, text scaling, translation, RTL, or adaptive layouts.

## Inspect before editing

Read project accessibility/localization guidance, UI semantics and focus graph, input abstraction and remapping, string/resource setup, supported API levels, window/inset policy, motion preferences, and tests. Load `05` and `14`; add `03` for navigation and `09` for animated interactions.

## Procedure

1. Identify supported users, devices, inputs, languages, and display conditions from repository/product evidence. Do not promise unsupported modes.
2. Trace each action through touch, keyboard/controller, semantics, and focus. Define initial focus, confirm/cancel/back, focus restoration, pointer ownership, and disconnect/cancel behavior.
3. Ensure meaningful labels/roles/state/value, visible non-color state cues, usable target areas, focus indication, and text that scales and wraps. Keep decorative visuals out of the accessibility tree.
4. Keep visible and spoken values derived from the same authoritative state. Localize strings and format numbers with locale-aware APIs; check long text, pluralization, font fallback, and RTL where supported.
5. Respect reduced motion for both new and in-flight animations. Offer a non-animated equivalent without losing state information.

## Choose the solution

Prefer Compose semantics/focus APIs or View accessibility APIs already used by the project. Keep Android-specific events at the input adapter and emit existing intents. Avoid hard-coded translations, raw event handling in unrelated controls, color-only signals, and blanket system-gesture exclusions.

## Verify

Run semantic/action tests and locale/resource checks. Inspect font scale and compact/expanded windows. Test each claimed input path independently, including focus transitions, cancel/disconnect, and multi-touch cancellation. Use TalkBack/Switch Access on a device or emulator for platform behavior; automated semantics tests alone do not prove assistive-technology usability.

## Accept when

Every required action has a discoverable equivalent, state is perceivable without color alone, focus and text remain usable in supported configurations, and the named automated/manual checks pass.

## Evidence boundary

Do not claim WCAG/Android accessibility conformance, TalkBack usability, controller coverage, or locale support based only on source review. Name tested API/device/input/locale and remaining gaps.