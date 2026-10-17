# 09 — Motion, feedback, and "juice"

Load this reference for animation, transitions, press feedback, reward moments, haptics, or any request like "make it feel more alive/premium". Motion must explain something (state change, hierarchy, success/failure) and must never delay input. Durations below are **starting values**; tune on a device.

## 1. Principles

1. **Motion explains.** Every animation answers: what changed, where did it go, what should I do next.
2. **Input is never blocked.** A running animation does not swallow taps unless the interaction truly requires it (for example a short confirmation). Long reward sequences always have a visible Skip/Continue.
3. **Fast in, slower out for emphasis.** Pressing is near-instant; celebration can breathe.
4. **Springs for physical things, tweens for fades and color.** Springs make pressed/released and drag feel natural; tween + easing suits opacity, color, and scrims.
5. **Budget.** Few things move at once. One focal animation per moment; supporting motion is subordinate.
6. **Always have a no-motion path.** Reduced motion replaces movement with instant state change or a short fade (section 6).
7. **Do not touch game logic.** Motion reads state; it does not own it. A "count-up" shows a value that is already final in the authoritative state.

## 2. Timing guide

| Purpose | Duration | Notes |
|---|---|---|
| Press / release feedback | 70–150 ms | Springs; scale about 0.96 or surface travel (see button recipe) |
| Toggle, chip select, small state change | 120–200 ms | Tween with standard easing |
| Element enter/exit, dialog open | 200–300 ms | Decelerate on enter, accelerate on exit |
| Screen/route transition | 250–400 ms | Direction communicates hierarchy (forward/back) |
| Count-up of a number | 400–800 ms | Ease-out; show the final value to accessibility immediately |
| Damage trail on a bar | 100–150 ms front, trail waits about 300–400 ms then 400–500 ms | See `SegmentedBar.kt` / `SegmentedBarView.java` |
| Reward burst / confetti | 600–1,000 ms | Particle count tied to quality tier |
| Staggered list entrance | 30–50 ms per item, cap at about 6 items | Never stagger long lists |
| Whole reward/result sequence | ≤ 1.5–2 s before Continue is available, skippable | |

Idle/attention animations (pulse on a claimable reward) are limited: loop a few times or until seen, then stop; never loop forever on several elements.

## 3. Catalog of useful effects

| Effect | Where it helps | Notes |
|---|---|---|
| Press travel + lip collapse | Buttons | Face moves down, shadow lip shrinks; spring release with slight overshoot |
| Count-up | Score, coins, XP | Tabular numerals; never animate through invalid intermediate states that could be read as real |
| Bar with trail | Health, progress, cooldown | Trail communicates "how much was lost" |
| Fly-to-counter | Coins/gems flying to the resource chip | Cap particle count; end with a small chip "bump" |
| Reward burst | Chest open, level complete | Deterministic particles from a seed; cheap circles/stars |
| Stagger-in | Result rows, shop cards | Short delays; start from slight offset + alpha |
| Pop/overshoot | New badge, star earned | Scale 0 → 1.15 → 1 with spring |
| Shake | Invalid action, damage | Small amplitude, 2–3 oscillations; skip when reduced motion; never on the whole gameplay view unless that is a design feature |
| Scrim fade | Dialogs, pause | Alpha only, 150–200 ms |
| Shimmer/skeleton | Loading lists | Only for real loading; stop on error |

## 4. API mapping (stay inside the project's installed versions)

| Need | Compose | Views (Kotlin/Java) |
|---|---|---|
| Animate a value | `animate*AsState`, `Animatable` | `ValueAnimator`, `ObjectAnimator` |
| Several properties together | `updateTransition` | `AnimatorSet`, `ViewPropertyAnimator` |
| Physical/spring motion | `spring(dampingRatio, stiffness)` | `androidx.dynamicanimation` (`SpringAnimation`) — check that the dependency exists before using |
| Sequenced steps | `LaunchedEffect` + `animateTo` + `delay` | `AnimatorSet.playSequentially`, or listener chains |
| Appear/disappear | `AnimatedVisibility`, `AnimatedContent` | `TransitionManager` (`androidx.transition` if present), property animators |
| Draw-only animation (no relayout) | Read state inside `drawBehind`/`Canvas`/`graphicsLayer {}` | Mutate fields + `invalidate()` |
| Move/scale without relayout | `Modifier.graphicsLayer`, lambda `Modifier.offset {}` | `translationX/Y`, `scaleX/Y`, `alpha` |
| Gesture-driven | `Animatable` + `pointerInput`/`draggable` | `GestureDetector`, `SpringAnimation` with velocity |

