# Workflow: Testing and Release Review

## Activate when

Planning UI test coverage, reviewing a release/PR, validating templates or this skill package, or reporting completion of Android UI work.

## Inspect before editing

Read CI workflows, Gradle wrapper and version catalog, test source sets, existing unit/instrumented/screenshot/benchmark tooling, known failures, release checklist, and the target change. Load `06` and `11`; for package validation use `scripts/validate_skill.py` and `benchmarks/README.md`.

## Procedure

1. Translate the change into observable acceptance criteria and risk. Separate existing failures from new failures.
2. Select the cheapest discriminating check first: focused unit/regression, compile/lint, UI interaction, screenshot, integration, device, or profiler.
3. For bug fixes, ensure the regression check fails for the target behavior before the implementation where practical. For screenshot tests, inspect reviewed baselines; never auto-accept unexplained image changes.
4. Run the required checks and record exact commands, versions, scope, result, and environment. If a dependency/device is missing, run remaining checks and mark the blocked layer explicitly.
5. Review behavior, resources/imports/API levels, lifecycle cancellation, accessibility/localization, secrets, license/provenance, internal links, and archive contents as applicable.
6. Report VERIFIED, PARTIALLY VERIFIED, or UNVERIFIED per claim. List blockers and a reproducible next command.

## Choose the solution

Use existing repository test frameworks and wrappers. Do not invent Gradle tasks or dependency versions. Prefer behavior assertions over implementation details; choose screenshots for stable visual regressions, device tests for platform behavior, and benchmarks for performance. Do not add tooling without a concrete gap and maintenance owner.

## Verify

Run the package structural validator and unit tests for this bundle. In an Android project, run only supported wrapper tasks and report exact task names. Inspect produced ZIP listing and validate the unpacked artifact rather than trusting the pack command exit alone.

## Accept when

Acceptance criteria have reproducible evidence or are marked unverified; changed files and generated artifacts are scoped; documentation agrees with actual results; and no known failing check is described as passing.

## Evidence boundary

A passed structural test does not prove Android compilation, UI quality, accessibility, policy compliance, or agent effectiveness. Do not invent benchmark scores or success-rate improvements.