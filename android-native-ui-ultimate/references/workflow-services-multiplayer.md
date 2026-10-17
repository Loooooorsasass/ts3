# Workflow: Commerce, Services, and Multiplayer

## Activate when

Changing shop/purchase/ads/consent UI, sign-in, achievements, leaderboards, save/sync, matchmaking, reconnect, multiplayer results, or integrity messaging.

## Inspect before editing

Read the provider SDK/version and policy links, existing integration boundary, state machine, persistence/schema owner, network error/retry handling, consent/privacy flow, localization/accessibility, security documentation, and tests. Load `03`, then relevant `15`, `16`, or `17`; include `05` and `06` for player-visible work.

## Procedure

1. Identify trusted source of truth, client/server boundary, irreversible actions, consent requirements, and states already represented by the provider/domain layer.
2. Map idle, loading, pending, success, failure, cancellation, offline, retry, restore, stale/reconnect, and duplicate-submission states that apply. Preserve idempotency and transaction ownership.
3. Render factual, localized status and actionable recovery. Prevent repeat taps while a request is in flight, but do not invent entitlement, currency, result, identity, or server authority in the view.
4. Make the narrow UI change through the existing integration API. Keep provider calls, secrets, signing material, anti-cheat logic, and authoritative decisions out of presentation components.
5. Test success and failure/cancel paths with existing fakes/sandbox or integration test facilities. Review applicable current provider policies and API docs before release claims.

## Choose the solution

Reuse existing SDK adapters, repositories, state holders, and durable storage. A UI-only request should not rewrite the purchase flow, save schema, backend protocol, or matchmaking architecture. Ask for clarification only when a missing product/policy decision blocks safe behavior.

## Verify

Run unit tests for state transitions, UI tests for pending/error/retry/restore semantics, and integration/sandbox tests when configured. Check process recreation/offline/conflict/migration for persistence changes. Review logs and diffs for secrets and sensitive identifiers.

## Accept when

The player sees truthful provider/domain state, cancellation and recovery are defined, actions remain idempotent as required, the trusted authority is unchanged, and the targeted tests pass.

## Evidence boundary

Never claim billing-policy compliance, secure storage, anti-cheat effectiveness, server authority, transaction success, or multiplayer correctness from UI tests. State whether provider sandbox/backend/device testing actually occurred.