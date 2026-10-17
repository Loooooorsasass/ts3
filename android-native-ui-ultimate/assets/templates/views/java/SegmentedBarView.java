package com.example.game.ui.template.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;

/**
 * TEMPLATE: segmented progress/health bar with a damage trail.
 *
 * - setFraction() receives the authoritative value (0..1) from the state owner; this view only renders.
 * - The last target always wins: rapid changes cancel the previous animation.
 * - No allocation in onDraw (Paints and RectF are reused).
 * - Set a content description from a string resource, for example android:contentDescription.
 *   Pair the bar with a numeric label or icon so low state is not communicated by color alone.
 * - Placeholder colors below match the Candy Cartoon example; call setColors() with project tokens.
 */
public class SegmentedBarView extends View {
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF segRect = new RectF();

    private final float gapPx;
    private int segments = 10;
    private float lowThreshold = 0.25f;

    private float target = 1f;
    private float front = 1f;
    private float ghost = 1f;
    private ValueAnimator frontAnim;
    private ValueAnimator ghostAnim;
    private boolean reduceMotion;
    private ContentObserver motionObserver;
    private String normalStateDescription;
    private String lowStateDescription;

    public SegmentedBarView(Context context) {
        this(context, null);
    }

    public SegmentedBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentedBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gapPx = dp(3f);
        setColors(0xFF1B1D4A, 0xFFFFE7A3, 0xFF5CD65C, 0xFFFF5468);
        reduceMotion = MotionPrefs.isReduced(context);
    }

    public void setColors(int track, int trail, int fill, int low) {
        trackPaint.setColor(track);
        trailPaint.setColor(trail);
        fillPaint.setColor(fill);
        lowPaint.setColor(low);
        invalidate();
    }

    public void setSegments(int count) {
        segments = Math.max(1, count);
        invalidate();
    }

    public void setLowThreshold(float threshold) {
        lowThreshold = Math.max(0f, Math.min(1f, threshold));
        invalidate();
    }

    /**
     * Supply localized strings. AccessibilityNodeInfo stateDescription is available on API 30+;
     * on older versions, pair this bar with a visible localized warning icon/label as in the sample.
     */
    public void setStateDescriptions(String normalDescription, String lowDescription) {
        normalStateDescription = normalDescription;
        lowStateDescription = lowDescription;
        invalidate();
        if (isAttachedToWindow()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
        }
    }

    public void setFraction(float fraction) {
        final float next = Math.max(0f, Math.min(1f, fraction));
        if (next == target) return;
        cancelAnimators();
        final boolean wasLow = isLow(target);
        final boolean damage = next < front;
        target = next;
        if (wasLow != isLow(target) && isAttachedToWindow()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
        }
        if (reduceMotion || !isAttachedToWindow()) {
            front = next;
            ghost = next;
            invalidate();
            return;
        }
        if (damage) {
            frontAnim = animate(front, next, 120L, 0L, true);
            ghostAnim = animate(ghost, next, 450L, 350L, false);
        } else {
            frontAnim = animate(front, next, 300L, 0L, true);
            ghostAnim = animate(ghost, next, 300L, 0L, false);
        }
    }

    private ValueAnimator animate(float from, float to, long duration, long delay, final boolean isFront) {
        ValueAnimator a = ValueAnimator.ofFloat(from, to);
        a.setDuration(duration);
        a.setStartDelay(delay);
        a.setInterpolator(new DecelerateInterpolator());
        a.addUpdateListener(anim -> {
            float v = (float) anim.getAnimatedValue();
            if (isFront) front = v; else ghost = v;
            invalidate();
        });
        a.start();
        return a;
    }

    private void cancelAnimators() {
        if (frontAnim != null) { frontAnim.cancel(); frontAnim = null; }
        if (ghostAnim != null) { ghostAnim.cancel(); ghostAnim = null; }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        reduceMotion = MotionPrefs.refresh(getContext());
        motionObserver = MotionPrefs.observe(getContext(), this::setReducedMotion);
    }

    private void setReducedMotion(boolean enabled) {
        if (reduceMotion == enabled) return;
        reduceMotion = enabled;
        if (enabled) {
            // Never leave an in-flight animation frozen halfway when motion is disabled.
            cancelAnimators();
            front = target;
            ghost = target;
            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        MotionPrefs.stopObserving(getContext(), motionObserver);
        motionObserver = null;
        cancelAnimators();
        // Jump to the final state so a reattached view is never stuck mid-animation.
        front = target;
        ghost = target;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int w = (int) dp(160f) + getPaddingLeft() + getPaddingRight();
        final int h = (int) dp(16f) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(w, widthMeasureSpec), resolveSize(h, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float w = getWidth() - getPaddingLeft() - getPaddingRight();
        final float h = getHeight() - getPaddingTop() - getPaddingBottom();
        if (w <= 0f || h <= 0f) return;

        final int n = segments;
        final float segW = (w - gapPx * (n - 1)) / n;
        final float radius = h * 0.3f;
        final float top = getPaddingTop();
        final Paint fill = target <= lowThreshold ? lowPaint : fillPaint;

        for (int i = 0; i < n; i++) {
            final float left = getPaddingLeft() + i * (segW + gapPx);
            segRect.set(left, top, left + segW, top + h);
            canvas.drawRoundRect(segRect, radius, radius, trackPaint);
            drawFill(canvas, ghost, i, n, radius, trailPaint);
            drawFill(canvas, front, i, n, radius, fill);
        }
    }

    // Uses segRect set by the caller for the current segment.
    private void drawFill(Canvas canvas, float value, int index, int n, float radius, Paint paint) {
        final float f = Math.max(0f, Math.min(1f, value * n - index));
        if (f <= 0f) return;
        canvas.save();
        canvas.clipRect(segRect.left, segRect.top, segRect.left + segRect.width() * f, segRect.bottom);
        canvas.drawRoundRect(segRect, radius, radius, paint);
        canvas.restore();
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setRangeInfo(createRangeInfo(target * 100f));
        if (Build.VERSION.SDK_INT >= 30) {
            String description = isLow(target) ? lowStateDescription : normalStateDescription;
            if (description != null) info.setStateDescription(description);
        }
    }

    private boolean isLow(float value) {
        return value <= lowThreshold;
    }

    // RangeInfo exists since API 19. obtain() is deprecated from API 33 and the public constructor
    // exists from API 30, so use the constructor where it is available.
    @SuppressWarnings("deprecation")
    private static AccessibilityNodeInfo.RangeInfo createRangeInfo(float percent) {
        if (Build.VERSION.SDK_INT >= 30) {
            return new AccessibilityNodeInfo.RangeInfo(
                    AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_PERCENT, 0f, 100f, percent);
        }
        return AccessibilityNodeInfo.RangeInfo.obtain(
                AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_PERCENT, 0f, 100f, percent);
    }

    private float dp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
