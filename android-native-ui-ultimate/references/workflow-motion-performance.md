# Workflow: Motion, Rendering, and Performance

## Activate when

Changing animation/feedback, custom Canvas or Surface rendering, fixed-timestep integration, frame pacing, rendering lifecycle, jank, memory, startup, battery, or thermal behavior.

## Inspect before editing

Identify the renderer/engine and version, owner thread, frame/update loop, surface/context lifecycle, animation state, screen visibility, lifecycle callbacks, existing profiler/benchmark setup, target device class, and reduced-motion setting. Load `04`, `06`, `09`, `12`, and `13` as applicable; consult `14` when input events feed the loop.

## Procedure

1. State an observable target and hypothesis. For performance, define a repeatable user journey and record device/OS, build type, refresh rate, thermal state, and data set.
2. Capture a baseline using the closest project-native profiler/benchmark. Do not infer a bottleneck from code style or debug-build timing.
3. Trace lifecycle and ownership: start/stop with visibility, cancel work on teardown, recreate surface-bound resources, and avoid retaining Activity/View/Composition objects.
4. Keep simulation and UI/render clocks intentionally separated. Preserve the current engine's fixed-step/frame-pacing contract; do not add a competing loop.
5. Tie motion to state change, keep urgent actions immediate, bound transient effects, and provide reduced-motion behavior. Avoid hot-path allocations and unbounded particles/overdraw.
6. Make one bounded change, repeat the same measurement, and compare correctness and performance under equivalent conditions.

## Choose the solution

Prefer the existing animation/rendering API and measured bottleneck fix. Use fixed timestep only when the existing simulation contract needs it; it is not a default for every UI animation. Choose Canvas/Surface or specialized rendering only with evidence and lifecycle ownership.

## Verify

Run focused animation/state tests, lifecycle/surface recreation checks, and the same profiler/benchmark journey before and after. Exercise reduced motion and rapid target changes. Use device traces and representative hardware for frame/jank, memory, thermal, and battery claims.

## Accept when

Behavior is correct across lifecycle transitions; motion is bounded and respects settings; the target metric improves or the change's correctness benefit is demonstrated; and no unsupported performance claims remain.

## Evidence boundary

Source inspection cannot prove smoothness, battery use, thermal stability, or frame-time improvement. Report device/build details and metric; if a device/profile run was unavailable, mark it UNVERIFIED.