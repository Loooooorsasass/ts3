#!/usr/bin/env bash
# Read-only checklist for porting the templates into a real project.
# Usage: bash port_check.sh /path/to/project
# It prints what still needs attention. It changes nothing.
set -u
ROOT="${1:?usage: port_check.sh <project-root>}"
SRC=(--include=*.kt --include=*.java --include=*.xml --exclude-dir=.git --exclude-dir=build --exclude-dir=generated)

echo "== 1. Template placeholders still present (replace with project tokens)"
grep -rn "PLACEHOLDER\|TEMPLATE" "$ROOT" "${SRC[@]}" 2>/dev/null | grep -v "/build/" | grep -v "assets/templates" | head -40
echo "   (none above = nothing left to review in project code)"

echo
echo "== 2. Template package name still used (com.example.game.ui.template)"
grep -rln "com.example.game.ui.template" "$ROOT" "${SRC[@]}" 2>/dev/null | grep -v "/build/" | grep -v "assets/templates" | head -20

echo
echo "== 3. Hard-coded colors in Kotlin/Java/XML (review and move to tokens)"
grep -rnE 'Color\(0x[0-9A-Fa-f]{6,8}|0x[0-9A-Fa-f]{6,8}|#[0-9A-Fa-f]{6,8}' "$ROOT" "${SRC[@]}" 2>/dev/null | grep -v "/build/" | grep -v "/generated/" | grep -v "assets/templates" | head -40

echo
echo "== 4. Toolchain versions (check minSdk and Compose/AppCompat versions against the templates)"
grep -rn "minSdk" "$ROOT" --include=*.gradle --include=*.gradle.kts --include=*.toml 2>/dev/null | head -5
grep -n "compose\|material3\|appcompat" "$ROOT/gradle/libs.versions.toml" 2>/dev/null | head -20
echo "   Templates need: minSdk >= 23 for the highlight layer; AppCompat for GameButtonView."
echo "   Note: Settings.Global animation-scale observation requires API 17+; RangeInfo requires API 19+; stateDescription requires API 30+."

echo
echo "== 5. APIs that depend on the installed version (confirm each one compiles)"
echo "   - HapticFeedbackType values passed by callers (GameButton defaults to no haptic):"
grep -rn "HapticFeedbackType" "$ROOT" --include=*.kt 2>/dev/null | grep -v "/build/" | head -5
echo "   - ProgressBarRangeInfo / progressBarRangeInfo (SegmentedBar):"
grep -rn "progressBarRangeInfo" "$ROOT" --include=*.kt 2>/dev/null | grep -v "/build/" | head -5
echo "   - AccessibilityNodeInfo.RangeInfo (SegmentedBarView; class is API 19+, constructor API 30+, obtain() deprecated in 33):"
grep -rn "RangeInfo" "$ROOT" --include=*.java 2>/dev/null | grep -v "/build/" | head -5
echo "   - Font feature 'tnum' (CountUpText, CountUp): requires a font with tabular figures; otherwise it has no effect."

echo
echo "== 6. Test sources in the project (templates in assets/templates/tests need these)"
find "$ROOT" -path "*/build" -prune -o \( -path "*/src/androidTest/*" -o -path "*/src/test/*" \) -name "*.kt" -print 2>/dev/null | head -20
