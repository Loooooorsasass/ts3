# 08 — Game UI craft (menus, HUD, controls, polish)

Load this reference when the task is a game-style interface: HUD, touch controls, main menu, level select, pause, result, shop, inventory, settings, reward popups, or when the user asks for a "more professional / more beautiful" game UI. Pair it with `10-style-presets.md` (visual direction) and `09-motion-juice.md` (feedback and motion). Repository facts always win: existing art, tokens, and gameplay rules override everything here. Numbers below are **starting values to validate on a device and in playtesting**, not laws.

## 1. What makes game UI different from app UI

- **Gameplay owns the screen.** Persistent UI must be glanceable and must not hide the play field. Menus can be richer than the HUD because the player has stopped playing.
- **Feedback is part of the design.** A button that looks good but does not respond (press, sound/haptic where appropriate, state change) reads as unfinished.
- **Hands cover the screen.** Thumbs occlude corners and bottom edges; design for what stays visible while touching.
- **State clarity beats decoration.** Locked, new, affordable, equipped, selected, and recommended must be distinguishable at a glance and not by color alone.
- **Consistency of one visual language** (materials, edge treatment, lighting direction, icon style) matters more than any single fancy element.

## 2. HUD rules

1. **Three priority tiers.** *Always visible* (the few values the player needs to survive/score), *contextual* (appear when relevant: combo, objective hint), *on demand* (inventory, map, details in a pause/side sheet). Count what is always visible; if it exceeds roughly 5–7 elements, demote something.
2. **Zones.** Keep the center of the play field clear. Put persistent read-only values in the top corners/top edge, thumb controls in the lower corners, transient messages in the upper-center third. Respect cutouts and rounded corners via insets (see 05).
3. **Do not put frequent or destructive actions near system gesture edges.** For Views, use `View.setSystemGestureExclusionRects` (API 29+) only for small, deliberate regions such as a joystick; do not exclude whole edges. For Compose, check the installed version for a system-gesture-exclusion modifier before using one; this skill does not assume a specific name.
4. **Legibility over variable backgrounds.** Use outline/shadow text, a subtle scrim behind dense numbers, or panels with sufficient opaque area. Test over the brightest and darkest scenes in the game.
5. **Scale and opacity settings.** Offer HUD scale and idle opacity if controls cover gameplay. Persist via the project's existing settings owner.
6. **Left/right-handed mirroring** for control clusters when the game relies on them.
7. **Warn with more than color.** Low-health: color + icon/pulse (respecting reduced motion) + number.

## 3. Touch controls

- Minimum 48 dp hit target everywhere; primary gameplay buttons commonly 56–72 dp; give adjacent controls enough spacing to prevent mis-taps (start around 8–12 dp and tune).
- Show the visible pressed state on pointer-down for every button (see the button recipe below).
- **Action buttons** (jump, fire, dash) should run their action on pointer-down. **Menu buttons** run it on release, so dragging away cancels.
- **The templates (`GameButton`, `GameButtonView`) run `onClick` on release.** That fits menu buttons but not action buttons. For a pointer-down action button, start the action from the press callback (Compose: `detectTapGestures(onPress = { ... })` inside `pointerInput`; Views: `ACTION_DOWN` in a touch listener or a custom view). Keep the same action reachable through the control's click/accessibility action as well, so TalkBack and switch users can still trigger it.
- Define multi-touch ownership: which pointer owns the joystick vs. buttons; handle `ACTION_POINTER_UP`/cancel so a control never stays stuck.
- Include a dead zone for analog sticks and clamp the knob to the base radius.
- For custom drawn controls, keep drawing geometry and hit-test geometry the same object/values (see 04).

## 4. Surface and depth recipe (works in Compose, Canvas, and XML drawables)

Game-style surfaces read as "physical" because they follow one lighting model. Light comes from the top.

