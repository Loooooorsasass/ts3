# 06 — Verification and handoff

## Verification ladder

Use the narrowest relevant check first, then broaden the scope as time and tooling permit.

1. **Static inspection:** review diff, call sites, API usage, resource references, state ownership, and secrets/config exposure.
2. **Compile/lint:** use the repository's Gradle wrapper and documented task names; inspect modules and variants before choosing a task. Do not invent missing build files.
3. **Unit/regression tests:** test business/state transformations and bugs introduced or affected by the change.
4. **UI tests:** test semantics/actions/state transitions using existing Compose/View testing conventions. Prefer behavior assertions over fragile implementation details.
5. **Integration flows:** test navigation/back/cancel, persistence, error states, service callbacks, permissions, and View interop where touched.
6. **Visual verification:** inspect real emulator/device rendering or current screenshot tests at representative sizes and states. Source code and unexecuted screenshots are not runtime verification.
7. **Performance:** for frequently redrawn or animation-heavy surfaces, profile when available; do not claim smoothness based only on static code.

## Screenshot tests (JVM, no physical emulator required)

For these reusable templates, start with **Paparazzi** when the target is a deterministic component snapshot. Paparazzi supports Compose, Android Views, and mixed hierarchies. Prefer a currently compatible stable release; verify the project's JDK, Gradle, AGP, Compose, and Paparazzi matrix before pinning. Do not adopt an alpha release as a production gate without a short compatibility spike.

Official sources: [Paparazzi README](https://github.com/cashapp/paparazzi), [Paparazzi changelog](https://cashapp.github.io/paparazzi/changelog/), [Paparazzi accessibility snapshots](https://cashapp.github.io/paparazzi/accessibility/).

### Setup sketch

Add the plugin version explicitly in the target project's `build.gradle.kts` after checking compatibility:

```kotlin
plugins {
    id("app.cash.paparazzi") version "<pinned-compatible-version>"
}
```

Keep the version in the project's version catalog or plugin management convention if that is the repository standard. A minimal snapshot test has this shape:

```kotlin
@get:Rule val paparazzi = Paparazzi()

@Test
fun gameButtonEnabled() {
    paparazzi.snapshot(name = "game-button-enabled") {
        GameButton(
            text = "Continue", onClick = {},
            reduceMotion = true, hapticType = null,
        )
    }
}
```

Copy `assets/templates/tests/screenshot/GameUiPaparazziTest.kt` to the appropriate JVM test source set, adapt packages and tokens, then record and review snapshots using the tasks available in the installed plugin. Common Paparazzi tasks are:

```bash
./gradlew :app:recordPaparazziDebug
./gradlew :app:verifyPaparazziDebug
```

Confirm task names with `./gradlew tasks --all`; do not copy them blindly if the project uses different variants or plugin version. Commit baseline files only after reviewing image diffs. Keep CI JDK, locale, font scale, device config, theme, and rendering dependencies pinned; do not automatically overwrite golden files on failure.

### When to choose Roborazzi instead

Choose [Roborazzi](https://github.com/takahirom/roborazzi) when screenshot capture belongs inside existing Robolectric/JVM integration tests, or when the test needs Activity/View hierarchy, Android framework behavior, Espresso, or test fixtures already built around Robolectric. It has its own Gradle plugin and `recordRoborazzi<Variant>` / `verifyRoborazzi<Variant>` tasks; verify exact task names and compatibility against the pinned version. Configure Robolectric SDK/graphics mode consistently and avoid parallel tasks writing to the same output folder.

Paparazzi is the default for simple component goldens in this skill; do not add both tools unless a concrete test requirement justifies the extra configuration and CI surface.

### Required baseline matrix for this template

| Surface | Required states |
|---|---|
| `GameButton` | enabled, pressed, disabled |
| `SegmentedBar` / `HealthBarExample` | normal and low state, including warning marker and spoken state description |
| `GamePanel` | standard panel and long Vietnamese content with diacritics |
| Views renderer | at least one custom `SegmentedBarView` snapshot if the target project uses the Views templates |
| Text/adaptive | long localized copy, increased font scale, compact width; add RTL only if claimed as supported |

Snapshot tests are visual regression tests, not a substitute for interaction tests, reduced-motion lifecycle tests, TalkBack checks on platform devices, or performance profiling. Do not claim that baseline images have been recorded until the target project has run the record task and the images have been reviewed.

## Do not overclaim

Use precise language:
- “Source reviewed” means files were inspected.
- “Compilation passed” means the stated build task was actually executed and succeeded.
- “UI test passed” names the actual test/command executed.
- “Visual check performed” means rendered output was inspected.
- “Not verified” means required tooling, the full project, device, credentials, or external service was unavailable.

A successful build does not prove visual quality, accessibility, Play Billing, authentication, advertisements, cloud services, or all supported devices.

## Risk-based test selection

Prioritize checks based on the changed surface:

- Theme/token change → all affected surfaces, light/dark variants, semantic status colors, screenshot/diff review.
- Navigation/modal change → open/close/back/cancel/confirm, return screen, duplicate handlers, state restoration.
- Form/IME change → keyboard behavior, validation, focus order, scrolling, submission/retry.
- Localization → longest labels, plural values, font fallback, RTL where supported.
- Canvas/View interop → draw updates, input/hit test, listener lifecycle, pause/resume/recreation, performance.
- Persistence/data state → success/failure and reopen/restart when meaningful.
- Third-party integration → use safe test configuration/sandbox and never paste credentials into reports.

## Final handoff template

### Changed
- Files/screens and user-visible behavior.

### Preserved
- Rules, state ownership, navigation, integrations, or data behavior intentionally left unchanged.

### Verification
- Exact commands/tests and observed results.
- Whether rendering/device checks were run.

### Remaining risks
- Untested form factors/locales/service flows, missing full checkout/tooling, or follow-up items.

## Concrete commands (replace `:app` with the real module; run in the target project)

```bash
bash /path/to/skill/assets/templates/scripts/port_check.sh .   # read-only porting checklist
./gradlew :app:assembleDebug                 # compile
./gradlew :app:testDebugUnitTest             # JVM tests, e.g. GameNumberFormatTest
./gradlew :app:connectedDebugAndroidTest     # Compose UI tests; needs an emulator or device
./gradlew :app:lintDebug                     # lint
adb exec-out screencap -p > /tmp/screen.png  # capture the current screen for review
adb shell settings put global animator_duration_scale 0   # reduced-motion check
adb shell settings put global animator_duration_scale 1   # restore afterwards
```

Report each command with its actual result. A command that was not run stays "not run".
