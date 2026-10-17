# 05 — Accessibility, localization, and adaptive layout

## Accessibility baseline

- Prefer semantic interactive controls over custom clickable containers when feasible.
- Provide meaningful content descriptions for informative icons/images and custom-rendered controls. Mark purely decorative imagery as decorative when supported. Avoid reading the same label twice.
- Aim for 48 × 48 dp touch targets where practical; do not rely on the visible glyph's size as the hit-box size.
- Inspect contrast for body, muted, disabled, placeholder, error, success, selected, and overlay text. Use labels/shapes/icons in addition to color.
- Verify TalkBack labels, traversal order, meaningful grouping, keyboard/switch focus where relevant, touch exploration, text scaling, and dynamic updates.
- Respect system animation preferences when supported. Avoid unnecessary flicker, rapid flashing, or motion that obscures state.

## Localization

- Use the project's established Android string resources or localization abstraction. Do not hard-code user-facing strings when neighboring code has a string system.
- Inspect actual translations and fallback behavior. A locale being selectable does not prove every label/dialog/error is translated.
- Allow for long labels, variable word order, pluralization, bidi text, right-to-left layouts, locale-specific date/number formats, and font fallback.
- Use start/end-relative layout concepts and direction-aware icons when appropriate instead of assuming left-to-right.
- Do not concatenate sentence fragments in ways that prevent natural translation. Keep dynamic values correctly formatted and avoid translating identifiers accidentally.
- If adding or changing locale behavior, test the actual supported locales for the touched flow and state which locales were not checked.

## Edge-to-edge and insets

- Inspect the target SDK, edge-to-edge setup, `Scaffold` insets behavior, and existing inset handling first.
- Ensure critical content, buttons, bottom bars, sheets, and text fields remain accessible around status/navigation bars, cutouts, rounded corners, gesture regions, and the IME.
- Avoid applying the same inset twice. Verify overlays and custom views as well as ordinary Compose content.
- Current platform behavior changes over time; consult the current Android documentation and the project's actual target SDK when making compatibility claims.

## Adaptive layouts

Use the app window's available size instead of inferring the layout solely from a phone/tablet label or physical screen size. Where supported and useful, use window-size classes/adaptive libraries to decide layout structure. Do not only scale a phone layout up to fill a tablet.

Consider:
- Compact windows: concise hierarchy, reachable primary actions, appropriate bottom navigation, low horizontal density.
- Medium/expanded windows: navigation rail, list-detail, multi-pane, wider content constraints, or extra contextual information when it benefits the task.
- Landscape, split screen, folding/posture, resizing, IME-visible state, and configuration changes where supported.
- Game surfaces that need aspect-ratio-aware sizing or letterboxing rather than stretching.

Do not add adaptive dependencies or code paths before confirming the project supports them and the supported device scope makes them useful.

## Verification matrix

For a meaningful UI change, pick representative cases:

| Dimension | Cases |
|---|---|
| Window | Compact phone; expanded/tablet if in scope; landscape/resized if supported |
| Text | Default scale; enlarged font; longest affected translation |
| Locale/direction | Primary locale; another supported locale; RTL if claimed supported |
| Accessibility | TalkBack label/order for key actions; focus; contrast; non-color cues |
| System UI | Insets with gesture navigation; IME visible for forms; overlays/dialogs near edges |
