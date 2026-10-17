# Update notes — v1.4.0

## Added references 12–17

- `12-rendering-game-loop.md`: renderer choice, `SurfaceView`/`GLSurfaceView`/GameActivity boundaries, surface/context lifecycle, fixed-step simulation sketch, shader/resource lifecycle, render hot-path rules.
- `13-performance.md`: measurement-first workflow, Macrobenchmark/Perfetto/JankStats/Baseline Profiles, frame intervals, memory trim signals, thermal and soak testing.
- `14-input-controllers.md`: input normalization, controller `KeyEvent`/`MotionEvent`, dead zones, focus navigation, multi-touch `pointerId`, disconnect/cancel paths.
- `15-monetization-ads-consent.md`: purchase UI state machine, pending/restore states, rewarded-ad reward callback, UMP consent/privacy entry point, test and policy boundary.
- `16-platform-services-save.md`: sign-in UX, achievements/leaderboards, retries/idempotency, local/cloud save ownership, schema migration and atomic persistence.
- `17-multiplayer-ui-anticheat.md`: connection/reconnect/queue/result UI, stale callbacks, safe rejection messages, Play Integrity/backend trust boundaries.

## Router and release metadata

- Bumped SKILL metadata version to `1.4.0`.
- Updated the intent router with specialist references `12`–`17` while retaining `01`, `05`, and `06` as mandatory baseline references for UI tasks.
- Updated README structure and scope notes.
- Multiplayer, anti-cheat, purchase entitlement, provider policy, cloud save authority, and service security are covered only as client/UI integration boundaries; this archive does not implement backend systems.

## Verification status

These references link to official documentation and intentionally avoid hard-coding third-party SDK versions. API behavior and policy must be checked against the current project/dependency versions before implementation. This archive is a documentation/skill bundle; the target Android app was not built from this archive, and no game runtime, live billing, ad, consent, sign-in, leaderboard, save-service, or multiplayer integration has been executed.

---

# Update notes — production-readiness pass

This archive applies the P1–P3 source/documentation changes agreed during review.

## P1 — state correctness and accessibility

- Compose reduced-motion now uses a live `ContentObserver` while the composable is active; the process cache is only an initial read. `onResume` refresh remains a defensive option.
- Views `MotionPrefs` supports an attached-lifecycle observer. `SegmentedBarView` cancels in-flight animators and snaps to its target when reduced motion becomes enabled; it unregisters on detach.
- `RewardBurst` resets to its idle/finished state when trigger clears, colors disappear, or reduced motion is enabled. The animation driver is testable.
- `SegmentedBar` can expose a caller-localized `stateDescription`; `HealthBarExample` and the XML sample add a non-color low-health warning marker.
- Intent routing explicitly retains discovery (`01`), accessibility/localization/adaptive (`05`), and verification (`06`) as baseline layers.

## P2 — verification evidence

- Added `tests/compose/RewardBurstTest.kt` and a state-description assertion in `SegmentedBarTest.kt`.
- Added `tests/screenshot/GameUiPaparazziTest.kt` for button enabled/pressed/disabled, normal/low health, a panel, and long Vietnamese copy.
- Expanded `references/06-verification.md` with Paparazzi setup/record/verify, Roborazzi decision criteria, CI determinism guidance, and a snapshot matrix.

## P3 — maintainability and polish

- Removed the duplicated nested `android-native-ui/android-native-ui/` copy from the archive; the root contains one canonical skill tree.
- Port checker scans Kotlin/Java/XML color literals and reports relevant API-level considerations.
- Retuned disabled colors toward a lighter face/darker foreground. Re-measure contrast after mapping to the host project's theme and overlays; color contrast cannot be assumed from these placeholders alone.
- `GameButton` defaults haptics to off because valid Compose feedback types vary by version and action semantics; it accepts an optional interaction source for deterministic visual-state capture.

## Verification limits

This archive contains templates but no Gradle wrapper/build scripts or Android project module. XML parsing, shell syntax and archive structure can be checked here; Android compilation, screenshot recording, baseline review, TalkBack checks and device profiling must run after porting into the target app. Screenshot test sources are templates, not verified snapshots.
