package com.droidlogic.launcher.livetv;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class TvPrompt {
    public static final int TV_PROMPT_GOT_SIGNAL              = 0;
    public static final int TV_PROMPT_NO_SIGNAL               = 1;
    public static final int TV_PROMPT_IS_SCRAMBLED            = 2;
    public static final int TV_PROMPT_NO_DEVICE               = 3;
    public static final int TV_PROMPT_SPDIF                   = 4;
    public static final int TV_PROMPT_BLOCKED                 = 5;
    public static final int TV_PROMPT_NO_CHANNEL              = 6;
    public static final int TV_PROMPT_RADIO                   = 7;
    public static final int TV_PROMPT_TUNING                  = 8;
    public static final int TV_PROMPT_DATA_SERVICE            = 9;
    public static final int TV_PROMPT_IS_SKIP                 = 10;
    public static final int TV_PROMPT_IS_DELETE               = 11;

    private TextView mTvPromptView;

    public TvPrompt(TextView view){
        mTvPromptView = view;
    }

    public void setTvPrompt(String text, Drawable background) {
        if(mTvPromptView != null) {
            mTvPromptView.setText(text);
            mTvPromptView.setBackground(background);
        }
    }

    public void setPosition(Rect rect){
        if (mTvPromptView != null) {
            android.widget.FrameLayout.LayoutParams para;
            para = new android.widget.FrameLayout.LayoutParams(rect.width(), rect.height());

            para.setMargins(rect.left, rect.top, 0, 0);
            mTvPromptView.setLayoutParams(para);
        }
    }
}