Performance tips:
- Animate with **draw/layout-phase reads** (lambdas) instead of recomposing the whole screen each frame.
- Do not animate layout size of large subtrees if a transform will do.
- Reuse objects in `onDraw` and in particle updates; use primitive arrays for particle state.
- Respect lifecycle: cancel animators in `onDetachedFromWindow`/`onDestroyView`; in Compose, rely on effect cancellation and key effects correctly.

## 5. Haptics

- Use haptics for confirmation of meaningful actions (press on primary actions, purchase success, error), not for every tap or per-frame events. `GameButton` defaults to no haptic because Compose feedback types vary by version and input semantics; opt in explicitly only with a type supported by the project.
- Compose: `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.…)` — the available types depend on the installed version. Views: `view.performHapticFeedback(HapticFeedbackConstants.…)` (`CONFIRM`/`REJECT` need API 30+; provide a fallback such as `VIRTUAL_KEY`).
- Provide an in-app haptics toggle if the project has a settings screen, and respect the system's touch-feedback setting (the platform call already does when you use `performHapticFeedback`).
- Do not use custom `Vibrator` patterns for ordinary UI unless the project already does and the manifest permission is in place.

## 6. Reduced motion

- **System signal:** `Settings.Global.ANIMATOR_DURATION_SCALE == 0f` indicates system animator timing is disabled; it is only a practical motion signal, not a universal reduced-motion preference. `ReduceMotion.kt` and `MotionPrefs.java` observe the setting while active/attached; keep Activity `onResume` resync as a defensive fallback where appropriate. Combine with an in-app preference when the product offers one.
- **In-app toggle:** if the project has settings, offer "Reduce motion" and let it override the system signal.
- **Replacement rules:** movement → instant or short fade; bursts/particles → none (show a static "+N" label); shakes → color/flash-free emphasis (icon/label); count-up → final value; looping idle effects → off.
- Avoid flashes and high-frequency flicker regardless of the setting.

## 7. Verifying motion

- Toggle animation scale from the shell: `adb shell settings put global animator_duration_scale 0` (and `10` to slow things down for inspection). Restore it to `1` afterwards.
- Compose tests: control the clock with the test rule (`mainClock.autoAdvance`, `advanceTimeBy`) rather than sleeping.
- Check that rapidly changing the value (damage, damage, heal) never leaves the bar/number in a wrong final state — the **last** target must always win.
- Check rotation/recreation mid-animation: no crash, and the final value displays correctly.
- Profile on a low-end device if the screen has particles or large animated surfaces; do not claim smoothness from source reading.

## 8. Anti-patterns

- Looping glow/pulse on many elements at once.
- Reward sequences without Skip.
- Animations that make the player wait before pressing Next/Retry.
- Count-ups that disagree with the real stored value if the user leaves the screen (always render the final value on the next entry).
- Springs with high bounce on text-heavy layouts (looks jittery and hurts readability).
- Per-frame allocations (new `Paint`, `Path`, `Brush`, lists) in animated draw paths.
- Haptic spam.

## 9. Templates

`assets/templates/compose/`: `GameButton.kt` (press travel + haptic), `CountUpText.kt`, `SegmentedBar.kt`, `RewardBurst.kt`, `ReduceMotion.kt`.
`assets/templates/views/java/`: `PressFeedback.java`, `GameButtonView.java`, `CountUp.java`, `SegmentedBarView.java`, `MotionPrefs.java`.
