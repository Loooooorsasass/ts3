package com.example.game.ui.template

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.awaitDispose
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/*
 * TEMPLATE. Treats "system animator duration scale = 0" as an opt-out from movement.
 * This is a practical signal, not a complete platform-wide reduced-motion API. Combine it with an
 * in-app accessibility preference when the product offers one.
 *
 * The cache provides a quick initial value. The composable also observes the Settings URI while it
 * is in the composition, so a live screen receives refreshes rather than keeping a stale
 * remember(context) value. Activity.onResume may still call refresh() as a defensive resync.
 */
object ReduceMotionCache {
    @Volatile
    private var cached: Boolean? = null

    fun get(context: Context): Boolean {
        cached?.let { return it }
        return read(context).also { cached = it }
    }

    fun refresh(context: Context): Boolean = read(context).also { cached = it }

    private fun read(context: Context): Boolean = runCatching {
        Settings.Global.getFloat(
            context.applicationContext.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }.getOrDefault(false)
}

@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current.applicationContext
    val initial = remember(context) { ReduceMotionCache.get(context) }
    return produceState(initialValue = initial, context) {
        val resolver = context.contentResolver
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                value = ReduceMotionCache.refresh(context)
            }
        }

        try {
            resolver.registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
                false,
                observer,
            )
            // Close the gap between the cached initial value and observer registration.
            value = ReduceMotionCache.refresh(context)
        } catch (_: RuntimeException) {
            // Some devices/policies may deny observing global settings. Keep the best read we have.
            value = ReduceMotionCache.get(context)
        }

        awaitDispose {
            runCatching { resolver.unregisterContentObserver(observer) }
        }
    }.value
}