| Layer | Purpose | Typical treatment |
|---|---|---|
| Lip / base shadow | Thickness and grounding | Darker shade of the face hue, offset down by `depth` (about 4–6 dp for buttons, 3–4 dp for panels) |
| Face | The main color | Vertical gradient, top lighter than bottom; keep the delta modest (about 8–15% lightness) so it stays in one hue family |
| Edge stroke | Separation from the background | 1.5–3 dp, a darker, slightly more saturated shade of the face; avoid pure black |
| Top highlight | Light catching the upper edge | 1.5–2 dp line, white at about 40–60% alpha, inset from the edge |
| Inset well (panels) | Content area that feels recessed | Slightly darker fill + thin lighter stroke on the inside |

Rules:
- One shadow direction for the whole app. Do not mix soft Material elevation shadows on some surfaces with hard lips on others unless the style preset says so.
- Avoid pure black (`#000000`) shadows and borders; use a dark tint of the surface hue.
- Gradients stay subtle and in-hue. Rainbow or high-delta gradients on every surface look cheap.
- A **pressed** state removes the lip: the face travels down by about 80% of `depth` and the lip nearly disappears.
- A **disabled** state desaturates the face and keeps the shape unchanged. WCAG exempts disabled controls from its contrast minimums, but the player should still be able to tell what the control is and why it is unavailable: aim for a label of roughly 3:1 or better on the disabled face, and say why it is disabled where that matters (for example a price or a lock reason). The template values do **not** reach that yet. Measured label contrast is about 2.6–3.3:1 for the View drawables (`#3B3A40` on `#8E8A93` / `#7B7780`) and about 2.6–3.6:1 for Compose `asDisabled()` across the three default token sets. Retune the disabled label color against your final face colors.
- Cache heavy drawing (`drawWithCache`, reused `Paint`/`Path`) and avoid stacking many blurred shadows (see section 9).

Working implementations: `assets/templates/compose/GameButton.kt`, `GamePanel.kt`; `assets/templates/views/res/drawable/bg_game_button*.xml`, `bg_game_panel.xml`.

## 5. Component anatomy

| Component | Must have | Common mistakes |
|---|---|---|
| Primary button | One per screen; label ≥ 4.5:1 against both gradient ends; min height 56 dp; pressed/disabled/loading states | Two competing primaries; white label on a light gold face; no pressed state |
| Secondary/ghost button | Visibly lower emphasis (flatter, lower saturation) | Same weight as primary |
| Icon button | 48 dp hit target even if the glyph is 24 dp; content description | Glyph-sized hit box |
| Panel/card | Clear edge, optional title band, inset well for content, consistent padding (a multiple of 4 or 8 dp) | Every section boxed in an identical card |
| Resource chip (coins/gems/energy) | Icon + tabular number + optional "+" action; compact number format; count-up on change | Layout shift when digits change; localized separators ignored |
| Progress/health bar | Track, fill, optional trail for damage, numeric/text cue, `progressBarRangeInfo` semantics | Color-only low state; animation that lags behind the real value |
| Badge / notification dot | Shape + position cue; clears when seen | Red dots everywhere (alert fatigue) |
| Rarity frame | Ramp (common → legendary) plus a **non-color cue** (frame shape, corner ornament, or star count) | Color-only rarity |
| Toast / reward popup | Short, non-blocking, auto-dismiss, never covers the control the player needs next | Modal popups for trivial events |
| Dialog / modal | Dim scrim, one primary + one dismiss, Back closes it, state reflects current data on reopen | Stale snapshot data; Back not mapped |
| Tab bar / segmented control | Selected = shape + weight + color; stable width | Selected state only by hue |
| Slot (inventory/item) | Item art, count, state overlays (locked/new/equipped), 48 dp+ hit box | Text baked into images |

## 6. Screen blueprints

Each blueprint gives the focal point and the states to cover. Adapt to the product; do not copy blindly.

