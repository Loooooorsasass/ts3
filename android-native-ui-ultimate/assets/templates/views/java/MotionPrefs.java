package com.example.game.ui.template.views;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

/**
 * TEMPLATE. Uses "system animator duration scale = 0" as a practical reduced-motion signal.
 * The per-process cache is only a fast initial read; live Views should observe changes while
 * attached. Call refresh(context) from Activity.onResume as a defensive resync if appropriate.
 */
public final class MotionPrefs {
    public interface Listener {
        void onChanged(boolean reduceMotion);
    }

    private static volatile Boolean cached;

    private MotionPrefs() {}

    public static boolean isReduced(Context context) {
        Boolean value = cached;
        if (value == null) return refresh(context);
        return value;
    }

    public static boolean refresh(Context context) {
        boolean value = read(context);
        cached = value;
        return value;
    }

    /** Register while the owning View is attached; always pair with stopObserving(). */
    public static ContentObserver observe(Context context, Listener listener) {
        Context appContext = context.getApplicationContext();
        ContentObserver observer = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                listener.onChanged(refresh(appContext));
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                appContext.getContentResolver().registerContentObserver(
                        Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
                        false,
                        observer);
            } catch (RuntimeException ignored) {
                // Settings observation can be restricted on some builds; callers still have refresh().
            }
        }
        return observer;
    }

    public static void stopObserving(Context context, ContentObserver observer) {
        if (observer == null) return;
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        try {
            resolver.unregisterContentObserver(observer);
        } catch (IllegalArgumentException ignored) {
            // No-op if registration was not allowed or has already been removed.
        }
    }

    private static boolean read(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return false;
        try {
            float scale = Settings.Global.getFloat(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1f);
            return scale == 0f;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
