package com.example.game.ui.template.views;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * TEMPLATE: quick scale feedback for non-button views (cards, icon buttons, slots).
 * Replaces any existing OnTouchListener on the view and never consumes the event (returns false),
 * so click handling and accessibility actions keep working. For SpringAnimation-based feedback,
 * check that androidx.dynamicanimation is already a dependency.
 */
public final class PressFeedback {
    private static final float PRESSED_SCALE = 0.96f;
    private static final long PRESS_MS = 70L;
    private static final long RELEASE_MS = 180L;

    private PressFeedback() {}

    @SuppressLint("ClickableViewAccessibility")
    public static void attach(View view) {
        view.setOnTouchListener((v, event) -> {
            final boolean reduced = MotionPrefs.isReduced(v.getContext());
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().cancel();
                    v.animate()
                            .scaleX(PRESSED_SCALE)
                            .scaleY(PRESSED_SCALE)
                            .setDuration(reduced ? 0L : PRESS_MS)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().cancel();
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(reduced ? 0L : RELEASE_MS)
                            .setInterpolator(new OvershootInterpolator(2f))
                            .start();
                    break;
                default:
                    break;
            }
            return false;
        });
    }
}
