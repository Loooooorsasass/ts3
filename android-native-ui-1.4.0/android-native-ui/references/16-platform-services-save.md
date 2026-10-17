# 16 — Platform services, sign-in, achievements, and save data

Keep platform-service integration behind a small repository/service boundary. UI components should render the operation state, dispatch user intent, and remain usable when sign-in or network service is unavailable. Do not couple a HUD button directly to a provider SDK singleton.

## Sign-in UI

Model sign-in as states such as `SignedOut`, `SigningIn`, `SignedIn(profile)`, `RecoverableError`, and `Unavailable`. A button click is a request to start the provider flow, not proof of success. Prevent repeated launch, preserve the prior screen, and return focus when the external Activity/dialog closes. If the user declines or cancels, the app should remain in a valid signed-out state unless the feature truly requires authentication.

Use the current Play Games Services Android sign-in documentation for the configured SDK generation. Do not assume a legacy API, account identifier, or consent flow from a sample still applies. Official starting point: [Play Games Services](https://developer.android.com/games/pgs/android/android-overview).

## Achievements and leaderboards

- Show clear loading, success, unavailable, and retry states; do not freeze gameplay while waiting for a remote service.
- Queue noncritical submissions when offline if the provider and product semantics allow it; retry with bounded exponential backoff and jitter.
- Make retries idempotent where possible. For incremental achievements, avoid replaying the same increment after an uncertain timeout unless the API semantics guarantee safe handling.
- Keep local display separate from provider-confirmed state. A local animation may acknowledge the player's action, but do not claim a leaderboard submission succeeded before a result confirms it.
- Batch or debounce high-frequency events. Never submit every frame of score change.
- Preserve a useful offline experience, and make clear whether a value is local, pending sync, or confirmed remotely when that distinction affects player expectations.

Use the platform's documented achievement and leaderboard clients rather than scraping or simulating provider screens. For operation/client details see [PlayGames API reference](https://developers.google.com/android/reference/com/google/android/gms/games/PlayGames).

## Save ownership and offline behavior

Choose one explicit source-of-truth policy for the game. For an offline-first single-player game, a local durable save can be authoritative on that device; if cloud save is enabled, define a merge/conflict strategy rather than silently assuming local always wins. Competitive or server-authoritative values must not be decided by an editable client save.

Persist domain data, not rendered UI state alone. Use a versioned schema and migration path. Treat missing, truncated, corrupt, too-new, or partially written save files as expected recovery cases. Never crash the home screen because a save cannot be parsed; offer recovery or a safe default while preserving the original file for diagnostics where appropriate.

### Atomicity and write strategy

Prefer a storage solution with transactional semantics, such as Room for relational game data or DataStore for appropriately structured settings/state. For a custom file format, write a complete temporary file in the same filesystem, flush/sync according to the durability requirements, then atomically replace the previous file where supported. Do not overwrite the only good save while serializing the new version. Keep I/O off the main thread and debounce repeated progress updates without risking loss of essential milestones.

Store a schema version and migrate step by step. Validate ranges and invariants after decoding. If migration fails, do not endlessly retry a destructive rewrite; surface a recoverable error and retain a backup. Avoid storing secrets, authentication tokens, or sensitive server credentials in an ordinary game save.

## Cloud/local conflict policy

Possible policies include server version, latest validated timestamp, highest verified progression, or explicit user choice. None is universally correct. Timestamps alone can be wrong because device clocks can drift. Record schema/version and server revision if supported; treat duplicates and retries as normal distributed-system conditions. Do not overwrite a newer valid save with a stale offline copy without a documented rule.

For save selection/restore UI, show meaningful slots/profiles, last-known save time with timezone-aware formatting, local/cloud status, and the consequence of replacing a save. Confirm destructive replacement. Never label a save “cloud synced” until confirmation from the relevant service has succeeded.

## Lifecycle and failure states

Test: app process death during/after save, low-storage errors, app upgrade schema migration, corrupted payload, offline start, network loss during sync, two devices with conflicting progress, sign-out, and sign-in to a different account. The UI should distinguish unsaved local progress, pending upload, sync conflict, and confirmed sync when users could otherwise lose data.

Use `onTrimMemory()` and lifecycle callbacks for releasing reclaimable caches, not as the sole trigger for durable save operations. Save at meaningful checkpoints and by an explicit durability policy, not only at Activity destruction: Android may terminate the process without a final callback.

## Verification checklist

- Sign-in cancel/failure leaves navigation and focus consistent.
- Achievements/leaderboards retry without duplicate increments or false success messaging.
- Local save migration is deterministic and tested from representative older fixtures.
- A failed write preserves the last known good save.
- Cloud conflict behavior is explicit and tested if cloud save exists.
- Sensitive identifiers/tokens are not written to logs or normal save files.

The skill covers UI and client integration patterns. Backend ranking validation, account security, cloud conflict authority, and economy integrity require service-side design outside a UI-only change.
