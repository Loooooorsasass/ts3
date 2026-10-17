# Game UI templates (starting points)

These files are **templates, not drop-in production code**. They have **not been compiled or run**: this environment had no Android SDK. Treat each file as "source reviewed, not built" until it compiles and its tests pass in the target project.

## Porting steps

1. Run `bash scripts/port_check.sh <project-root>`. It is read-only and lists placeholders, hard-coded colors, toolchain versions, and version-dependent APIs.
2. Map the templates onto the project's own tokens and theme. Replace the placeholder values (the Candy Cartoon example from `references/10-style-presets.md`).
3. Change the package name `com.example.game.ui.template` and move the files into the project structure.
4. Check `minSdk` (the highlight layer needs 23+) and the Compose/AppCompat versions. `AccessibilityNodeInfo.RangeInfo` itself is API 19+; `stateDescription` on the Views bar is exposed on API 30+.
5. Replace sample strings with the project's localization. `views/res/values/game_template_strings.xml` is for the example layout only.
6. Copy `tests/compose/*` into `src/androidTest` and `tests/unit/*` into `src/test`. Copy `tests/screenshot/GameUiPaparazziTest.kt` into `src/test` only after adding a compatible Paparazzi plugin version, then record/review the baselines (see `references/06-verification.md`).
7. Keep gameplay and domain state out of these components. They only render values owned elsewhere.

## Compose (`compose/`)

| File | What it gives you | Notes |
|---|---|---|
| `GameTokens.kt` | `GameSurfaceTokens`, `asDisabled()` | Map onto the project theme |
| `GameButton.kt` | Lip/face/edge/highlight button, spring press travel, optional haptic, disabled state, focus ring | Foundation-only; runs `onClick` on release (for pointer-down action buttons see `references/08` §3); accepts an optional interaction source for deterministic state previews; default haptic is off |
| `GamePanel.kt` | Panel with hard shadow, gradient body, edge, inner stroke, optional title band | Title exposed as a heading |
| `OutlinedGameText.kt` | Outlined text with eight offset copies | Works on any Compose version; nine layouts per instance |
| `CountUpText.kt` | Animated number + `GameNumberFormat.compact` | Recomposes per frame; Float precision limit noted |
| `SegmentedBar.kt` | Segmented bar with damage trail, range semantics and optional localized state description | Last target wins. Animation state is internal for tests. `HealthBarExample.kt` shows a non-color warning marker for low health |
| `RewardBurst.kt` | One-shot, deterministic particle burst | Primitive arrays, no per-frame allocation |
| `ReduceMotion.kt` | `rememberReduceMotion()` with observable system-setting changes and an initial process cache | ContentObserver updates active Compose consumers; `onResume` refresh may be retained as a defensive resync |

### Wiring the Views health warning

The XML example contains a warning mark that starts `gone`. Update it from the same authoritative health state as the bar, and provide localized state descriptions (API 30+ exposes them through `AccessibilityNodeInfo.setStateDescription`; older releases still have the visible warning cue):

```java
// Activity fields
private SegmentedBarView healthBar;
private TextView healthWarning;

private void bindHealthViews() {
    healthBar = findViewById(R.id.health_bar);
    healthWarning = findViewById(R.id.health_warning);
    healthBar.setStateDescriptions(
        getString(R.string.game_template_health_normal_state),
        getString(R.string.game_template_health_low_state));
}

// Call from the same state observer that owns the health fraction.
private void renderHealth(float fraction) {
    float value = Math.max(0f, Math.min(1f, fraction));
    healthBar.setFraction(value);
    healthWarning.setVisibility(value <= 0.25f ? View.VISIBLE : View.GONE);
}
```

Place the fields and methods in the host Activity/Fragment, call `bindHealthViews()` after inflating the layout, and adapt `renderHealth` to the existing state observer. `health_bar` and `health_warning` must share the same source value so visual and accessibility state cannot drift.

## Views (`views/`)

| File | What it gives you | Notes |
|---|---|---|
| `res/values/game_tokens.xml` | Colors and dimens | Merge into the project's resources |
| `res/values/game_styles.xml` | `Widget.Game.Button`, `Widget.Game.Panel`, `TextAppearance.Game.Title` | AppCompat parent |
| `res/drawable/bg_game_button*.xml` | Normal / pressed / disabled + selector | Highlight item needs API 23+ |
| `res/drawable/bg_game_panel.xml` | Panel background | |
| `res/color/game_button_text.xml` | Label color by state | |
| `res/layout/template_example.xml` | Usage sketch | Replace the custom view package |
| `java/GameButtonView.java` | Button that shifts its label with the face when pressed, haptic on click | Extends `AppCompatButton`; the background is drawn before `onDraw`, so only the label moves. Runs click on release. `Widget.Game.Button` clears `backgroundTint` so a Material `buttonStyle` cannot tint the background |
| `java/PressFeedback.java` | Scale feedback for any view | Never consumes touch events |
| `java/CountUp.java`, `java/GameNumberFormat.java` | Animated number and compact formatting | Cancel in lifecycle teardown |
| `java/SegmentedBarView.java` | Bar with trail, no allocation in `onDraw` | Range info for accessibility (API 19+; uses the `RangeInfo` constructor on API 30+ because `obtain()` is deprecated from API 33) |
| `java/MotionPrefs.java` | Reduced-motion check, initial process cache, ContentObserver listener | `SegmentedBarView` observes while attached and snaps to target when motion is disabled |

## Tests (`tests/`)

| File | Type | What it checks |
|---|---|---|
| `tests/compose/GameButtonTest.kt` | Instrumented Compose | click, disabled (not enabled, callback not invoked), button role, Vietnamese label |
| `tests/compose/SegmentedBarTest.kt` | Instrumented Compose | label/state semantics; real fill/trail values: last target wins, damage trail lags then catches up, reduced motion jumps |
| `tests/compose/RewardBurstTest.kt` | Instrumented Compose | burst returns to idle when trigger clears or reduced motion is enabled in-flight |
| `tests/screenshot/GameUiPaparazziTest.kt` | JVM visual snapshots | enabled/pressed/disabled button, normal/low health, panel and Vietnamese long text; requires Paparazzi plugin and baseline recording |
| `tests/unit/GameNumberFormatTest.kt` | JVM unit | compact number formatting and rounding |

## Scripts (`scripts/`)

| File | Purpose |
|---|---|
| `port_check.sh` | Read-only checklist for the porting steps above |

The Java files use Java 8 lambdas, which current Android Gradle Plugin desugaring supports. Kotlin projects can use them as-is or convert them.
