package com.example.game.ui.template.views;

import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.Locale;

/**
 * TEMPLATE: counts a TextView to a new value.
 * - The new value must already be the authoritative final value; this class only renders.
 * - Call cancel() from onDestroyView()/onDestroy() so the animator never outlives the screen.
 * - TalkBack receives the final compact value immediately through the content description.
 *   Replace with a full-precision description if the compact form loses needed detail.
 * - Reduced motion: shows the final value immediately.
 */
public final class CountUp {
    private static final long DURATION_MS = 600L;

    private final TextView view;
    private final Locale locale;
    private ValueAnimator animator;
    private long current;

    public CountUp(TextView view, long initialValue) {
        this.view = view;
        this.locale = Locale.getDefault();
        view.setFontFeatureSettings("tnum"); // effective only if the font has tabular figures
        view.setContentDescription(GameNumberFormat.compact(initialValue, locale));
        show(initialValue);
    }

    public void setValue(final long newValue) {
        cancel();
        view.setContentDescription(GameNumberFormat.compact(newValue, locale));
        if (newValue == current || MotionPrefs.isReduced(view.getContext())) {
            show(newValue);
            return;
        }
        final long from = current;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(DURATION_MS);
        animator.setInterpolator(new DecelerateInterpolator(2f));
        animator.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            show(f >= 1f ? newValue : from + Math.round((newValue - from) * (double) f));
        });
        animator.start();
    }

    public void cancel() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    private void show(long value) {
        current = value;
        view.setText(GameNumberFormat.compact(value, locale));
    }
}
