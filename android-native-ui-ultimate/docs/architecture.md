# Architecture

## Purpose

This bundle packages a reusable Android UI skill and supporting references without turning them into a stand-alone app. The root `SKILL.md` is the primary contract, while the `references/` folder holds domain-specific workflows and the `assets/templates/` folder contains reusable examples.

## Package layout

- `SKILL.md`: task router and operating contract
- `README.md`: package overview and purpose
- `references/`: source-agnostic Android UI guidance grouped by task domain
- `assets/templates/`: code examples for Compose, Views/XML, tests, and port-check tooling
- `docs/`: product-level evidence, source audit, and installation notes
- `scripts/`: validation and packaging automation
- `tests/`: structural validation tests for the skill bundle
- `benchmarks/`: benchmark task specification and scoring rubric

## Boundaries

This bundle intentionally does not claim a buildable Android app, runtime integration, or device validation. The included templates are examples only; they must be adapted to the consuming project and proven there with the repository's real toolchain.

## Evidence posture

The product follows a strict evidence model:

- VERIFIED: a check was actually run and command output confirms success
- PARTIALLY VERIFIED: only some portions were run
- UNVERIFIED: no reproducible check was run

Use `docs/verification-report.md` for the bundle-level evidence and `references/06-verification.md` for project-specific verification workflows.

## Design philosophy

The skill is repository-first and task-scoped. It aims to:

- inspect the actual project before prescribing a UI change
- preserve state ownership and gameplay/business logic boundaries
- favor narrow, observable checks over broad assumptions
- keep framework choice flexible (Compose, Views, Canvas, or hybrid)
- separate design guidance from backend, billing, anti-cheat, and server-authority concerns

## Source provenance

The package preserves the original archived source in its original folder and adds a new product wrapper around it. The historical audit is recorded in `docs/source-audit.md`.
