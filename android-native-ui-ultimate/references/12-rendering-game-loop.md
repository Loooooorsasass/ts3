# 12 — Rendering architecture and game loop

This reference helps choose and integrate a rendering surface for game-like Android UI. It does not replace the architecture of an existing game engine. Identify the active renderer and its lifecycle contract before introducing a new surface or loop.

## Choose the smallest rendering layer that fits

- **Compose or ordinary Views:** menus, settings, inventory, shops, dialog-heavy flows, and HUDs whose elements are mostly text and controls. Use platform drawing primitives before introducing a dedicated renderer.
- **`SurfaceView`:** a separately composed surface for a custom renderer, video-like output, or a game loop with explicit frame production. It can be layered with Android UI, but its z-order, insets, input, and lifetime need deliberate handling.
- **`GLSurfaceView`:** a convenient OpenGL ES host when its provided render-thread and lifecycle model fit. Choose `RENDERMODE_WHEN_DIRTY` for content that only changes on demand; continuous rendering costs power when nothing changes.
- **Vulkan:** use when the project already has a Vulkan renderer or requires explicit low-level GPU control. Do not introduce it for a conventional HUD redesign.
- **`GameActivity`:** consider for games whose core is C/C++ and needs the AGDK native glue for lifecycle, input, and text input. It renders into a `SurfaceView` that can coexist with Android UI. It is an integration host, not a replacement for a game's engine architecture.

Official starting points: [GameActivity](https://developer.android.com/games/agdk/game-activity), [OpenGL ES environment](https://developer.android.com/develop/ui/views/graphics/opengl/environment), [GLSurfaceView](https://developer.android.com/reference/android/opengl/GLSurfaceView).

## Surface and context lifecycle

A surface is not permanent. Handle surface creation, resize, destruction, Activity pause/resume, and process recreation explicitly. Stop submitting work to a destroyed surface. Recreate size-dependent framebuffer targets and viewport state after a resize. With OpenGL ES, assume the EGL context can be lost; reload GPU resources from retained CPU-side descriptions or packaged assets rather than depending on stale texture/program handles.

Do not block the main thread waiting for a render thread, and do not mutate the renderer's state from arbitrary UI callbacks without a defined synchronization mechanism. Send commands through a queue, immutable state snapshot, or renderer-owned dispatcher. Cancel or drain pending work when the surface is destroyed. A callback from an old surface must not update the new surface instance.

A `SurfaceView` and its surrounding Views/Compose nodes can have different timing and composition behavior. Test z-order, clipping, system bars, edge-to-edge insets, dialogs, keyboard/IME, and accessibility overlays on actual devices. Do not assume a screenshot from a single preview proves correct surface composition.

## Fixed-step simulation and frame pacing

Keep simulation time separate from render time when deterministic or stable movement matters. A common accumulator model adds elapsed monotonic time, advances simulation in fixed steps, then renders an interpolation between previous and current state. Clamp unusually large elapsed times after pause/resume to prevent a catch-up spiral. Define a maximum number of simulation steps per rendered frame, and make pause/resume behavior explicit.

Pseudocode (illustrative; adapt it to the engine and thread model):

```kotlin
val stepSeconds = 1.0 / 60.0
var accumulator = 0.0
var previousNanos = clock.nanoTime()

while (running) {
    val now = clock.nanoTime()
    val elapsed = ((now - previousNanos) / 1e9).coerceIn(0.0, 0.25)
    previousNanos = now
    accumulator += elapsed

    var steps = 0
    while (accumulator >= stepSeconds && steps < maxStepsPerFrame) {
        updateSimulation(stepSeconds)
        accumulator -= stepSeconds
        steps++
    }

    val alpha = (accumulator / stepSeconds).toFloat()
    renderInterpolated(alpha)
}
```

This is a conceptual sketch, not a drop-in loop: a production loop also needs cancellation, surface readiness, synchronization, frame deadlines, and back-pressure. Do not run this on the UI thread. For engines already using Choreographer, a game engine, or AGDK frame pacing, preserve its timing model instead of layering in a second competing loop.

Android frame intervals are approximately 16.67 ms at 60 Hz and 8.33 ms at 120 Hz. These are whole-frame intervals, not a CPU-only budget; app work competes with system and GPU work. Use Android's frame-pacing guidance and measure on supported refresh rates rather than hard-coding 60 FPS: [Frame pacing](https://developer.android.com/games/sdk/frame-pacing).

## Render hot-path rules

- Do not allocate objects, collections, bitmaps, paths, or formatted strings per frame unless measurement proves the cost acceptable.
- Cache reusable geometry, `Paint`, shader, and GPU resources for the lifetime they actually belong to; invalidate on size, theme, or surface/context changes.
- Keep I/O, resource decoding, network calls, and blocking locks out of the render loop.
- Avoid logging every frame. Aggregate counters and sample traces instead.
- Do not use Compose recomposition as a substitute for a high-frequency simulation loop. Publish only the UI state the HUD needs at a bounded rate.
- Do not use custom drawing to bypass semantics: important counters, health, warnings, and controls still need accessible labels and actions.

## Shader and GPU resource lifecycle

Compile or load shaders outside the per-frame draw path. Check compilation/link status and surface errors through a safe diagnostic path. Keep source assets/versioning clear and support context recreation. Avoid silently continuing after resource creation fails; use an explicit fallback or report the failure. Do not put secrets or user data in shader logs.

## Verification checklist

- Surface creation/destruction and Activity pause/resume do not leak threads or render against a dead surface.
- Resize, rotation, multi-window, edge-to-edge, and overlay UI remain correct where supported.
- A 120 Hz display does not cause simulation speed to double.
- Large elapsed time after backgrounding does not cause a catch-up spiral.
- The idle state stops unnecessary continuous rendering where possible.
- CPU/GPU/frame pacing and battery behavior are measured before and after a renderer change.
