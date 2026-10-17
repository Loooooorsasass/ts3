# 11 — Review and audit rubric

Load this reference when the request is a review, audit, "what is wrong with this screen", a PR review, or a pre-release UI check. A review answers one question: *what should the owner know and fix, in what order?* It is read-only unless the person asks for edits.

## 1. Ground rules

- **Report, do not fix.** Make no edits during a review. Offer the fixes afterwards.
- **Every finding needs evidence:** a file path with a line or symbol, what was observed, and why it matters to a user. Something you suspect but cannot point to goes under "Questions", not "Findings".
- **Judge against the project's own rules first** (its tokens, conventions, brand, supported devices), then platform guidance, then taste. Label taste as taste.
- **Check that code is active** before calling it dead or legacy (see `01`).
- **Use the labels from `01` and `06`:** Observed, Inferred, Not verified. Never imply you ran, rendered, or tested something you only read.

## 2. Procedure

1. **Scope.** Which screen, flow, or diff? What is out of scope? What will the owner do with the result (release gate, redesign input, learning)?
2. **Map.** Entry point, state owner, tokens, strings, and tests for the slice (`01`, short form).
3. **Walk the lenses** in section 3. Use only the lenses that apply to the surface. Add the game lens for game-style screens.
4. **Probe instead of guessing.** For each suspect, find the cheapest evidence: grep the call sites, read the test, check the manifest, run the narrowest command. A runnable check beats a theory.
5. **Rank.** Merge duplicates, drop what you cannot support, order by user impact (section 5).
6. **Report** with the template in section 6.

## 3. Lenses

| Lens | Look for | Reference |
|---|---|---|
| Behavior and state | Two sources of truth; dialogs showing a stale snapshot; lambdas capturing stale state; a spinner that can never end; actions that silently drop edits | `03` |
| Navigation and Back | Several `BackHandler`s with unclear priority; cancel that commits; destructive actions without confirmation | `03` |
| View, Canvas, interop | Listeners attached on every recomposition; allocation or blocking work in `onDraw`; drawing geometry that differs from hit-test geometry | `04` |
| Accessibility | Targets under 48 dp; missing or duplicated descriptions; custom controls without semantics; state shown by color only; contrast; focus and traversal order | `05` |
| Localization | Hard-coded strings; concatenated sentence fragments; clipping with long text or Vietnamese stacked marks; RTL | `05` |
| Window and insets | Insets applied twice; IME covering fields; controls under gesture navigation; a phone layout merely stretched on large windows | `05` |
| Design system | Literals instead of tokens; one radius or card style everywhere; competing primary actions; missing states | `02` |
| Game UI | HUD covering the play field; controls near system gesture edges; unclear pointer ownership; no feedback on press | `08` |
| Motion and haptics | Motion that blocks input; no reduced-motion path; endless loops; haptic spam | `09` |
| Performance | Stacked blurs; per-frame allocation; recomposition on animated paths. Claim a problem only with a measurement or a clear mechanism in the code | `08` §9, `09` |
| Tests | See section 4 | `06` |
| Secrets and privacy | Credentials, tokens, or user data in code, logs, or reports | `SKILL.md` contract 10 |

## 4. Reviewing tests, templates, and ported snippets

A passing test only helps if it can fail. For each test, ask:

- **Does it read the thing that would break,** or a value derived from its own input? For example, semantics that report the *target* value cannot show that an animation reached it.
- **Would it still pass if the feature were removed?** Mutate the code in your head.
- **Does it assert behavior** (the callback was not called) **or an implementation detail** (a particular semantics action exists)? Prefer behavior.
- **Is it deterministic?** Controlled clock, fixed locale, no sleeps.

For ported templates and copied snippets, also check:

- **Theme leakage.** A default style attribute from the app theme can alter a custom component, for example a Material `buttonStyle` tint applied over a custom background by `AppCompatButton`.
- **Caches with the wrong key.** A process-wide cache wrapped in `remember(context)` will not update when the cache is refreshed.
- **Comment and code disagreeing** about geometry or behavior.
- **API-level and version claims.** Confirm them against the official docs. Level numbers are easy to misremember.

## 5. Severity and confidence

Rank by user impact times likelihood, not by effort or by how easy the problem was to spot.

| Severity | Meaning |
|---|---|
| Blocker | The main task cannot be completed, progress or data is lost, the app crashes, purchase or auth is broken, or an accessibility barrier locks a group out of the main path |
| High | The main path works, but a real group of users is badly affected: a stuck state that only a restart fixes, Back doing the wrong thing, a primary action clipped at a common font scale, a primary control far below 48 dp |
| Medium | A clear defect on a secondary path or edge case: inconsistent states, low contrast on secondary text, no reduced-motion path |
| Low | Polish: small inconsistencies, minor misalignment |
| Note | An observation or follow-up that is not a defect |

Add a confidence label to each finding:

- **Confirmed:** you ran or rendered it and saw the problem.
- **Likely:** you read the code and the mechanism is clear, but you did not run it.
- **Possible:** it needs a specific check, which you name.

## 6. Report template

```text
## Review: <scope>

**Verdict:** one or two sentences.
**Reviewed:** files, screens, commands.
**Not reviewed or not verified:** what you could not see or run.

### Findings (highest impact first)
1. [High · Likely] Short title — `path/File.kt:123`
   Observed: what the code or screen does.
   Impact: who is affected and how.
   Fix: the smallest change, and what to re-test.
2. ...

### Working well (keep)
- ...

### Questions for the owner
- ...

### Suggested order of work
1. ...
```

Rules for the report:

- Keep the main list to about ten findings. Group repeats into one finding ("12 places use literal colors: ...").
- Include what works, so the owner knows what to preserve.
- Write the report in the person's language. Keep code identifiers, paths, and commands verbatim.
- State plainly what was not verified. "Source reviewed" is not "tested".

## 7. Reviewer anti-patterns

- Padding the list with nits to look thorough.
- Calling taste a defect.
- Proposing a rewrite when a few lines fix it.
- Flagging "missing X" without checking the project's other mechanism for X, such as a localization abstraction.
- Saying "looks fine" without saying what was looked at.
- Saying a screen "passes accessibility" from reading code alone.
