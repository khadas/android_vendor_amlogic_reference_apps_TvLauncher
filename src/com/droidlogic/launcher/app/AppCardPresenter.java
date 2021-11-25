package com.droidlogic.launcher.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.main.BaseCardPresenter;


public class AppCardPresenter extends BaseCardPresenter {

    private int CARD_WIDTH  = 313;
    private int CARD_HEIGHT = 176;

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setMainImageDimensions(CARD_WIDTH,CARD_HEIGHT);
        cardView.setMainImageScaleType(ImageView.ScaleType.CENTER_INSIDE);

        AppModel app = (AppModel) item;
        cardView.getMainImageView().setImageDrawable(app.getIcon());
        cardView.setTitleText(app.getName());
    }

}
