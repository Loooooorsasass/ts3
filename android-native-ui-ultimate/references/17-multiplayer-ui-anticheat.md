# 17 — Multiplayer status UI and anti-cheat boundaries

This reference is deliberately limited to player-facing UI and client integration. It is not a networking protocol, matchmaking implementation, server-authority architecture, or anti-cheat system. Do not try to secure a game by changing presentation code.

## Show connection state honestly

Use a state model that distinguishes at least:

- `Connecting`
- `Connected`
- `Reconnecting(attempt, nextRetry)`
- `Disconnected(recoverable)`
- `Queueing(positionOrEstimate?)`
- `MatchFound(acceptanceDeadline)`
- `JoiningMatch`
- `InMatch`
- `Rejected(reasonCode)`
- `MaintenanceOrUnavailable`

Not all products need every state. Do not invent a queue position, ETA, opponent identity, or “server confirmed” claim if the service has not returned it. Render uncertainty explicitly (“Connecting…”, “Reconnecting”, “Could not join”) and offer a safe action such as Retry, Cancel, or Return to Lobby. A timeout should not imply that a purchase, match result, or rank update definitely failed if the operation outcome is unknown; query or reconcile with the service.

## State transitions and stale callbacks

Treat network callbacks as asynchronous and possibly out of order. Tag requests/session attempts with an ID or generation so a late response for an old lobby cannot overwrite the current screen. Cancel client-side jobs when leaving a flow, but do not assume canceling a local coroutine cancels a request already processed by the server. Disable duplicate actions while a non-repeatable request is pending and provide a retry policy for recoverable failures.

Use backoff with jitter for reconnect attempts and avoid a tight retry loop when radio/network is unavailable. Preserve player-entered information where safe. On foreground/resume, reconcile current session state with the service instead of assuming the connection survived backgrounding.

## Rejection, moderation, and integrity messages

Use neutral, factual copy based on a trusted reason code. Distinguish network failure, version mismatch, access restriction, matchmaking timeout, and an integrity/security rejection. Do not expose internal risk signals, sensitive detection thresholds, device fingerprints, raw tokens, or server stack traces. Provide an appeal/support path where the product's policies call for one, without revealing exploit-sensitive implementation details.

Avoid accusatory wording when the cause could be environmental or uncertain. A client-visible error is not the place to expose a full anti-cheat verdict payload. Log only appropriately minimized diagnostics under the application's privacy and retention rules.

## Play Integrity and trust boundaries

Play Integrity can help a backend assess whether requests come from a recognized app/device environment, but it does not magically prove that every client-reported game action is honest. The backend must request or receive the integrity token, verify/decode it through the documented secure flow, check the relevant verdicts and request context, and decide what to do. Never ship server credentials to the client or let the UI's own Boolean such as `isVerified = true` grant valuable items or ranking outcomes.

For sensitive outcomes, use server-authoritative validation and idempotent operations. Treat the client as untrusted: it can be modified, disconnected, replay events, or fabricate local UI state. The UI should display only results returned by the authority appropriate to the operation. Official source: [Play Integrity overview](https://developer.android.com/google/play/integrity/overview).

## Match result and rewards

Separate `SubmittingResult`, `ResultConfirmed`, `ResultPending`, and `ResultRejected` when the service may accept or reject a result asynchronously. Do not show a final rank change or award as server-confirmed before confirmation. If offline retry is supported, retain a durable operation ID and let the server deduplicate. If the outcome cannot be determined, tell the player it is being checked rather than asking them to repeat a potentially non-idempotent action blindly.

For real-money or economy-sensitive rewards, keep entitlement and balance mutation on the trusted authority. The UI can animate a reward after confirmed delivery but must not be the ledger.

## Multiplayer accessibility and localization

Announce meaningful status changes without flooding TalkBack with every retry tick. Include both icon and text/state description for connectivity; color alone is insufficient. Keep Retry/Cancel targets comfortably touchable, preserve focus after errors, and make countdowns understandable without relying on animation alone. Localize the full error/reason-code mapping and ensure long translations do not obscure primary actions.

## Verification cases

- Connection succeeds, fails, times out, and returns after app background/foreground.
- Old response arrives after the user has canceled or started a new attempt.
- Duplicate tap/retry does not create duplicate sessions or rewards.
- Matchmaking queue is canceled and re-entered.
- Result submission times out but server outcome is later reconciled.
- Rejection UI does not reveal sensitive server diagnostics.
- TalkBack announces major status changes, but not every progress tick.

Backend responsibilities are outside this skill. Do not implement matchmaking protocol, server-side validation, anti-cheat detection, rate limiting, or authority rules in the UI layer; coordinate with the backend owner and current provider documentation.
