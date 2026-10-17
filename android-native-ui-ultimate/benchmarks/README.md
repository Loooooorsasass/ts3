# Paired Agent Benchmark

## Purpose

Measure whether this skill improves Android game UI engineering on a fixed set of tasks. `tasks.csv` contains 24 task prompts and observable acceptance criteria. It is a benchmark specification only: no agent runs or results are included in this release.

## Controlled comparison

For every task, run one skill-off and one skill-on condition from the same clean base commit. Keep model/version, system prompt, user prompt, tools, temperature, token/time budget, dependency cache, test environment, and evaluator constant. Randomize condition order within each pair. Use at least five paired runs per task before making aggregate claims; retain all raw transcripts, diffs, commands, screenshots, and test results.

Do not let an evaluator know the condition when scoring the final artifact where practical. Score observable evidence, not whether the agent mentions the skill. Count incomplete/time-out results as failures for completion metrics and report them separately.

## Metrics

| Metric | Operational definition |
|---|---|
| Build success rate | Fraction of runs whose prescribed clean build succeeds. Report task/run denominators and environment. |
| Test pass rate | Fraction of prescribed relevant test suites passing after the final change; report pre-existing failures separately. |
| Regression rate | Fraction introducing at least one acceptance-breaking or previously passing behavior regression, adjudicated against baseline. |
| Requirement compliance | Per-task 0-2 on each explicit acceptance item: 0 absent/contradicted, 1 partial, 2 evidenced. Normalize and report item-level misses. |
| Accessibility coverage | Per-task 0-2 for the accessibility/input criteria specified in the row, backed by automated or manual evidence; source assertions alone cannot score 2 for device behavior. |
| UI quality | Blind reviewer score 1-5 using hierarchy, state coverage, consistency with repository art/tokens, responsive/localized layout, and screenshot evidence. Report inter-rater method. |
| Scope/maintainability | 0-2: unrelated changes/dependencies and complexity relative to task; cite diff evidence. |
| Effort | Tokens, elapsed time, and number of tool/test iterations, reported as context rather than quality. |

## Analysis

Report raw paired outcomes, mean/median and dispersion, task-level differences, failures, and sample size. For binary metrics report paired changes and uncertainty; do not turn a small sample into a universal quality claim. Publish the exact benchmark commit and scoring sheet. Use the same acceptance rubric for both conditions.

## Limitations

Results are model-, prompt-, repository-, and environment-dependent. UI quality requires rendered evidence and blinded human judgment. This suite is not yet calibrated, independently adjudicated, or executed. Do not claim effectiveness until a reproducible paired run is published.