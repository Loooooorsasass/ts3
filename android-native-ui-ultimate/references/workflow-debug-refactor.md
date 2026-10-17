# Workflow: UI Bug and Refactor

## Activate when

Fixing a UI defect/regression or restructuring UI code while intending to preserve behavior.

## Inspect before editing

Capture the exact symptom and reproducible steps. Read the target, caller, state owner, route, lifecycle/subscriptions, relevant resource/theme, recent diff if available, existing tests, and toolchain. Establish whether the path is active before calling it legacy. Load `01`, `03`, `04` when interop is involved, and `06`; consult `11` for review-only work.

## Procedure

1. Reproduce the failure or mark reproduction unavailable. Read complete failure output; distinguish expected failure from test/setup failure.
2. Trace the value/event from source through state owner and UI to the observed result. Check timing, stale callbacks, lifecycle, configuration changes, focus, and resource variants as relevant.
3. Write a falsifiable root-cause hypothesis and one narrow regression check. For behavior that can be tested, make the check fail for the defect before fixing it.
4. Change the smallest owning layer. Refactor only what is needed to make the correction understandable and maintainable; preserve public behavior and renderer/state contracts.
5. Rerun the regression check and nearby affected tests. Broaden to compile/lint or integration only when risk warrants it.

## Choose the solution

Fix the state transition, constraint, lifecycle ownership, or rendering cause rather than masking the symptom. Avoid speculative API migrations, global cleanup, new abstractions for one-off code, and changing gameplay/business rules as a side effect.

## Verify

Use the cheapest discriminating check first, then relevant unit/UI tests and module compile. For visual defects, inspect before/after renders at the failing state and size. For lifecycle defects, exercise leave/re-enter, pause/resume, or recreation as relevant. If regression coverage is impossible, explain why and name the remaining risk.

## Accept when

The original failure is reproduced or the causal path is evidenced; a regression check covers the behavior; the focused checks pass; and unrelated behavior remains unchanged to the extent tested.

## Evidence boundary

Do not state “root cause fixed” when only the symptom disappeared or the trigger was not reproduced. A source-level trace is not runtime proof. Report any missing repro or device coverage.