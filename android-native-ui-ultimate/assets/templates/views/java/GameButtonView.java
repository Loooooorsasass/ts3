package com.example.game.ui.template.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;

import androidx.appcompat.widget.AppCompatButton;

/**
 * TEMPLATE: game-style button for Views. Use with style="@style/Widget.Game.Button".
 *
 * - Background/pressed/disabled visuals come from drawable/bg_game_button*.xml.
 * - While pressed (and enabled) the label is drawn shifted down to match the face traveling down.
 *   Keep PRESS_SHIFT_DP equal to @dimen/game_button_press_shift.
 * - Haptic feedback on click can be disabled with setHapticOnClick(false) (for example from an in-app setting).
 * - Extends AppCompatButton so it is not swapped for MaterialButton under Material Components themes.
 *   Use it together with Widget.Game.Button: that style clears the backgroundTint which a Material
 *   buttonStyle would otherwise apply over the layer-list background. Not compiled or device-tested;
 *   check the button's colors under your actual theme.
 *   If the project has no AppCompat dependency, extend android.widget.Button and adjust the constructor.
 */
public class GameButtonView extends AppCompatButton {
    private static final float PRESS_SHIFT_DP = 4f;

    private final float pressShiftPx;
    private boolean hapticOnClick = true;

    public GameButtonView(Context context) {
        this(context, null);
    }

    public GameButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.buttonStyle);
    }

    public GameButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pressShiftPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PRESS_SHIFT_DP, getResources().getDisplayMetrics());
    }

    public void setHapticOnClick(boolean enabled) {
        hapticOnClick = enabled;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isPressed() && isEnabled()) {
            canvas.save();
            canvas.translate(0f, pressShiftPx);
            super.onDraw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    public boolean performClick() {
        if (hapticOnClick && isEnabled()) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
        return super.performClick();
    }
}
