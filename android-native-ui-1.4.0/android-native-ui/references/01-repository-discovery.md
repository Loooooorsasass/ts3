# 01 — Repository discovery and architecture

## Objective

Create a small, evidence-based map of the target feature before proposing edits. Never begin with an assumed framework or assumed package tree.

## Inspection sequence

1. **Identify stack and build:** list project roots and modules; read README/build docs, settings, root/module Gradle scripts, version catalog, wrapper, manifest, and source sets. Detect Kotlin/Java versions, Compose BOM or Compose dependencies, Material version, min/target SDK, and test setup.
2. **Locate app entry and routing:** launcher Activity/Application, `setContent`, XML entry layouts, navigation graph/router, deep-link definitions, and back handlers.
3. **Locate the active UI:** target screen, all call sites, component wrappers, resource layouts, preview/screenshot fixtures, and existing semantics/test tags.
4. **Trace state/data:** ViewModel, UI state model, event/command methods, repository/use cases, database/API boundary, persistence and async callbacks involved in the requested behavior.
5. **Read design foundations:** theme/color/typography/shape/spacing tokens, icon rules, image/font resources, reusable controls, and light/dark themes.
6. **Read language/accessibility conventions:** localization files or string abstraction, supported locales, font fallback, RTL logic, content descriptions, accessibility semantics.
7. **Check tests and app constraints:** unit/UI/instrumentation/screenshot tests, CI tasks, existing known failures, supported devices and user requirements.

## Build an architecture note

For the requested slice, record:

- Active file and owning screen/navigation route.
- State source of truth and state flow into the UI.
- User actions and callback path back to state/data.
- Components/tokens/assets to reuse.
- Localization/test conventions.
- Invariants and out-of-scope behavior.
- Important unknowns and commands that can verify them.

## Multiple or legacy implementations

When similar screens or packages exist, inspect references and runtime entry points. Search manifest declarations, router graph, deep links, reflection, dependency injection, resource names, tests, and callers before labelling code as obsolete. A legacy-looking path is a hypothesis, not evidence of dead code.

## Report format

Use concise labels such as **Observed**, **Assumed**, **Recommended**, and **Unverified**. Do not describe a source-level check as a build/runtime verification. Do not repeat secrets from configuration files while documenting architecture.
