# 10 — Style presets (for projects without an established visual direction)

**When to use this file.** Only when the repository has no clear visual direction for the target screen (new project, placeholder UI, or the user asks for a fresh look). If the app already has a brand, art, or tokens, extend those and ignore this file.

**How to use it.**
1. State the genre/audience in one line.
2. Offer 2–3 presets that fit, with one sentence on the feel of each, and let the user choose. If you cannot ask, pick one, say so as an assumption, and keep tokens centralized so it is easy to swap.
3. Map the preset into the project's token system (Compose theme / `values/` resources) — do not scatter literals.
4. Replace the example hex values with the product's art colors when real art exists. The presets are **examples to start from, not defaults to impose.**
5. Re-check contrast and Vietnamese/long-string behavior after any change.

The contrast ratios below were computed with the WCAG relative-luminance formula for the exact hex pairs listed. A ratio only describes that pair; re-verify if you change a value. Large bold text may use 3:1, UI shapes 3:1, normal text 4.5:1.

**Fonts:** names are *candidates to evaluate*. Before bundling any font, verify license, weights, file size, and glyph coverage for every supported language (for Vietnamese test stacked diacritics). If coverage is incomplete, choose another face or define a fallback.

---

## Preset A — Candy Cartoon (casual, bright, friendly)

Feel: chunky, playful, high-energy; thick edges and hard "lip" shadows; warm primary on a deep cool background.

| Role | Hex | Notes |
|---|---|---|
| Background | `#25265A` | Deep indigo |
| Surface | `#34377C` | Panels |
| Surface high | `#444A9A` | Inset wells/selected rows |
| Text | `#FFFFFF` | 13.97:1 on background, 10.59:1 on surface |
| Text muted | `#C9CDFF` | 6.89:1 on surface |
| Primary face (top → bottom) | `#FFC94A` → `#FF9F1C` | Gold/orange action |
| Primary edge / lip | `#A85A00` / `#7A3F00` | |
| On-primary label | `#3A1F00` | 9.96:1 on the top, 7.43:1 on the bottom |
| Secondary face | `#5663E8` → `#3B47C9` | White label: 4.83:1 on the top, 7.17:1 on the bottom |
| Danger face | `#D2354D` → `#B8243C` | White label: 4.82:1 on the top, 6.26:1 on the bottom |
| Accent (teal) | `#2EE6C8` | 6.69:1 on surface |
| Success | `#5CD65C` | 5.66:1 on surface |
| Warning | `#FFD23F` | 7.33:1 on surface; pair with an icon |
| Danger text | `#FF8A9B` | 4.72:1 on surface, 6.23:1 on background; only 3.48:1 on surface high, so not for small text there |

Shape: radius 16–20 dp (buttons), 20–24 dp (panels); edge 2–3 dp; lip depth 5 dp; panel shadow offset 4 dp.
Type: rounded, heavy display for titles/buttons; clean sans for body. Candidates to evaluate: Baloo 2, Nunito, Be Vietnam Pro.
Motion: springy, slight overshoot, bright reward bursts.
Pitfalls: the danger *face* color `#D2354D` used as text on the surface is only 2.20:1, and the brighter bar red `#FF5468` reaches 3.39:1. Use the Danger text color for red text, or keep red for shapes/icons and put a light label on a danger face.

## Preset B — Neon Sci-Fi (dark, technical, competitive)

Feel: dark glassy panels, thin luminous strokes, chamfered corners; restrained glow.

| Role | Hex | Notes |
|---|---|---|
| Background | `#070B1A` | |
| Surface | `#0F1730` | |
| Surface high | `#18244A` | |
| Text | `#E8F1FF` | 17.22:1 on background |
| Text muted | `#9FB3D9` | 8.37:1 on surface |
| Primary (cyan) | `#27E1FF` | Label `#021A22` is 11.33:1 on it |
| Accent (magenta) | `#FF3DCB` | 5.72:1 on surface; use for one meaning only |
| Success | `#3DF2A0` | 12.15:1 |
| Warning | `#FFC247` | 11.02:1 |
| Danger | `#FF5A75` | 5.88:1 |

Shape: chamfered/cut corners drawn with `Path` (Compose `GenericShape`/custom `Shape`, or `Path` in Views) instead of uniform rounded rectangles; 1–1.5 dp strokes at partial alpha; at most one glow (the active/selected element).
Type: geometric/technical sans, tabular numerals for stats. Candidates to evaluate: Exo 2, Rajdhani, Be Vietnam Pro (for Vietnamese body text).
Motion: fast and crisp (100–200 ms), sliding panels, scanline/shimmer only on loading.
Pitfalls: glow and blur are expensive on low-end devices — gate them behind a quality tier; do not set body text in thin, low-alpha strokes.

## Preset C — Parchment & Gold (fantasy RPG, collection games)

