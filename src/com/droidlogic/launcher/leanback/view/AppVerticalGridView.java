package com.droidlogic.launcher.leanback.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.leanback.widget.VerticalGridView;

public class AppVerticalGridView extends VerticalGridView {

    private int mNumColumns;

    public AppVerticalGridView(Context context) {
        this(context, null);
    }

    public AppVerticalGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppVerticalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setColumnNumbers(int numColumns) {
        this.mNumColumns = numColumns;
        setNumColumns(numColumns);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mNumColumns == 0 || getAdapter() == null) {
                        break;
                    }
                    int selectedPosition = getSelectedPosition();
                    int itemCount = getAdapter().getItemCount();
                    if ((selectedPosition + 1) % mNumColumns == 0 && selectedPosition != itemCount - 1) {
                        setSelectedPosition(selectedPosition + 1);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mNumColumns == 0 || getAdapter() == null) {
                        break;
                    }
                    selectedPosition = getSelectedPosition();
                    if (selectedPosition % mNumColumns == 0 && selectedPosition != 0) {
                        setSelectedPosition(selectedPosition - 1);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mNumColumns == 0 || getAdapter() == null) {
                        break;
                    }
                    itemCount = getAdapter().getItemCount();
                    if (itemCount + 1 < mNumColumns || itemCount % mNumColumns == 0) {//no need deal
                        break;
                    }
                    selectedPosition = getSelectedPosition();
                    int lineNumbers = itemCount / mNumColumns + 1;
                    int currentLine;
                    if (selectedPosition + 1 % mNumColumns == 0) {
                        currentLine = selectedPosition / mNumColumns;
                    } else {
                        currentLine = selectedPosition / mNumColumns + 1;
                    }
                    if ((currentLine == lineNumbers - 1)//penultimate line
                            && ((selectedPosition + 1 + mNumColumns) > itemCount)) {
                        setSelectedPositionSmooth(itemCount - 1);
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
