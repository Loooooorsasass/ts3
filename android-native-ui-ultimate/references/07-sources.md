# 07 — Sources and official resources

Use current documentation for API-level behavior; these links are starting points, not proof that a project includes a given dependency or satisfies a guideline. Recheck version-specific APIs against the installed stack.

## Provenance

This skill is self-contained. It does not depend on any other skill or repository being installed, and it does not import app-specific assumptions from the projects it was derived from.

## Official Android documentation

- Compose accessibility API defaults and minimum touch targets: https://developer.android.com/develop/ui/compose/accessibility/api-defaults
- Compose semantics/accessibility: https://developer.android.com/develop/ui/compose/accessibility
- Window insets and edge-to-edge: https://developer.android.com/develop/ui/compose/system/insets
- Adaptive layouts with Compose: https://developer.android.com/develop/ui/compose/build-adaptive-apps
- Adaptive window-size classes: https://developer.android.com/develop/adaptive-apps/guides/use-window-size-classes
- Compose UI testing: https://developer.android.com/develop/ui/compose/testing
- Common Compose UI test patterns: https://developer.android.com/develop/ui/compose/testing/common-patterns
- Android app testing fundamentals: https://developer.android.com/training/testing/fundamentals

## How to use sources

1. Discover the project's actual framework, dependency versions, and target SDK.
2. Read the relevant current official documentation for the API/behavior being changed.
3. Use repository examples and tests as the local style/compatibility baseline.
4. If official docs and repository code appear inconsistent, verify current behavior and explain migration risks before making broad changes.
5. Do not treat external design guidance as authority to change app rules or architecture unrelated to the request.

## Additional documentation for game UI work

Checked on 2026-10-17 by searching for each page and opening the result. Three links from an earlier draft could not be confirmed as live pages, so they were replaced by the canonical URLs below. The links in the first list above were not re-checked in this revision. Pages move over time: if a link redirects or fails, search the official docs, and still confirm the API exists in the project's installed version.

- Compose animation overview: https://developer.android.com/develop/ui/compose/animation/introduction
- Compose graphics and custom drawing: https://developer.android.com/develop/ui/compose/graphics/draw/overview
- Drawable resources (shape, layer-list, selector): https://developer.android.com/guide/topics/resources/drawable-resource
- Property animation (Views): https://developer.android.com/guide/topics/graphics/prop-animation
- Spring animation (`androidx.dynamicanimation`): https://developer.android.com/guide/topics/graphics/spring-animation
- `AccessibilityNodeInfo.RangeInfo` (class added in API 19, constructor API 30, `obtain()` deprecated in API 33): https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo.RangeInfo

## Game runtime, input, performance, and platform services (added in v1.4.0)

- GameActivity (AGDK): https://developer.android.com/games/agdk/game-activity
- OpenGL ES environment: https://developer.android.com/develop/ui/views/graphics/opengl/environment
- `GLSurfaceView`: https://developer.android.com/reference/android/opengl/GLSurfaceView
- Frame pacing library: https://developer.android.com/games/sdk/frame-pacing
- Macrobenchmark overview: https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview
- Perfetto/system tracing: https://developer.android.com/topic/performance/tracing
- JankStats: https://developer.android.com/reference/kotlin/androidx/metrics/performance/JankStats
- Baseline Profiles: https://developer.android.com/topic/performance/baselineprofiles/overview
- Android thermal guidance: https://developer.android.com/games/optimize/adpf/thermal
- `Activity.onTrimMemory`: https://developer.android.com/reference/android/app/Activity#onTrimMemory(int)
- Game controllers: https://developer.android.com/develop/ui/views/touch-and-input/game-controllers
- Google Play Billing integration: https://developer.android.com/google/play/billing/integrate
- Billing testing: https://developer.android.com/google/play/billing/test
- Rewarded ads: https://developers.google.com/admob/android/rewarded
- UMP privacy/consent: https://developers.google.com/admob/android/privacy
- Play Games Services: https://developer.android.com/games/pgs/android/android-overview
- Play Games Services API entry point: https://developers.google.com/android/reference/com/google/android/gms/games/PlayGames
- Play Integrity overview: https://developer.android.com/google/play/integrity/overview

Version-specific APIs and policies change. The URLs are official starting points; follow the migration/version pages linked there and verify compatibility with the application's actual Gradle, AGP, Kotlin, SDK, and provider setup.
