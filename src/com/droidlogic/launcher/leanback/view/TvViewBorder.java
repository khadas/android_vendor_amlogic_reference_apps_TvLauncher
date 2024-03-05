package com.droidlogic.launcher.leanback.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.livetv.TvConfig;

public class TvViewBorder extends BorderEffectLayout {
    public TvViewBorder(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (new TvConfig(getContext()).needPreviewFeature()) {
            setBackgroundColor(Color.BLACK);
        } else {
            setBackgroundResource(R.drawable.tv_holder);
        }
    }
}
