package com.droidlogic.launcher.leanback.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.droidlogic.launcher.R;

public class BorderEffectLayout extends NoEffectLayout {

    //white border
    public static final int EFFECT_SHAPE_RECTANGLE = 0;
    private ImageView borderView;
    private final int effectShape;

    public BorderEffectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BorderEffectLayout);
        effectShape = typedArray.getInt(R.styleable.BorderEffectLayout_effectShape, EFFECT_SHAPE_RECTANGLE);
        typedArray.recycle();
        initView();
    }

    private void initView() {
        //modify view draw order
        setChildrenDrawingOrderEnabled(true);
        //add border
        addBorder();
    }

    private void addBorder() {
        borderView = new ImageView(getContext());
        borderView.setDuplicateParentStateEnabled(true);
        borderView.setBackgroundResource(effectShape == EFFECT_SHAPE_RECTANGLE ? R.drawable.bg_white_board_rectangle : R.drawable.bg_white_board_oval);
        LayoutParams pms = new LayoutParams(0, 0);
        pms.leftToLeft = 0;
        pms.topToTop = 0;
        pms.rightToRight = 0;
        pms.bottomToBottom = 0;
        addView(borderView, pms);
    }

    @Override
    public int getChildDrawingOrder(int childCount, int i) {
        if (null == borderView) {
            return super.getChildDrawingOrder(childCount, i);
        }
        int position = indexOfChild(borderView);
        if (position < 0) {
            return super.getChildDrawingOrder(childCount, i);
        }
        if (i == childCount - 1) {
            return position;
        }
        if (i == position) {
            return childCount - 1;
        }
        return super.getChildDrawingOrder(childCount, i);
    }

}