**Main menu** — Focal point: the primary "Play/Continue" button or the key art. Keep secondary entries (settings, shop, events) visually quieter. Cover: first launch vs. returning, loading/sign-in state, offline, new-content indicators, version/legal text placement. Avoid six equal-weight buttons stacked vertically.

**Level / stage select** — Focal point: the current/next level. Show completed, current, locked, and recommended states with shape + icon + color. Keep progress (stars/score) legible; large worlds scroll with a persistent header. Cover: first level, all complete, locked-tapped feedback (explain why).

**Pause** — Focal point: Resume. Keep it fast to dismiss, dim the game, freeze game time through the existing game-state owner (do not reimplement it in UI). Quit/restart require confirmation if progress is lost. Back = Resume.

**Result (win/lose)** — Focal point: the outcome and the next action. Sequence the reveal (outcome → score/stars → rewards → buttons) with an always-visible skip. Primary = Next/Continue; secondary = Retry/Home. For losses, avoid shaming copy; offer a clear retry path. Cover: new record, no reward, reward pending/claim failure, ad/purchase unavailable.

**Shop / IAP** — Focal point: the featured or recommended bundle only if it is genuinely relevant. Make price, currency, quantity, and what is gained unambiguous; show loading/failure/restore/purchase-pending states; never hide prices; disable the button during in-flight purchase. Do not alter billing logic in a visual task.

**Inventory / collection** — Grid or list-detail; selected item detail with stats and a single primary action (Equip/Upgrade). Show empty, locked, new, equipped, and insufficient-resources states. Use stable slot sizes so counts changing never shift the grid.

**Settings** — Group by task (Sound, Controls, Display, Accessibility, Account). Immediate feedback on toggles; destructive items (reset progress, delete account) are separated and confirmed. Include HUD scale, haptics, reduced motion, and language where the product supports them.

**Loading** — Show real progress when known, otherwise an indeterminate indicator with a timeout path to an error/retry state. Do not leave a permanent spinner.

**Daily reward / event** — Focal point: today's claimable reward. Past days dimmed with a check, future days previewed. Claim button has loading, success, and already-claimed states; handle time/offline errors.

**Tutorial hint** — Anchored to the relevant control, dismissible, never blocking required input; one idea per hint; resumable after process death if the project persists tutorial progress.

## 7. Typography for games

- Roles: display (titles, big numbers), UI label (buttons), body, caption, numeric. Use the display face sparingly (titles and key numbers); long-form text uses a highly legible face.
- **Vietnamese and other diacritic-heavy languages:** many decorative game fonts lack complete coverage. Test strings with stacked marks such as `Ắ Ằ Ẳ Ẵ Ặ Ế Ệ Ộ Ợ Ữ Ự Ỵ` and lowercase equivalents. Allow extra line height so marks do not clip, and verify that fallback fonts do not look mismatched. Verify license and glyph coverage before bundling any font.
- **Outlined/shadowed text** for readability over busy backgrounds: Compose: `OutlinedGameText` draws eight offset copies in the outline color under the fill, which works on any Compose version; Views use `shadowColor/shadowDx/shadowDy/shadowRadius` or a custom `onDraw`. Hide the outline layer from accessibility.
- **Tabular numerals** for changing numbers: `fontFeatureSettings = "tnum"` (Compose) / `setFontFeatureSettings("tnum")` (Views) only works when the font supports the feature; otherwise reserve width or right-align.
- **Compact number format** (`1.2K`, `3.4M`) with the user's locale; keep the full value available to accessibility (`contentDescription`).
- Never bake text into bitmaps; it cannot be localized, scaled, or read by TalkBack.
- Provide fitting rules for long translations: allow two lines, then ellipsize; for single-line chips use auto-size (`TextView` auto-size APIs for Views; in Compose, check the installed version for text auto-size support, otherwise measure).

## 8. Getting a good look without an artist on staff

