# 04 — Native View, Canvas, and Compose interop

Load this reference only when the project uses classic Views/XML, custom drawing, game rendering, `AndroidView`, or mixed UI trees.

## Preserve the actual renderer

A request to restyle controls around a custom canvas does not automatically authorize replacing the renderer. Before modifying or replacing it, inspect its constructors, public setters, listener interfaces, event dispatch, coordinate model, layout sizing, invalidation rules, and callers. Confirm which layer owns gameplay/domain state.

## Compose `AndroidView` bridge

Inspect `factory`, `update`, and disposal/lifecycle behavior. Verify:

- View instances are not recreated unnecessarily.
- State passed in `update` stays synchronized when inputs change.
- Listeners are not attached multiple times across recompositions.
- Callbacks cannot retain stale Compose state or reference destroyed owners.
- Disposable resources and registrations are cleaned up.
- Native View input and Compose pointer/gesture handlers do not fight each other.
- Density, size, padding, clipping, insets, and coordinate conversions are correct.

Use the APIs supported by the project's Compose version; never invent a bridge method without inspecting the actual class.

## Custom `View` / `Canvas` draw path

- Keep `onDraw()` focused on rendering. Do not perform database/network/file access, maze/pathfinding searches, synchronous service calls, or blocking work in the draw callback.
- Reuse `Paint`, `Path`, `Rect`/`RectF`, text measurement helpers, and caches when it is safe to do so. Avoid avoidable per-frame allocations in high-frequency drawing.
- Define when cached geometry becomes invalid and rebuild only when relevant inputs change.
- Call `invalidate()` for redraw-only changes and request layout only when dimensions/measurement actually change.
- Check clipping, density conversion, transforms, touch coordinate mapping, hit-test boundaries, and edge cases at different aspect ratios.
- Preserve accessibility: custom-drawn information/control must expose semantics or an accessible companion interaction when applicable.
- Keep gameplay/domain rules outside draw code. Render data from the existing authoritative owner.

## Input and performance verification

Test pointer down/move/up, rapid input, cancel, multi-touch if supported, scroll conflict, system gestures, screen pause/resume, view recreation, and rotation/resize where supported. Check whether the view draws and hits the same logical geometry. On performance-sensitive canvases, consider profiling frame time and allocations instead of relying on source inspection alone.

## Scope rule

If the requested work is only visual, don't modify procedural generation, collision, scoring, pathfinding, move counting, win/loss criteria, or persistence. If a logic change is explicitly requested, add deterministic tests for behavior and boundary conditions as well as UI verification.
