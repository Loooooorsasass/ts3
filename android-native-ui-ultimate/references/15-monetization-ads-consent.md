# 15 — Monetization, ads, and consent UI

This reference covers UI state, lifecycle, messaging, and safe hand-off to platform SDKs. It is not a complete Billing, advertising, privacy-law, or Play Console integration. Read current official documentation and confirm dependency and policy requirements before implementing a live product.

## Model purchase UI as a state machine

Do not represent the entire purchase flow with a single Boolean such as `isPurchasing`. A useful UI model distinguishes at least:

- `LoadingProducts`
- `Ready(products)`
- `LaunchingPurchase(productId)`
- `Pending(productId)`
- `Verifying(productId)`
- `Success(entitlement)`
- `Cancelled`
- `Unavailable` / `Error(retryable, message)`
- `Restoring` / `RestoreComplete`

The UI should render state from the owning state holder. Prevent duplicate purchase launches, disable only the controls that cannot currently be used, preserve navigation/cancel behavior, and provide retry actions when an error is recoverable. Do not treat the user's tap on Buy as proof that a transaction succeeded.

The purchase flow is generally: show products, launch Play's purchase sheet, observe the result, verify the purchase, grant the entitlement, then acknowledge or consume it as required. Official guide: [Integrate Play Billing](https://developer.android.com/google/play/billing/integrate).

### Pending and restore are first-class states

A purchase in `PENDING` must not grant the paid item. Render a truthful message such as “Payment pending” and re-check the purchase when Play reports a state change. Query current purchases when the app resumes or re-enters the relevant purchase flow, so completion while the app was backgrounded is not missed. Keep restore/reconciliation separate from a new purchase action and make entitlement application idempotent.

If the app has a secure backend, follow Google's recommended server-side verification and entitlement storage where appropriate. Never put service-account credentials, private keys, or purchase-verification secrets in the client. Check current acknowledgement/consumption requirements and deadlines from the live documentation; don't hard-code policy assumptions into a generic UI template.

## UI state table

| Event/state | UI response | Must not do |
|---|---|---|
| Product list loading | Progress and stable layout | Display fabricated prices or tap-ready purchase controls |
| Purchase sheet launched | Prevent duplicate launch; preserve cancel route | Grant product immediately |
| User cancels | Return to shop with selection preserved | Show a payment failure as if charged |
| Purchase pending | Explain pending status; keep entitlement locked | Grant items or acknowledge as completed |
| Verifying | Show non-blocking progress where possible | Allow a repeated purchase to bypass verification |
| Verified and applied | Confirm success and update entitlement UI | Credit the same purchase twice |
| Network/SDK failure | Explain retry and preserve context | Lose the selected product or expose raw stack traces |
| Restore has no items | Say no restorable purchases were found | Imply a new charge occurred |

## Rewarded ads

Treat an ad as an external asynchronous activity. Model loading, ready, showing, reward earned, dismissed, and failed states explicitly. Provide a fallback when no ad is available and never make an ad mandatory for an essential action unless the product design and policy allow it.

Grant an in-app reward only from the SDK's documented reward-earned callback, not merely because the ad was shown or dismissed. Deduplicate rewards with a stable session/reward identifier where applicable, and use server-side verification for server-authoritative value or abuse-sensitive rewards. Clear stale ad objects after use and avoid launching an ad if the Activity is finishing or the screen has left the valid flow.

Use test ad unit IDs during development. Follow the current SDK's callback/thread requirements and keep SDK calls in the expected thread. Official guide: [Rewarded ads](https://developers.google.com/admob/android/rewarded).

## Consent and privacy choices

Use the current User Messaging Platform guidance when it applies to the product and markets. Request updated consent information at app launch as the SDK requires, show a form only when indicated, and check the SDK's current permission-to-request-ads result before loading ads. Provide a discoverable privacy options entry point whenever required by the SDK/message configuration; don't bury it behind a purchase screen or make the user hunt for it.

Consent is not a one-time hard-coded Boolean in a UI component. Reflect the SDK-owned state, handle failure/offline paths, and never infer consent from region alone. Test debug geography/test-device workflows without hard-coding debug overrides into production. Official source: [Set up UMP SDK](https://developers.google.com/admob/android/privacy).

## Accessibility and localization

Purchase and reward dialogs need a clear title, price/product label, pending/success/failure copy, focus order, dismiss/cancel semantics, and TalkBack announcement when state changes. Localize prices using store-provided formatted values, not string concatenation or a hard-coded currency symbol. Use plural resources for reward quantity. Avoid countdowns or motion that hides an important purchase status, and keep errors actionable.

## Testing and scope

Test cancel, duplicate taps, pending-to-purchased, pending-to-canceled, process/activity recreation, offline verification, restore, missing product metadata, ad unavailable, ad dismissed without reward, reward callback fired once, and consent-required/consent-not-required/error states. Use Play Billing test setups and ad test units, never live purchases or live ad units for ordinary development tests.

These examples are UI integration guidance only. The final entitlement, verification, privacy compliance, and anti-fraud policy must be reviewed against the current provider documentation and the app's backend/security design.