- Prefer **vector drawables, shape/layer-list drawables, and procedural Canvas/Compose drawing** for frames, buttons, bars, and glows; they are crisp at every density and tiny.
- Use bitmaps (WebP) only for real art (characters, key art, icons with detail). Provide the right density buckets and avoid upscaling small bitmaps.
- Keep an **icon system**: one stroke weight, one corner style, one fill style. Mixing outline and filled icon sets from different packs is the fastest way to look amateur. Check icon licenses.
- Use `RenderEffect` blur (API 31+) only with a fallback for lower APIs and low-end devices.
- Add a little texture sparingly (subtle noise or pattern at low alpha) only if the style preset calls for it and it does not hurt compression or performance.

## 9. Performance budget for beautiful UI

- Many stacked translucent layers, large blurs, and per-frame allocations hurt low-end devices. Measure with GPU rendering/overdraw tools, Layout Inspector, and Perfetto where available.
- Compose: hoist `Brush`, `Path`, and `TextMeasurer` results with `remember`/`drawWithCache`; read animated state in the draw or layout phase (`drawBehind`, `graphicsLayer {}`, lambda `offset`) to avoid recomposition per frame.
- Views: reuse `Paint`/`Path`/`RectF`, avoid allocation in `onDraw`, use hardware-layer animation of properties (`translationX/Y`, `scaleX/Y`, `alpha`).
- Define quality tiers (high / reduced / minimal) controlling blur, particle counts, and idle animations; default low-RAM devices (`ActivityManager.isLowRamDevice`) to a reduced tier if the project has a settings mechanism.

## 10. Polish pass (run before reporting "done")

Run on a real or emulated render when tools are available; otherwise label the result "source reviewed, not visually verified".

1. **Five-second test:** can a new player tell what the screen is for and what to tap?
2. **Squint test:** blur the screenshot; is there one clear focal point?
3. **Grayscale test:** does hierarchy survive without hue? (Value contrast carries structure.)
4. **State coverage:** normal, pressed, disabled, loading, error, empty, locked, selected, success.
5. **Alignment:** a 4/8 dp rhythm; consistent paddings; optical alignment for icons and numerals.
6. **Consistency:** one edge treatment, one light direction, one icon style, one corner language per component family.
7. **Text stress:** longest translation, large font scale, Vietnamese stacked marks, big numbers (`9,999,999`).
8. **Touch:** thumb reach on a compact phone in landscape/portrait as supported; no controls in system gesture regions.
9. **Motion:** every animation explains something; nothing blocks input; reduced motion honored.
10. **Performance:** no obviously heavy layers on the scrolling/animated path.

### Generic "AI-looking" tells and what to do instead

| Tell | Replace with |
|---|---|
| Every surface is the same rounded card with a soft shadow | Vary surface types by role (panel, inset well, flat row, floating chip) within one material system |
| Purple-to-blue gradient everywhere | A palette derived from the product's art; one accent used for one meaning |
| Emoji or default Material icons as the whole icon language | A consistent custom/licensed icon set or drawn vector icons |
| Same 16 dp radius on all things | Radius scale tied to component size (small chips < buttons < panels) |
| Glow on everything | Glow only on the single element that should draw the eye |
| Centered stack of equal buttons | A clear primary, quieter secondaries, grouped utilities |
| Decorative looping animation | Motion tied to state change or guidance, with an end |

## 11. Working method for visual tasks

1. **Brief (3–5 lines):** product/genre, audience, existing assets, direction (or "none — propose presets"), constraints.
2. **Spec:** a short token table (colors, radii, strokes, depth, type roles, motion durations) that maps to the project's existing tokens where they exist.
3. **Implement** with the existing architecture; reuse or adapt `assets/templates/` only after replacing placeholder tokens, package names, and checking `minSdk` and dependencies.
4. **Render and critique:** capture the screen (emulator/device/screenshot test/Compose preview), judge it against section 10, fix, repeat at most 2–3 rounds.
5. **Report** using the handoff template in 06, stating what was and was not visually verified.
