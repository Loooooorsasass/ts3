# 03 — Navigation, state, and interaction

## Map the flow

For each affected feature, identify its entry point and possible states: initial/loading, content-ready, empty, validation error, service failure, blocked/permission-denied, modal, success, failure, and return. Draw a small state diagram or list transitions if the flow has more than a few states.

## State ownership

- UI renders authoritative state; it must not become the source of truth for business rules, game rules, purchases, progress, or persistence.
- Follow existing ViewModel/state holder and repository/use-case patterns. Do not introduce a new architecture simply because the current names differ from a preferred pattern.
- Keep ephemeral presentation state (for example, an open tooltip) local when appropriate. Put durable preferences/progress and cross-screen state in the established owner.
- Avoid two mutable sources of truth for a value. Define how optimistic updates, retries, cancellation, and failures reconcile with the authoritative source.
- Use lifecycle-aware collection and cancellation patterns already supported by the project. Clean up callbacks/listeners/subscriptions when owners or screens are destroyed.

## Back, cancel, and modal behavior

- Back should close the topmost transient surface first when that is the existing product convention, then return through the correct navigation stack.
- Cancel must not accidentally commit an action. Confirm destructive/irreversible operations and provide a recovery path where practical.
- Reopening a modal should show current authoritative data, not a stale snapshot.
- After closing overlays, restore a meaningful focus/selection location where applicable.
- Check system Back, toolbar Back, deep links, nested routes, and predictive-back behavior according to the project's Android versions.

## Interaction states

Buttons, controls, and list rows should communicate pressed, focused, selected, disabled, loading, and failure states as relevant. Disable repeated submission when necessary without hiding why an action is unavailable. For long tasks, avoid blocking the main thread; expose progress and cancellation/retry behavior when appropriate.

## Risks to inspect

- Duplicate event callbacks after recomposition.
- Lambdas captured with stale state in interop/listener bridges.
- Multiple BackHandlers with ambiguous enabled/priority state.
- Navigation that drops edits/progress unexpectedly.
- Service errors that leave a permanent spinner or misleading success message.
- UI counters/toggles that diverge from persistence or server state.
- Gestures competing with scrolling, nested containers, or system navigation.

## Regression testing

Test the user-visible sequence, not just a composable snapshot: entry → action → resulting state → Back/cancel → return to correct state. When data persists, verify recreation or restart behavior if it is in scope.
