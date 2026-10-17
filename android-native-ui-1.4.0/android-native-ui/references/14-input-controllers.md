# 14 — Input, controllers, and focus navigation

Input is a translation layer between device events and game/UI intents. Keep platform-specific event handling at the edge and map events into actions such as `Confirm`, `Cancel`, `MoveSelection`, `MoveAim`, or `PointerDown`. Do not let unrelated UI components independently interpret the same raw event stream.

## Input routing architecture

- Normalize input in one owner or adapter, then dispatch intents to the active screen/game state.
- Define input priority when a dialog, text field, pause menu, or gameplay surface is active.
- Cancel held/repeating input when the relevant device disconnects, window loses focus, Activity pauses, or the active screen changes.
- Track source device and event time where repeat rate, latency, or diagnostics matter.
- Avoid treating every keyboard or controller event as a touch click. Support keyboard users and controllers without requiring a touchscreen.

## Gamepad and joystick events

Android commonly reports digital buttons through `KeyEvent` and analog axes through `MotionEvent` from joystick/controller sources. Check the event's source and device capabilities rather than assuming every `MotionEvent` is a thumbstick. See [Handle game controllers](https://developer.android.com/develop/ui/views/touch-and-input/game-controllers).

For analog input:

1. Read axes using the axis constants and event/device capabilities relevant to the controller.
2. Apply a configurable dead zone to suppress hardware drift.
3. Normalize magnitude and clamp output so downstream movement does not exceed the expected range.
4. Consider a radial dead zone for two-dimensional sticks when preserving direction matters; independent X/Y dead zones can distort diagonal response.
5. Test triggers, D-pads, stick clicks, multiple controller models, reconnect, and controllers with different axis ranges.

Illustrative radial dead-zone normalization for an already normalized two-axis vector:

```kotlin
fun applyRadialDeadZone(x: Float, y: Float, deadZone: Float): Pair<Float, Float> {
    val magnitude = kotlin.math.sqrt(x * x + y * y)
    if (magnitude <= deadZone || magnitude == 0f) return 0f to 0f
    val scaled = ((magnitude - deadZone) / (1f - deadZone)).coerceIn(0f, 1f)
    val factor = scaled / magnitude
    return (x * factor) to (y * factor)
}
```

Clamp or validate `deadZone` to the interval `[0, 1)` in production code and account for controllers whose values are not normalized. This helper handles only a two-axis vector; triggers and digital buttons need separate mapping.

## Menu focus and accessibility

Controller support is not complete until the player can navigate menus. Define a visible focus indicator, a predictable initial focus target, directional movement, wrap/no-wrap rules, and restoration when a dialog closes. Support Enter/Space and a gamepad confirm/cancel mapping consistent with platform expectations. Avoid a focus trap when changing tabs or opening transient UI.

Compose should use focus APIs and semantics rather than raw key listeners wherever possible. Views should expose focusable controls and meaningful accessibility descriptions. Test with keyboard, D-pad, screen reader, and touch independently. A focus outline must remain visible against every preset/background.

## Multi-touch and pointer identity

For touch controls, use pointer IDs rather than assuming pointer index `0` remains the same finger. Pointer indices may change as fingers are added or removed; IDs remain associated with a pointer for its lifetime. Track ownership per pointer for virtual stick, fire, drag, and UI gestures. A pointer released from one control must not accidentally release another control's held state.

Define gesture ownership and cancellation for: system back/edge gestures, scroll containers, drag-and-drop, overlays, loss of window focus, and pointer cancellation. A gesture that started on a UI button should not automatically become a gameplay action after the dialog disappears.

## Latency and repeat behavior

Do not use long main-thread work in input callbacks. Capture the intent quickly and route it to the proper state owner. For menu navigation, use a controlled key-repeat cadence and avoid duplicate activation from key down and key up. For gameplay, use the engine's existing input sampling model and timestamp events when appropriate. Do not create a second input loop alongside the engine without a concrete reason.

## Verification matrix

- Touch-only phone, keyboard, and at least one gamepad.
- D-pad navigation across every focusable control, dialog, tab, and error state.
- Stick drift, diagonal movement, trigger range, key repeat, device disconnect/reconnect.
- Two-finger simultaneous controls and pointer-up/cancel in different orders.
- Rotation/window resize and Activity pause while input is held.
- TalkBack semantics remain meaningful when using custom visual controls.

Input adapter guidance is UI/platform-side. Game rules, authoritative movement validation, networking, and anti-cheat logic belong to the game's domain/backend and must not be implemented inside a visual component.
