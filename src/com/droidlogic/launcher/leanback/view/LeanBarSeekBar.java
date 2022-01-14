package com.droidlogic.launcher.leanback.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.droidlogic.launcher.util.DensityTool;

public class LeanBarSeekBar extends View {

    private final Paint paint = new Paint();
    private final RectF rect = new RectF();
    private final int seekBarHeight;
    private int progress;
    private int max;

    public LeanBarSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        seekBarHeight = DensityTool.dp2px(20);
    }

    public void update(int progress, int max) {
        this.progress = progress;
        this.max = max;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.setEmpty();
        rect.left = 0;
        rect.right = w;
        paint.setStrokeWidth(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect.top = progress * 1f / max * (getHeight() - seekBarHeight-getWidth());
        rect.bottom = rect.top + seekBarHeight;
        float left=rect.left+getWidth()/2f;
        float offset=getWidth()/2f;
        canvas.drawLine(left, rect.top+offset,left, rect.bottom+offset, paint);
    }
}
