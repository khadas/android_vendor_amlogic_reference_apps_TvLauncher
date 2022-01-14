
package com.droidlogic.launcher.leanback.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.droidlogic.launcher.R;

public class NoEffectLayout extends FrameLayout {

    private ScaleAnimation gainFocusAnimation;
    private ScaleAnimation lostFocusAnimation;
    private boolean enableAnim = true;

    public NoEffectLayout(Context context) {
        super(context);
        initView();
    }

    public NoEffectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NoEffectLayout);
        enableAnim = typedArray.getBoolean(R.styleable.NoEffectLayout_animEnable, true);
        typedArray.recycle();
        initView();
    }

    private void initView() {
        setFocusable(true);
        //default anim
        gainFocusAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.gain_focus_anim);
        lostFocusAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.lose_focus_anim);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        clearAnimation();
        if (gainFocus) {
            if (enableAnim) {
                startAnimation(gainFocusAnimation);
            }
        } else {
            if (enableAnim) {
                startAnimation(lostFocusAnimation);
            }
        }
    }

}