Feel: warm, tactile, ornamental; double borders and inked text on parchment; metallic accents used sparingly.

| Role | Hex | Notes |
|---|---|---|
| Background (dark wood) | `#2B1D14` | |
| Panel (leather/wood) | `#4A3322` | Light text `#F3E6C8` is 9.49:1 |
| Parchment | `#E8D5A8` | Ink `#3B2A1A` is 9.47:1 |
| Gold | `#D4A437` | Label `#2B1D14` is 7.11:1; gold on dark wood is 7.11:1 |
| Crimson | `#A32A2A` | White label 7.19:1 |
| Forest | `#3F7D4E` | White label 4.94:1 |

Shape: 8–12 dp radii or squared corners with ornamental corner pieces (vector drawables); double stroke (outer dark, inner gold at partial alpha); plaques/ribbons for titles.
Type: a serif or "storybook" display face for titles; a readable humanist face for body/stats. Many ornate display faces lack Vietnamese glyphs — verify.
Motion: slower and heavier (250–400 ms), paper-like fades, wax-seal/stamp pop on claims.
Pitfalls: busy textures behind text; parchment noise should stay below legibility thresholds (keep text on flat inset areas).

## Preset D — Clean Flat Puzzle (minimal, casual, light/dark capable)

Feel: calm, spacious, content-first; soft 1 dp outlines instead of heavy shadows; color used for meaning.

| Role | Hex | Notes |
|---|---|---|
| Background | `#F6F4EF` | |
| Surface | `#FFFFFF` | |
| Ink | `#1E2430` | 14.15:1 on background |
| Ink muted | `#5B6475` | 5.42:1 on background, 5.96:1 on surface |
| Primary | `#3D5AFE` | White label 5.13:1 |
| Accent (coral) | `#FF6B57` | Only 2.80:1 on white → shapes/illustration only, not text |
| Success text | `#167A4F` | 5.34:1 on white |
| Danger text | `#D93A3A` | 4.55:1 on white |

Shape: 10–14 dp radii; 1 dp outlines; elevation via tonal difference more than shadow.
Type: a clean sans with good numerals; large score numerals as the focal point. Candidates to evaluate: Be Vietnam Pro, Inter.
Motion: subtle (120–250 ms), small scale/fade; celebrate completion once with a light burst.
Pitfalls: becoming bland — pick one memorable element (bold numeral treatment, a signature color, an illustrated empty state) and keep it consistent. Provide a real dark theme rather than inverting.

## Preset E — Pixel / Retro Arcade (pixel art games)

Feel: crisp, blocky, limited palette, integer-scaled art.

| Role | Hex | Notes |
|---|---|---|
| Background | `#14121F` | |
| Surface | `#2E2A4A` | |
| Text | `#F2E9D8` | 15.34:1 on background, 11.27:1 on surface |
| Primary (yellow) | `#FFD04A` | Label `#14121F` is 12.65:1 |
| Danger | `#FF5E5B` | 4.53:1 on surface |
| Success | `#4FD18B` | 7.00:1 |
| Info | `#5AA9FF` | 5.53:1 |
| Magic | `#A06BFF` | 3.91:1 on surface → UI shapes/large text only; use `#B38CFF` (5.25:1) for small text |

Shape: 0 radius or stepped "pixel" corners; 2–4 dp borders (multiples of the art's pixel size); hard 1-pixel-step shadows; no gradients or very banded ones.
Rendering: draw bitmaps with nearest-neighbor scaling (`Paint.isFilterBitmap = false`; Compose `FilterQuality.None`), at integer scale factors, and align to the pixel grid to avoid shimmering.
Type: a pixel font for headings only; fully verify language coverage — most pixel fonts lack Vietnamese diacritics, so plan a clean fallback for body text.
Motion: stepped/low-frame-rate easing (`snap`-like), flicker only when it does not harm readability; honor reduced motion.
Pitfalls: pixel font at non-integer sizes looks blurry; mixing smooth gradients into a pixel style.

---

## Shared semantic rules for every preset

- **Semantic colors keep one meaning across the whole app** (success, warning, danger, info, premium). Do not reuse the danger color for decoration.
- **Rarity ramp** (example values to adapt): common `#9AA3B2`, uncommon `#4FD18B`, rare `#5AA9FF`, epic `#B38CFF`, legendary `#FFC247`. Always add a non-color cue (frame ornament, star count, label).
- **Token naming** follows the project; if none exists, prefer semantic names (`surface`, `onSurface`, `primaryFaceTop`, `lip`, `edge`) over visual names (`blue500`).
- **Dark/light:** define both only if the product supports both; do not invert mechanically.
- **Quality tier:** each preset lists effects (glow, blur, particles). Map them to tiers so low-end devices drop the expensive ones first.
