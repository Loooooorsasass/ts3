# 02 — Design system and visual quality

## Start with the product, not a template

Determine what the app is for, who uses it, which action matters most on the target screen, and what existing branding/assets should remain. Inspect neighboring screens before introducing a new visual language. Write a one-sentence direction before coding, for example “quiet editorial utility with high-contrast typography” or “compact tactical game HUD.” Such directions are examples only, not defaults.

## Audit tokens first

Find the existing equivalents for:

- Background and layered surfaces.
- Primary/secondary/muted text.
- Primary action, selected state, and semantic success/warning/error colors.
- Typography roles, weights, line heights, and number styles.
- Spacing scale, component sizes, radii, strokes, elevation/shadow, and motion.
- Icon sizes, image treatments, and focus/pressed/disabled states.

Reuse and extend these tokens instead of scattering literals. A color's meaning should remain consistent across screens. Dark/light themes should retain contrast and semantics, not just invert values mechanically.

## Visual hierarchy rules

- Give each screen one clear focal point; then organize secondary and tertiary information.
- Use space, typography, alignment, scale, and grouping to show priority—not accent color alone.
- Reserve saturated accents for states and actions that truly need emphasis.
- Avoid identical full-width cards stacked vertically when rows, dividers, grouping, tabs, list-detail, a hero plus supporting content, or direct inline controls are clearer.
- Vary radii and surface treatments deliberately. Not every interactive surface needs the same capsule or rounded rectangle.
- Use custom/project assets when relevant. Material icons are useful, but they need a consistent visual role and should not be the app's only source of identity when illustration or branded iconography already exists.
- Keep motion functional: explain transitions, progress, selection, expansion, or feedback. Do not delay urgent input for decoration.
- Design all meaningful states: loading, empty, populated, selected, disabled, error, offline, permission-denied, confirmation, completion, and recovery.

## Compose-specific guidance

- Use the installed version's actual Material/Material 3 APIs. Inspect the version catalog and existing theme setup before using recently introduced APIs.
- Prefer Material components when they provide expected semantics and interaction; customize their colors, typography, shapes, and layout to fit the app.
- Provide reusable composables when there is real shared behavior or repeat usage. Avoid abstracting a one-off element prematurely.
- Use stable keys for lazy/repeated content and sensible state hoisting.
- Avoid recomposition-heavy patterns and expensive calculations in UI composition; profile before attempting speculative micro-optimizations.

## Typography and fonts

Establish a meaningful hierarchy for screen title, section title, body, label, helper text, error text, and numeric values. Support user font scaling and long translated strings. Before adding a font, verify weight files, shaping/Unicode coverage for supported languages, file size, and license. Do not claim a custom font is superior without evaluating legibility on-device.

## Design review checklist

- Is the next action obvious?
- Does the primary task dominate the visual hierarchy?
- Are accent colors semantically meaningful rather than everywhere?
- Are spacing, typography and component states consistent?
- Is the visual language distinct but compatible with the existing brand?
- Can users distinguish selected, disabled, error and success states without color alone?
- Are dynamic values/translated labels likely to cause layout shifts or clipping?
- Is animation explaining a real state change?
