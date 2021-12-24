package com.droidlogic.launcher.leanback.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.droidlogic.launcher.R;

public class BorderEffectLayout extends NoEffectLayout {

    //white border
    private ImageView borderView;

    public BorderEffectLayout(Context context) {
        super(context);
        initView();
    }

    public BorderEffectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        borderView.setBackgroundResource(R.drawable.bg_white_board);
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
        if (i < focusIndex) {
            return i;
        } else if (i < childCount - 1) {
            return focusIndex + childCount - 1 - i;
        } else {
            return focusIndex;
        }
    }

}
