package com.droidlogic.launcher.leanback.view;

import android.content.Context;
import androidx.leanback.widget.HorizontalGridView;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

public class OrderHorizontalGridView extends HorizontalGridView {

    public OrderHorizontalGridView(Context context) {
        this(context, null);
    }

    public OrderHorizontalGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderHorizontalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    public int getChildDrawingOrder(int childCount, int i) {
        final LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final View focusedChild = getFocusedChild();
            return orderChildDrawing(childCount, i, focusedChild);
        }
        return super.getChildDrawingOrder(childCount, i);
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
