# 13 — Performance, frame budgets, and thermal behavior

Performance work begins with a reproducible measurement and a user-visible target. Do not rewrite components or move drawing to Canvas solely because a profiler has not yet been used.

## Start with a hypothesis and a measurement

Define the journey being measured: cold start, opening a shop, scrolling inventory, starting a level, an animated HUD, or a long gameplay session. Record build variant, device/OS, refresh rate, thermal state, test data, and whether the run is warm or cold. Measure a baseline and compare the same journey after one bounded change.

Tools to consider:

- **Macrobenchmark** for repeatable startup, scrolling, and interaction measurements. See [Macrobenchmark](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview).
- **Perfetto / system traces** to identify main-thread stalls, RenderThread work, scheduling gaps, GPU waits, Binder calls, and I/O. See [System tracing](https://developer.android.com/topic/performance/tracing).
- **JankStats** to collect frame-jank signals and associate them with UI state. See [JankStats API](https://developer.android.com/reference/kotlin/androidx/metrics/performance/JankStats).
- **Baseline Profiles** for critical journeys in Compose-heavy apps. Profiles improve startup and runtime paths by allowing ART to precompile known paths; they do not fix expensive per-frame algorithms. See [Baseline Profiles](https://developer.android.com/topic/performance/baselineprofiles/overview).
- **Android Studio CPU/Memory profilers** for allocation churn, GC, retained objects, and hot methods. Use a release-like or profileable build when appropriate.

Never infer performance solely from source inspection or one debug build. Report the exact command/device/build and observed metrics.

## Frame interval is not a full app budget

At 60 Hz, a display frame is about 16.67 ms; at 90 Hz, about 11.11 ms; at 120 Hz, about 8.33 ms. These are total intervals. The app cannot assume all of that time is available for Kotlin/Java work: rendering, GPU, synchronization, the OS, and presentation all contribute. Aim to finish critical work with margin and measure actual missed deadlines on target hardware.

Avoid fixed claims such as “the game must always use exactly 60 FPS.” Respect variable refresh rates and the renderer/engine's chosen frame-pacing model. An idle menu should not run expensive per-frame work when nothing changes. Animations should have bounded lifetimes and a reduced-motion path.

## Common sources of UI/game jank

- Expensive recomposition caused by collecting high-frequency state too high in the tree.
- `CountUpText`-style values forcing a wide UI subtree to recompose every frame.
- Allocations or formatting inside `onDraw`, `drawBehind`, or particle loops.
- Decoding images, loading files, or doing network/serialization work on the main or render thread.
- Excessive overdraw, large translucent layers, unbounded particle counts, or repeated shader compilation.
- Lock contention between the simulation, render, and UI threads.
- Layout thrashing caused by repeatedly changing constraints or measuring large trees.

Fix the measured bottleneck. Keep an ordinary `Text` for a small number of counters unless profiling demonstrates it is a problem. Custom glyph drawing adds font, shaping, accessibility, clipping, scaling, and localization complexity.

## Memory and lifecycle

Bound image caches, decoded assets, replay buffers, and particle pools. Release surface-bound GPU resources when their owning context is lost. Avoid retaining Activity, View, or Composition references in process singletons.

Treat `onTrimMemory()` or corresponding lifecycle/process signals as hints to shed reclaimable caches, not as proof that a process will remain alive. Store durable progress independently from in-memory caches. Do not block the main thread while writing a large save or unloading assets.

For soak tests, run a representative level or animated screen for a defined duration, watch memory growth, GC, jank, thermal status, and battery drain, and repeat on low- and mid-tier target devices. Look for monotonic memory growth, growing frame time, and thermal throttling. Thermal behavior is device-specific; do not extrapolate from one cool run.

## Thermal and battery policy

Treat thermal headroom as a signal to reduce optional work, not a universal cross-device temperature threshold. Depending on the engine, degrade particle density, post-processing, render resolution, or nonessential background work gradually while protecting interaction and readable HUD. Do not silently change simulation speed, competitive fairness, or authoritative game state to save power.

Consider pausing continuous rendering on static screens, using dirty rendering where supported, avoiding needless wake locks, and stopping animation/timers when their screen is no longer visible. Resume from monotonic time and clear stale effects after long background periods.

## Verification contract

1. Capture a baseline on the same hardware and build type.
2. State the hypothesis and metric before changing code.
3. Collect trace/benchmark evidence for the same scenario.
4. Check correctness after throttling, background/foreground, surface recreation, and navigation away/back.
5. Keep the change only if the measured result justifies its complexity.

Official references: [Macrobenchmark](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview), [Perfetto](https://developer.android.com/topic/performance/tracing), [Baseline Profiles](https://developer.android.com/topic/performance/baselineprofiles/overview), [JankStats](https://developer.android.com/reference/kotlin/androidx/metrics/performance/JankStats), [Thermal API](https://developer.android.com/games/optimize/adpf/thermal), [Activity memory callbacks](https://developer.android.com/reference/android/app/Activity#onTrimMemory(int)).
