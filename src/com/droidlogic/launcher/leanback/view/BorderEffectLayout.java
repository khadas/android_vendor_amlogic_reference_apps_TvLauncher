package com.droidlogic.launcher.leanback.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
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
        addView(borderView);
    }

    @Override
    public int getChildDrawingOrder(int childCount, int i) {
        return orderChildDrawing(childCount, i, borderView);
    }

    private int orderChildDrawing(int childCount, int i, View focusedChild) {
        if (focusedChild == null) {
            return i;
        }
        int focusIndex = indexOfChild(focusedChild);
        if (i == focusIndex) {
            return childCount - 1;
        } else if (i < focusIndex) {
            return i;
        } else {
            return i - 1;
        }
    }

}
